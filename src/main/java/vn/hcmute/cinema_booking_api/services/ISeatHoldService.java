package vn.hcmute.cinema_booking_api.services;

import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldRequest;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldResponse;

import java.util.List;

public interface ISeatHoldService {
    SeatHoldResponse holdSeats(SeatHoldRequest request);

    @Transactional
    void releaseSeats(SeatHoldRequest req);

    @Transactional
    void extendHoldForPayment(Long showtimeId, List<Long> seatIds);
}
