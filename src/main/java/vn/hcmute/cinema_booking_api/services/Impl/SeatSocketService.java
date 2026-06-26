package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.controllers.Seat.SeatStatusMessage;

@Service
@RequiredArgsConstructor
public class SeatSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSeatStatus(Long showtimeId, Long seatId, String status) {
        SeatStatusMessage message = new SeatStatusMessage(showtimeId, seatId, status);
        messagingTemplate.convertAndSend("/topic/showtimes/" + showtimeId + "/seats", message);
    }
}