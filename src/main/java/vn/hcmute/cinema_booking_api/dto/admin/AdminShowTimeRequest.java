package vn.hcmute.cinema_booking_api.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminShowTimeRequest {
    @NotNull private Long movieId;
    @NotNull private Long roomId;
    @NotNull private LocalDateTime showTime;
}
