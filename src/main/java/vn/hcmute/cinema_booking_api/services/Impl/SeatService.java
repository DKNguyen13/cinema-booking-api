package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.seat.SeatStatusResponse;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.BookedSeatRepository;
import vn.hcmute.cinema_booking_api.repositories.SeatRepository;
import vn.hcmute.cinema_booking_api.repositories.ShowTimeRepository;
import vn.hcmute.cinema_booking_api.services.ISeatService;
import vn.hcmute.cinema_booking_api.utils.enums.SeatStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService implements ISeatService {
    private final StringRedisTemplate redis;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SeatStatusResponse> getSeatsByShowtime(Long showtimeId) {
        ShowTime showTime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new BadRequestException("Showtime not found"));

        Long roomId = showTime.getRoom().getRoomId();
        List<Seat> seats = seatRepository.findByRoomRoomIdOrderBySeatCodeAsc(roomId);
        // Get booked seat id
        Set<Long> bookedSeatIds = new HashSet<>(bookedSeatRepository.findBookedSeatIdsByShowtimeId(showtimeId));
        Set<Long> heldSeatIds = getHeldSeatIds(showtimeId, seats);

        return seats.stream()
                .map(seat -> {
                    SeatStatus status = resolveSeatStatus(seat.getSeatId(), bookedSeatIds, heldSeatIds);

                    return SeatStatusResponse.builder()
                            .seatId(seat.getSeatId())
                            .seatCode(seat.getSeatCode())
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Helper
    private SeatStatus resolveSeatStatus(Long seatId, Set<Long> bookedSeatIds, Set<Long> heldSeatIds) {
        if (bookedSeatIds.contains(seatId)) {
            return SeatStatus.BOOKED;
        }

        if (heldSeatIds.contains(seatId)) {
            return SeatStatus.HELD;
        }
        return SeatStatus.AVAILABLE;
    }

    private Set<Long> getHeldSeatIds(Long showtimeId, List<Seat> seats) {
        List<String> holdKeys = seats.stream()
                .map(seat -> buildSeatHoldKey(showtimeId, seat.getSeatId()))
                .toList();

        List<String> holders = redis.opsForValue().multiGet(holdKeys);

        Set<Long> heldSeatIds = new HashSet<>();

        if (holders == null) {
            return heldSeatIds;
        }

        for (int i = 0; i < holders.size(); i++) {
            if (holders.get(i) != null) {
                heldSeatIds.add(seats.get(i).getSeatId());
            }
        }

        return heldSeatIds;
    }

    private String buildSeatHoldKey(Long showtimeId, Long seatId) {
        return "seat_hold:" + showtimeId + ":" + seatId;
    }
}