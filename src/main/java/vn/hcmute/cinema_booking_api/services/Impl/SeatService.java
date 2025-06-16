package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.SeatDTO;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.repository.SeatRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketHoldService ticketHoldService;

    public List<SeatDTO> findAll() {
        List<Seat> seats = seatRepository.findAll();
        return seats.stream()
                .map(m -> {
                    SeatDTO seatDTO = new SeatDTO();
                    seatDTO.setSeatCode(m.getSeatCode());
                    return seatDTO;
                }).collect(Collectors.toList());
    }

    public List<SeatDTO> getAvailableSeatsByShowtimeId(Long showTimeId) {
        return seatRepository.findAvailableSeatsByShowtimeId(showTimeId).stream()
                .filter(seat -> ticketHoldService.getHeldTicket(seat.getSeatId(), showTimeId) == null)
                //String key = "hold:ticket:" + showTimeId + ":" + seat.getSeatId();
                //return !Boolean.TRUE.equals(redisTemplate.hasKey(key));
                .map(seat -> new SeatDTO(seat.getSeatCode()))
                .toList();
    }

    public List<SeatDTO> getNotAvailableSeatsByShowTimeId(Long showTId){
        List<Seat> bookedSeats = seatRepository.findNotAvailableSeatByShowTimeId(showTId);//Seat in BookedSeat

        //Get Seat in redis
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByShowtimeId(showTId);
        List<Seat> heldSeats = availableSeats.stream()
                .filter(seat -> ticketHoldService.getHeldTicket(seat.getSeatId(), showTId) != null)
                .toList();

        return Stream.concat(bookedSeats.stream(), heldSeats.stream())
                .map(seat -> new SeatDTO(seat.getSeatCode()))
                .toList();
    }

    public Seat findSeatBySeatCode(String seatCode) {
        return seatRepository.findSeatBySeatCode(seatCode).orElseThrow(() -> new RuntimeException("Seat Not Found"));
    }
}
