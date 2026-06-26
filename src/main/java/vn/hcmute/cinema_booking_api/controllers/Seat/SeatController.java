package vn.hcmute.cinema_booking_api.controllers.Seat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.seat.SeatStatusResponse;
import vn.hcmute.cinema_booking_api.services.ISeatService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/showtimes")
public class SeatController {
    private final ISeatService seatService;

    @GetMapping("/{showtimeId}/seats")
    public ResponseEntity<ApiResponse<List<SeatStatusResponse>>> getSeatsByShowtime(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(ApiResponse.success("Get seats by showtime successful!", seatService.getSeatsByShowtime(showtimeId)));
    }
}