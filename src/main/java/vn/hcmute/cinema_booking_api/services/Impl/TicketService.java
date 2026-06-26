package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.ticket.TicketHistoryResponse;
import vn.hcmute.cinema_booking_api.entity.Ticket;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.TicketRepository;
import vn.hcmute.cinema_booking_api.repositories.UserRepository;
import vn.hcmute.cinema_booking_api.utils.enums.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public List<TicketHistoryResponse> getMyTickets() {
        User user = getCurrentUser();

        return ticketRepository.findByUserUserIdOrderByShowTimeDateTimeDesc(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TicketHistoryResponse> getTicketsByOrder(Long orderId) {
        User user = getCurrentUser();

        List<Ticket> tickets = ticketRepository.findByOrderOrderIdAndUserUserId(
                orderId,
                user.getUserId()
        );

        if (tickets.isEmpty()) {
            throw new BadRequestException("Tickets not found");
        }

        return tickets.stream()
                .map(this::toResponse)
                .toList();
    }

    private TicketHistoryResponse toResponse(Ticket ticket) {
        return TicketHistoryResponse.builder()
                .ticketId(ticket.getTicketId())
                .ticketCode(ticket.getTicketCode())
                .qrToken(ticket.getQrToken())
                .orderId(ticket.getOrder().getOrderId())
                .movieTitle(ticket.getMovieTitle())
                .roomName(ticket.getRoomName())
                .seatCode(ticket.getSeatCode())
                .showTimeDateTime(ticket.getShowTimeDateTime())
                .price(ticket.getPrice())
                .status(resolveStatus(ticket))
                .build();
    }

    private TicketStatus resolveStatus(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.USED) {
            return TicketStatus.USED;
        }

        if (ticket.getStatus() == TicketStatus.VALID
                && ticket.getShowTimeDateTime().isBefore(LocalDateTime.now())) {
            return TicketStatus.EXPIRED;
        }

        return ticket.getStatus();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}