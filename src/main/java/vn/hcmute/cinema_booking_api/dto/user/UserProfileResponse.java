package vn.hcmute.cinema_booking_api.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Integer point;
    private String imageUrl;
    private String imagePublicId;
}