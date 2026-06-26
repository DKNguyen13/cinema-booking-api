package vn.hcmute.cinema_booking_api.controllers.Ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.ticket.TicketHistoryResponse;
import vn.hcmute.cinema_booking_api.services.Impl.TicketService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<TicketHistoryResponse>>> getMyTickets() {
        return ResponseEntity.ok(ApiResponse.success("Get my tickets successfully", ticketService.getMyTickets()));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<TicketHistoryResponse>>> getTicketsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Get tickets by order successfully", ticketService.getTicketsByOrder(orderId)));
    }
}