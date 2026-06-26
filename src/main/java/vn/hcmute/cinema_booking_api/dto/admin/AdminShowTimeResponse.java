package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminShowTimeResponse {
    private Long showtimeId;
    private LocalDateTime showTime;
    private LocalDateTime endTime;
    private Long movieId;
    private String movieTitle;
    private Long roomId;
    private String roomName;
}
