package vn.hcmute.cinema_booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String email;
    private String psw;
    private String fullName;
    private String phone;
    private String addr;
    private String token = "";
}
