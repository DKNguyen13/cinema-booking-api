package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.services.Impl.ShowTimeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/show-times")
public class ShowTimeController {
    @Autowired
    private ShowTimeService showTimeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<?> getShowTimesByMovieId(@RequestParam Long movieId) {
        List<ShowTime> showTimes = showTimeService.findAllShowTimesByMovieId(movieId);
        if(showTimes.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No upcoming showtimes available"));
        }
        return ResponseEntity.ok(ApiResponse.success("Upcoming showtimes retrieved successfully", showTimes));
    }
}
