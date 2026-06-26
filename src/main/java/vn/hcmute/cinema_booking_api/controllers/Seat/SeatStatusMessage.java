package vn.hcmute.cinema_booking_api.controllers.Seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SeatStatusMessage {
    private Long showtimeId;
    private Long seatId;
    private String status;
}