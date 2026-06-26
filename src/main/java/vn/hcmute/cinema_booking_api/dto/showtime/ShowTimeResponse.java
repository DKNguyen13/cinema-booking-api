package vn.hcmute.cinema_booking_api.dto.showtime;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowTimeResponse {
    Long showtimeId;
    LocalDateTime showTime;
    LocalDateTime endTime;

    Long movieId;
    String movieTitle;

    Long roomId;
    String roomName;
}