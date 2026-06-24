package vn.hcmute.cinema_booking_api.dto.seat;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatHoldResponse {
    Long showtimeId;
    List<Long> seatIds;
    Integer holdMinutes;
    LocalDateTime expiresAt;
}