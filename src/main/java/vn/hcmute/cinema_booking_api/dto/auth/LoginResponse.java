package vn.hcmute.cinema_booking_api.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String email;
    private String fullName;
    private String imgUrl;
    private String token;
}