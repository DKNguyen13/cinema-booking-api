package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.cinema_booking_api.dto.TicketHoldInfoDTO;
import vn.hcmute.cinema_booking_api.dto.request.TicketHoldRequest;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.services.Impl.TicketHoldService;
import vn.hcmute.cinema_booking_api.services.Impl.TicketService;

@RestController
@RequestMapping("/api/v1")
public class TicketController {
    @Autowired
    private TicketHoldService ticketHoldService;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/hold-seat")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> hold(@RequestBody TicketHoldRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) {
            throw new AccessDeniedException("You are not logged in");
        }
        TicketHoldInfoDTO info = ticketService.mapTicketHoldInfo(email, request);

        boolean held = ticketHoldService.holdTicket(info);
        if (!held) return ResponseEntity.status(409).body(ApiResponse.error(409, "Seat is holding"));
        return ResponseEntity.ok(ApiResponse.success("Successfully hold seat 5 minutes"));
    }
}
