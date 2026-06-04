package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.cinema_booking_api.dto.request.BookingRequest;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.dto.response.BookingResponse;
import vn.hcmute.cinema_booking_api.services.Impl.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> checkout(@Validated @RequestBody BookingRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null) {
            throw new AccessDeniedException("You are not logged in");
        }

        try {
            BookingResponse response = bookingService.checkout(email, request);
            return ResponseEntity.ok(ApiResponse.success("Booking completed successfully", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
