package vn.hcmute.cinema_booking_api.dto.admin;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminRoomRequest {
    @NotBlank @Size(max = 100) private String roomName;
    @NotNull @Min(1)           private Integer totalSeats;
}
