package vn.hcmute.cinema_booking_api.services.Impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.payment.CreatePaymentRequest;
import vn.hcmute.cinema_booking_api.dto.payment.CreatePaymentResponse;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.*;
import vn.hcmute.cinema_booking_api.services.IMailService;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;
import vn.hcmute.cinema_booking_api.utils.enums.PaymentMethod;
import vn.hcmute.cinema_booking_api.utils.enums.TicketStatus;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static vn.hcmute.cinema_booking_api.utils.CONSTANT.MAX_SEATS;
import static vn.hcmute.cinema_booking_api.utils.CONSTANT.PAYMENT_HOLD_MINUTES;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final StringRedisTemplate redis;
    private final UserRepository userRepository;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TicketRepository ticketRepository;
    private final IMailService mailService;

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${app.frontend-payment-success}")
    private String frontendSuccessUrl;

    @Value("${app.frontend-payment-fail}")
    private String frontendFailUrl;

    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request, HttpServletRequest servletRequest) {
        User user = getCurrentUser();
        validateRequest(request);

        ShowTime showtime = showTimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new BadRequestException("Showtime not found"));

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        validateSeatsBelongToRoom(showtime, seats, request.getSeatIds());
        validateSeatsNotBooked(showtime.getShowtimeId(), request.getSeatIds());
        validateSeatsHeldByUser(showtime.getShowtimeId(), request.getSeatIds(), user.getUserId());

        int price = showtime.getMovie().getPrice();
        int total = price * seats.size();

        Order order = Order.builder()
                .user(user)
                .showtime(showtime)
                .finalPrice(total)
                .quantity(seats.size())
                .status(OrderStatus.PENDING_PAYMENT)
                .payment(PaymentMethod.VNPAY)
                .expiredAt(LocalDateTime.now().plusMinutes(PAYMENT_HOLD_MINUTES))
                .build();

        orderRepository.save(order);

        List<OrderItem> items = seats.stream()
                .map(seat -> OrderItem.builder()
                        .order(order)
                        .seat(seat)
                        .seatCode(seat.getSeatCode())
                        .price(price)
                        .build())
                .toList();

        orderItemRepository.saveAll(items);

        extendSeatHolds(showtime.getShowtimeId(), request.getSeatIds());

        String paymentUrl = buildPaymentUrl(order, servletRequest);

        return CreatePaymentResponse.builder()
                .orderId(order.getOrderId())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Transactional
    public String handleReturn(HttpServletRequest request) {
        Map<String, String> vnpParams = extractParams(request);
        String secureHash = vnpParams.remove("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHashType");

        String signedHash = hashAllFields(vnpParams);

        if (secureHash == null || !secureHash.equalsIgnoreCase(signedHash)) {
            return frontendFailUrl;
        }

        Long orderId = parseOrderId(vnpParams.get("vnp_TxnRef"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            return frontendSuccessUrl + "?orderId=" + order.getOrderId();
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return frontendFailUrl;
        }

        int returnedAmount = Integer.parseInt(vnpParams.getOrDefault("vnp_Amount", "0"));
        int expectedAmount = order.getFinalPrice() * 100;

        boolean success = "00".equals(vnpParams.get("vnp_ResponseCode"))
                && "00".equals(vnpParams.get("vnp_TransactionStatus"))
                && returnedAmount == expectedAmount;

        if (!success) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            releaseOrderHolds(order);
            return frontendFailUrl;
        }

        validateOrderSeatsNotBooked(order);
        createBookedSeats(order);

        List<Ticket> tickets = createTickets(order);
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
        releaseOrderHolds(order);
        mailService.sendTicketEmail(order, tickets);
        return frontendSuccessUrl + "?orderId=" + order.getOrderId();
    }

    private String buildPaymentUrl(Order order, HttpServletRequest request) {
        String createDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Locale", "vn");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        params.put("vnp_OrderInfo", "Thanh toan ve xem phim order " + order.getOrderId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Amount", String.valueOf(order.getFinalPrice() * 100));
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_CreateDate", createDate);

        String secureHash = hashAllFields(params);
        params.put("vnp_SecureHash", secureHash);

        String query = new TreeMap<>(params).entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));

        return vnpPayUrl + "?" + query;
    }

    private String hashAllFields(Map<String, String> fields) {
        String signData = new TreeMap<>(fields).entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));

        return hmacSHA512(vnpHashSecret, signData);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();
        } catch (Exception e) {
            throw new BadRequestException("Cannot create VNPay secure hash");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("%20", "+");
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        return params;
    }

    private void validateRequest(CreatePaymentRequest request) {
        if (request.getShowtimeId() == null) {
            throw new BadRequestException("Showtime is required");
        }

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new BadRequestException("Seat list is required");
        }

        if (request.getSeatIds().size() > MAX_SEATS) {
            throw new BadRequestException("You can select maximum " + MAX_SEATS + " seats");
        }

        Set<Long> uniqueSeatIds = new HashSet<>(request.getSeatIds());
        if (uniqueSeatIds.size() != request.getSeatIds().size()) {
            throw new BadRequestException("Duplicate seats are not allowed");
        }
    }

    private void validateSeatsBelongToRoom(ShowTime showtime, List<Seat> seats, List<Long> seatIds) {
        if (seats.size() != seatIds.size()) {
            throw new BadRequestException("Some seats were not found");
        }

        Long roomId = showtime.getRoom().getRoomId();

        for (Seat seat : seats) {
            if (!seat.getRoom().getRoomId().equals(roomId)) {
                throw new BadRequestException("Seat does not belong to showtime room");
            }
        }
    }

    private void validateSeatsNotBooked(Long showtimeId, List<Long> seatIds) {
        List<Long> bookedSeatIds = bookedSeatRepository
                .findBookedSeatIdsByShowtimeAndSeatIds(showtimeId, seatIds);

        if (!bookedSeatIds.isEmpty()) {
            throw new BadRequestException("Some seats are already booked");
        }
    }

    private void validateSeatsHeldByUser(Long showtimeId, List<Long> seatIds, Long userId) {
        for (Long seatId : seatIds) {
            String holder = redis.opsForValue().get(buildSeatHoldKey(showtimeId, seatId));

            if (holder == null) {
                throw new BadRequestException("Seat hold expired. Please select seats again.");
            }

            if (!holder.equals(String.valueOf(userId))) {
                throw new BadRequestException("Seat is not held by you");
            }
        }
    }

    private void extendSeatHolds(Long showtimeId, List<Long> seatIds) {
        for (Long seatId : seatIds) {
            redis.expire(buildSeatHoldKey(showtimeId, seatId), Duration.ofMinutes(PAYMENT_HOLD_MINUTES));
        }
    }

    private void validateOrderSeatsNotBooked(Order order) {
        List<Long> seatIds = orderItemRepository.findByOrderOrderId(order.getOrderId())
                .stream()
                .map(item -> item.getSeat().getSeatId())
                .toList();

        validateSeatsNotBooked(order.getShowtime().getShowtimeId(), seatIds);
    }

    private void createBookedSeats(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());

        List<BookedSeat> bookedSeats = items.stream()
                .map(item -> BookedSeat.builder()
                        .showTime(order.getShowtime())
                        .seat(item.getSeat())
                        .build())
                .toList();

        bookedSeatRepository.saveAll(bookedSeats);
    }

    private List<Ticket> createTickets(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());
        List<Ticket> tickets = items.stream()
                .map(item -> Ticket.builder()
                        .ticketCode(generateTicketCode(order.getOrderId(), item.getSeat().getSeatId()))
                        .qrToken(UUID.randomUUID().toString())
                        .status(TicketStatus.VALID)
                        .user(order.getUser())
                        .userEmail(order.getUser().getEmail())
                        .showtime(order.getShowtime())
                        .showTimeDateTime(order.getShowtime().getShowTime())
                        .movieTitle(order.getShowtime().getMovie().getTitle())
                        .roomName(order.getShowtime().getRoom().getRoomName())
                        .seat(item.getSeat())
                        .seatCode(item.getSeatCode())
                        .price(item.getPrice())
                        .order(order)
                        .build())
                .toList();

        return ticketRepository.saveAll(tickets);
    }

    private void releaseOrderHolds(Order order) {
        List<String> keys = orderItemRepository.findByOrderOrderId(order.getOrderId())
                .stream()
                .map(item -> buildSeatHoldKey(
                        order.getShowtime().getShowtimeId(),
                        item.getSeat().getSeatId()
                ))
                .toList();

        if (!keys.isEmpty()) {
            redis.delete(keys);
        }
    }

    private String buildSeatHoldKey(Long showtimeId, Long seatId) {
        return "seat_hold:" + showtimeId + ":" + seatId;
    }

    private Long parseOrderId(String txnRef) {
        try {
            return Long.valueOf(txnRef);
        } catch (Exception e) {
            throw new BadRequestException("Invalid VNPay transaction reference");
        }
    }

    private String generateTicketCode(Long orderId, Long seatId) {
        return "TICKET-" + orderId + "-" + seatId + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}