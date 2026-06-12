package vn.hcmute.cinema_booking_api.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String fullName;

    private String phone;

    private String address;

    private String urlImage;
}