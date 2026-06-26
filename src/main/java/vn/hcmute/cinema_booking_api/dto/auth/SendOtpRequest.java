package vn.hcmute.cinema_booking_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendOtpRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(
            regexp = "^(\\d{10})$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;
}