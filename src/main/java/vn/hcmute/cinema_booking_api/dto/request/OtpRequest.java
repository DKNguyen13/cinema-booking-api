package vn.hcmute.cinema_booking_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hcmute.cinema_booking_api.dto.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    private String otpCode;
    private UserDTO infUser;
}

