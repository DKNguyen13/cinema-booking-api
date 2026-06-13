package vn.hcmute.cinema_booking_api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
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

    private MultipartFile image;
}