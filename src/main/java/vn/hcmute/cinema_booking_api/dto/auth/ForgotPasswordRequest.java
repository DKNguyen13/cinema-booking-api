package vn.hcmute.cinema_booking_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
}