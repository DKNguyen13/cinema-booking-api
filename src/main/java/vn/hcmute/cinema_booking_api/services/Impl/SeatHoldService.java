package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldRequest;
import vn.hcmute.cinema_booking_api.dto.seat.SeatHoldResponse;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.BookedSeatRepository;
import vn.hcmute.cinema_booking_api.repositories.SeatRepository;
import vn.hcmute.cinema_booking_api.repositories.ShowTimeRepository;
import vn.hcmute.cinema_booking_api.repositories.UserRepository;
import vn.hcmute.cinema_booking_api.services.ISeatHoldService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static vn.hcmute.cinema_booking_api.utils.CONSTANT.HOLD_MINUTES;
import static vn.hcmute.cinema_booking_api.utils.CONSTANT.OWNER_BLOCK_MINUTES;

@Service
@RequiredArgsConstructor
public class SeatHoldService implements ISeatHoldService {
    private final StringRedisTemplate redis;
    private final ShowTimeRepository showTimeRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public SeatHoldResponse holdSeats(SeatHoldRequest request) {
        User user = getCurrentUser();
        validateRequest(request);

        ShowTime showTime = showTimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new BadRequestException("Showtime not found"));

        List<Long> seatIds = request.getSeatIds();
        Long showtimeId = showTime.getShowtimeId();
        Long userId = user.getUserId();

        validateSeatsBelongToRoom(showTime, seatIds);
        validateSeatsNotBooked(showtimeId, seatIds);
        validateSeatsNotBlockedByOwner(showtimeId, seatIds, userId);
        validateSeatsNotCurrentlyHeld(showtimeId, seatIds, userId);

        acquireSeatHolds(showtimeId, seatIds, userId);
        createOwnerBlocks(showtimeId, seatIds, userId);

