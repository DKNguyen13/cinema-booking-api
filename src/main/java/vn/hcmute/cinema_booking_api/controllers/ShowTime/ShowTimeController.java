package vn.hcmute.cinema_booking_api.controllers.ShowTime;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.showtime.ShowTimeResponse;
import vn.hcmute.cinema_booking_api.services.IShowTimeService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.time.LocalDate;
import java.util.List;

@RestController("/api/movies")
@RequiredArgsConstructor
public class ShowTimeController {
    private final IShowTimeService showTimeService;

    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<ApiResponse<List<ShowTimeResponse>>> getShowTimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success("Get showtimes by movie successful!", showTimeService.getShowTimesByMovie(movieId)));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<ApiResponse<List<ShowTimeResponse>>> getShowTimesByDate(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Get showtimes by date successful!", showTimeService.getShowTimesByDate(date)));
    }

    @GetMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowTimeResponse>> getShowTimeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Get showtime detail successful!", showTimeService.getShowTimeDetail(id)));
    }
}