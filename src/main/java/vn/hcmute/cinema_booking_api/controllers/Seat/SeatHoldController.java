package vn.hcmute.cinema_booking_api.controllers.Seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldRequest;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldResponse;
import vn.hcmute.cinema_booking_api.services.Impl.SeatHoldService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seat-holds")
public class SeatHoldController {
    private final SeatHoldService seatHoldService;

    @PostMapping
    public ResponseEntity<ApiResponse<SeatHoldResponse>> holdSeats(@Valid @RequestBody SeatHoldRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Seats held successfully", seatHoldService.holdSeats(request)));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> releaseSeats(@Valid @RequestBody SeatHoldRequest request) {
        seatHoldService.releaseSeats(request);
        return ResponseEntity.ok(ApiResponse.success("Seats released successfully", null));
    }

    @PostMapping("/extend-payment")
    public ResponseEntity<ApiResponse<Void>> extendHoldForPayment(@Valid @RequestBody SeatHoldRequest request) {
        seatHoldService.extendHoldForPayment(request.getShowtimeId(), request.getSeatIds());
        return ResponseEntity.ok(ApiResponse.success("Seat hold extended for payment", null));
    }
}