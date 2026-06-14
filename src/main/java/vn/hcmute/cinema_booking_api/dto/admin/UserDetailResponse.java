package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Integer point;
    private String imageUrl;
    private Boolean isActive;
    private String roleName;
}
