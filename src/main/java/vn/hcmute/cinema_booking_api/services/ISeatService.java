package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.seat.SeatStatusResponse;
import java.util.List;

public interface ISeatService {
    List<SeatStatusResponse> getSeatsByShowtime(Long showtimeId);
}