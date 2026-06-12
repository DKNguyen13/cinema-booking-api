package vn.hcmute.cinema_booking_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(
            regexp = "^(\\d{10})$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otp;
}