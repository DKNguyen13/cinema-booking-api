package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.TicketHoldInfoDTO;
import vn.hcmute.cinema_booking_api.dto.request.BookingRequest;
import vn.hcmute.cinema_booking_api.dto.response.BookingResponse;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.exception.ResourceNotFoundException;
import vn.hcmute.cinema_booking_api.repository.*;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookedSeatRepository bookedSeatRepository;

    @Autowired
    private TicketHoldService ticketHoldService;

    @Autowired
    private MailService mailService;

    @Transactional
    public BookingResponse checkout(String email, BookingRequest request) {
        // 1. Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        // 2. Get Showtime
        ShowTime showTime = showTimeRepository.findByShowtimeId(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found: " + request.getShowtimeId()));

        if (showTime.getShowTime().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Cannot book tickets for past showtimes.");
        }

        // 3. Process seats
        List<Seat> seatsToBook = new ArrayList<>();
        for (String seatCode : request.getSeatCodes()) {
            Seat seat = seatRepository.findSeatBySeatCode(seatCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat code not found: " + seatCode));

            // Check if already booked in DB
            if (bookedSeatRepository.existsBySeatSeatIdAndShowTimeShowtimeId(seat.getSeatId(), showTime.getShowtimeId())) {
                throw new BadRequestException("Seat " + seatCode + " is already booked for this showtime.");
            }

            // Check Redis hold
            TicketHoldInfoDTO holdInfo = ticketHoldService.getHeldTicket(seat.getSeatId(), showTime.getShowtimeId());
            if (holdInfo != null && !holdInfo.getEmail().equalsIgnoreCase(email)) {
                throw new BadRequestException("Seat " + seatCode + " is currently held by another user.");
            }

            seatsToBook.add(seat);
        }

        // 4. Calculate price
        int baseTicketPrice = showTime.getMovie().getPrice();
        int totalOriginalPrice = baseTicketPrice * seatsToBook.size();
        int finalPrice = totalOriginalPrice;

        // 5. Apply Discount if any
        Discount discount = null;
        if (request.getDiscountCode() != null && !request.getDiscountCode().trim().isEmpty()) {
            discount = discountRepository.findByDiscountCode(request.getDiscountCode().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount code not found: " + request.getDiscountCode()));

            if (!Boolean.TRUE.equals(discount.getActive()) ||
                    discount.getStartDate().isAfter(LocalDate.now()) ||
                    discount.getEndDate().isBefore(LocalDate.now()) ||
                    (discount.getQuantity() != null && discount.getQuantity() <= 0)) {
                throw new BadRequestException("Discount code is inactive, expired, or out of stock.");
            }

            // Calculate discount amount
            int discountAmount = 0;
            if (discount.getFixedAmount() != null && discount.getFixedAmount() > 0) {
                discountAmount = discount.getFixedAmount();
            } else if (discount.getPercentage() != null && discount.getPercentage() > 0) {
                discountAmount = (totalOriginalPrice * discount.getPercentage()) / 100;
            }

            finalPrice = Math.max(0, totalOriginalPrice - discountAmount);

            // Deduct discount quantity
            if (discount.getQuantity() != null) {
                discount.setQuantity(discount.getQuantity() - 1);
                if (discount.getQuantity() == 0) {
                    discount.setActive(false);
                }
                discountRepository.save(discount);
            }
        }

        // 6. Create Order
        Order order = new Order();
        order.setUser(user);
        order.setQuantity(seatsToBook.size());
        order.setPayment(request.getPayment());
        order.setStatus(OrderStatus.SUCCESSFUL);
        order.setFinalPrice(finalPrice);
        order = orderRepository.save(order);

        // 7. Create Tickets and BookedSeats
        List<Ticket> savedTickets = new ArrayList<>();
        List<BookingResponse.TicketDetail> ticketDetails = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Distribute discount proportionally across tickets for record keeping
        double discountFactor = (double) finalPrice / totalOriginalPrice;

        for (Seat seat : seatsToBook) {
            int ticketPrice = (int) Math.round(baseTicketPrice * discountFactor);

            Ticket ticket = new Ticket();
            ticket.setUserEmail(email);
            ticket.setMovieTitle(showTime.getMovie().getTitle());
            ticket.setShowTimeDateTime(showTime.getShowTime());
            ticket.setPrice(ticketPrice);
            ticket.setSeatCode(seat.getSeatCode());
            ticket.setUser(user);
            ticket.setShowtime(showTime);
            ticket.setSeat(seat);
            ticket.setOrder(order);
            ticket = ticketRepository.save(ticket);
            savedTickets.add(ticket);

            // Save BookedSeat in DB
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setSeat(seat);
            bookedSeat.setShowTime(showTime);
            bookedSeatRepository.save(bookedSeat);

            // Release Redis Hold
            ticketHoldService.releaseTicket(seat.getSeatId(), showTime.getShowtimeId());

            ticketDetails.add(BookingResponse.TicketDetail.builder()
                    .ticketId(ticket.getTicketId())
                    .seatCode(seat.getSeatCode())
                    .price(ticketPrice)
                    .movieTitle(showTime.getMovie().getTitle())
                    .showTime(showTime.getShowTime().format(formatter))
                    .build());
        }

        order.setTickets(savedTickets);

        // 8. Send confirmation email
        try {
            mailService.sendBookingConfirmation(email, order);
        } catch (Exception e) {
            // Log mail failure but do not roll back transaction
            e.printStackTrace();
        }

        DateTimeFormatter createdDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return BookingResponse.builder()
                .orderId(order.getOrderId())
                .finalPrice(order.getFinalPrice())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .payment(order.getPayment())
                .createdDate(order.getCreatedDate() != null ? order.getCreatedDate().format(createdDateFormatter) : java.time.LocalDateTime.now().format(createdDateFormatter))
                .tickets(ticketDetails)
                .build();
    }
}
