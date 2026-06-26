package vn.hcmute.cinema_booking_api.dto.seat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatHoldRequest {
    @NotNull(message = "Showtime id must not be null")
    Long showtimeId;

    @NotEmpty(message = "Seat ids must not be empty")
    List<Long> seatIds;
}