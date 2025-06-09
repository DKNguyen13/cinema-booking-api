package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.SeatDTO;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.repository.SeatRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

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
                .map(seat -> new SeatDTO(seat.getSeatCode()))
                .collect(Collectors.toList());
    }

    public List<SeatDTO> getNotAvailableSeatsByShowTimeId(Long showTId){
        return seatRepository.findNotAvailableSeatByShowTimeId(showTId).stream()
                .map(seat -> new SeatDTO(seat.getSeatCode()))
                .collect(Collectors.toList());
    }
}