        return SeatHoldResponse.builder()
                .showtimeId(showtimeId)
                .seatIds(seatIds)
                .holdMinutes(HOLD_MINUTES)
                .expiresAt(LocalDateTime.now().plusMinutes(HOLD_MINUTES))
                .build();
    }

    @Transactional
    @Override
    public void releaseSeats(SeatHoldRequest req){
        User user = getCurrentUser();
        Long showtimeId = req.getShowtimeId();
        Long userId = user.getUserId();
        List<String> keysToDelete = new ArrayList<>();

        for (Long seatId : req.getSeatIds()) {
            String holdKey = buildSeatHoldKey(showtimeId, seatId);

            String currentHolder = redis.opsForValue().get(holdKey);

            if (currentHolder == null) {
                continue;
            }

            if (!currentHolder.equals(String.valueOf(userId))) {
                throw new BadRequestException("You can only release seats held by you");
            }

            keysToDelete.add(holdKey);
        }

        if (!keysToDelete.isEmpty()) {
            redis.delete(keysToDelete);
        }
    }

    @Transactional
    @Override
    public void extendHoldForPayment(Long showtimeId, List<Long> seatIds) {
        User user = getCurrentUser();
        Long userId = user.getUserId();

        for (Long seatId : seatIds) {
            String holdKey = buildSeatHoldKey(showtimeId, seatId);
            String currentHolder = redis.opsForValue().get(holdKey);

            if (currentHolder == null) {
                throw new BadRequestException("Seat hold expired. Please select seats again.");
            }

            if (!currentHolder.equals(String.valueOf(userId))) {
                throw new BadRequestException("Seat is not held by you");
            }
        }

        for (Long seatId : seatIds) {
            String holdKey = buildSeatHoldKey(showtimeId, seatId);
            redis.expire(holdKey, Duration.ofMinutes(10));
        }
    }

    // Helper
    private void validateRequest(SeatHoldRequest request) {
        if (request.getShowtimeId() == null) {
            throw new BadRequestException("Showtime is required");
        }

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new BadRequestException("Seat list is required");
        }

        Set<Long> uniqueSeatIds = new HashSet<>(request.getSeatIds());
        if (uniqueSeatIds.size() != request.getSeatIds().size()) {
            throw new BadRequestException("Duplicate seats are not allowed");
        }
    }

    private void validateSeatsBelongToRoom(ShowTime showTime, List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new BadRequestException("Some seats were not found");
        }

        Long roomId = showTime.getRoom().getRoomId();
        Map<Long, Seat> seatMap = seats.stream().collect(toMap(Seat::getSeatId, identity()));

        for (Long seatId : seatIds) {
            Seat seat = seatMap.get(seatId);

            if (!seat.getRoom().getRoomId().equals(roomId)) {
                throw new BadRequestException("Seat does not belong to this showtime room");
            }
        }
    }

    private void validateSeatsNotBooked(Long showtimeId, List<Long> seatIds) {
        List<Long> bookedSeatIds = bookedSeatRepository
                .findBookedSeatIdsByShowtimeAndSeatIds(showtimeId, seatIds);

        if (!bookedSeatIds.isEmpty()) {
            throw new BadRequestException("Some seats are already booked");
        }
    }

    private void validateSeatsNotBlockedByOwner(Long showtimeId, List<Long> seatIds, Long userId) {
        List<String> ownerKeys = seatIds.stream()
                .map(seatId -> buildSeatHoldOwnerKey(showtimeId, seatId, userId))
                .toList();

        List<String> ownerBlocks = redis.opsForValue().multiGet(ownerKeys);

        if (ownerBlocks == null) return;
        boolean blocked = ownerBlocks.stream().anyMatch(value -> value != null);

        if (blocked) {
            throw new BadRequestException("You cannot hold this seat again immediately after timeout. Please wait a moment.");
        }
    }

    private void validateSeatsNotCurrentlyHeld(Long showtimeId, List<Long> seatIds, Long userId) {
        List<String> holdKeys = seatIds.stream()
                .map(seatId -> buildSeatHoldKey(showtimeId, seatId))
                .toList();

        List<String> currentHolders = redis.opsForValue().multiGet(holdKeys);

        if (currentHolders == null) return;

        for (String currentHolder : currentHolders) {
            if (currentHolder == null) continue;
            if (currentHolder.equals(String.valueOf(userId))) {
                throw new BadRequestException("Seat is already held by you. Please complete booking before it expires.");
            }

            throw new BadRequestException("Seat is being held by another user");
        }
    }

    private void acquireSeatHolds(Long showtimeId, List<Long> seatIds, Long userId) {
        List<String> acquiredKeys = new ArrayList<>();

        try {
            for (Long seatId : seatIds) {
                String holdKey = buildSeatHoldKey(showtimeId, seatId);
                Boolean success = redis.opsForValue().setIfAbsent(holdKey, String.valueOf(userId), Duration.ofMinutes(HOLD_MINUTES));
                if (!Boolean.TRUE.equals(success)) throw new BadRequestException("Seat is being held by another user");
                acquiredKeys.add(holdKey);
            }
        } catch (RuntimeException e) {
            if (!acquiredKeys.isEmpty()) {
                redis.delete(acquiredKeys);
            }
            throw e;
        }
    }

    private void createOwnerBlocks(Long showtimeId, List<Long> seatIds, Long userId) {
        for (Long seatId : seatIds) {
            String ownerKey = buildSeatHoldOwnerKey(showtimeId, seatId, userId);
            redis.opsForValue().set(ownerKey, "1", Duration.ofMinutes(OWNER_BLOCK_MINUTES));
        }
    }

    private String buildSeatHoldKey(Long showtimeId, Long seatId) {
        return "seat_hold:" + showtimeId + ":" + seatId;
    }

    private String buildSeatHoldOwnerKey(Long showtimeId, Long seatId, Long userId) {
        return "seat_hold_owner:" + showtimeId + ":" + seatId + ":" + userId;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}