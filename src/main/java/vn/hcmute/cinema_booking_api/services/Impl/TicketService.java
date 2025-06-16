package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.TicketHoldInfoDTO;
import vn.hcmute.cinema_booking_api.dto.UserDTO;
import vn.hcmute.cinema_booking_api.dto.request.TicketHoldRequest;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.entity.User;

import java.time.format.DateTimeFormatter;

@Service
public class TicketService {

    @Autowired
    private SeatService seatService;

    @Autowired
    private ShowTimeService showTimeService;

    @Autowired
    private UserService userService;

    public TicketHoldInfoDTO mapTicketHoldInfo(String email, TicketHoldRequest request) {
        if(request == null) throw new NullPointerException("info is null");
        Seat seat = seatService.findSeatBySeatCode(request.getSeatCode());
        ShowTime showTime = showTimeService.findShowTimeById(request.getShowtimeId());
        UserDTO u = userService.findByEmail(email);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return TicketHoldInfoDTO.builder()
                .email(email)
                .seatId(seat.getSeatId())
                .showtimeId(request.getShowtimeId())
                .seatCode(request.getSeatCode())
                .movieTitle(showTime.getMovie().getTitle())
                .showTime(showTime.getShowTime().format(formatter))
                .price(showTime.getMovie().getPrice())
                .build();
    }
}
