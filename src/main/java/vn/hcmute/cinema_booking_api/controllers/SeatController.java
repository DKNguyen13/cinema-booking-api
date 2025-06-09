package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.cinema_booking_api.dto.SeatDTO;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.services.Impl.SeatService;
import vn.hcmute.cinema_booking_api.services.Impl.ShowTimeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SeatController {
    @Autowired
    private SeatService seatService;
    @Autowired
    private ShowTimeService showTimeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/seats/available")
    public ResponseEntity<?> getAvailableSeats(@RequestParam Long showTimeId){
        try {
            if(!showTimeService.checkExistShowTimeByShowTimeId(showTimeId)){
                return ResponseEntity.badRequest().body(ApiResponse.error(404, "Not found "+ showTimeId));
            }

            List<SeatDTO> list = seatService.getAvailableSeatsByShowtimeId(showTimeId);

            if(list.isEmpty()){
                return ResponseEntity.ok(ApiResponse.success("Not found seats"));
            }
            return ResponseEntity.ok(ApiResponse.success("List of available seats", list));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/seats/not-available")
    public ResponseEntity<?> getNotAvailableSeats(@RequestParam Long showTimeId){
        try {
            if(!showTimeService.checkExistShowTimeByShowTimeId(showTimeId)){
                return ResponseEntity.badRequest().body(ApiResponse.error(404, "Not found "+ showTimeId));
            }

            List<SeatDTO> list = seatService.getNotAvailableSeatsByShowTimeId(showTimeId);

            if(list.isEmpty()){
                return ResponseEntity.ok(ApiResponse.success("Not found seats"));
            }
            return ResponseEntity.ok(ApiResponse.success("List of not available seats", list));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
