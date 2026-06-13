package vn.hcmute.cinema_booking_api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String oldPassword;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String newPassword;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String confirmPassword;
}