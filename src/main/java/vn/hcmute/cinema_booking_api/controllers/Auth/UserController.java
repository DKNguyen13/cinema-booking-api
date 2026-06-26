package vn.hcmute.cinema_booking_api.controllers.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.user.UpdatePasswordRequest;
import vn.hcmute.cinema_booking_api.dto.user.UpdateProfileRequest;
import vn.hcmute.cinema_booking_api.services.IUserService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Load profile successful!", userService.getUserProfile()));
    }

    @PutMapping(
            value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<String>> updateProfile(@Valid @ModelAttribute UpdateProfileRequest request) {
        userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}