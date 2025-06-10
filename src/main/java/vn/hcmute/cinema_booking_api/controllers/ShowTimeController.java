package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.ShowTimeDTO;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.services.Impl.ShowTimeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ShowTimeController {
    @Autowired
    private ShowTimeService showTimeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/show-times")
    public ResponseEntity<?> getShowTimesByMovieId(@RequestParam Long movieId) {
        List<ShowTime> showTimes = showTimeService.findAllShowTimesByMovieId(movieId);
        if(showTimes.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No upcoming showtimes available"));
        }
        return ResponseEntity.ok(ApiResponse.success("Upcoming showtimes retrieved successfully", showTimes));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/show-time")
    public ResponseEntity<?> createShow(@RequestBody ShowTimeDTO showTimeDTO){
        return ResponseEntity.ok(ApiResponse.success("Show time created", showTimeService.createShowTime(showTimeDTO)));
    }
}
