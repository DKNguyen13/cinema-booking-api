package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminRoomResponse {
    private Long roomId;
    private String roomName;
    private Integer totalSeats;
    private Boolean isActive;
}
