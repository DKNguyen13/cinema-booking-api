package vn.hcmute.cinema_booking_api.controllers.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.auth.*;
import vn.hcmute.cinema_booking_api.services.IAuthService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse currentUser = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful!", currentUser));
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegisterOtp(@RequestBody @Valid SendOtpRequest req) {
        authService.sendRegisterOTP(req.getEmail(), req.getPhone());
        return ResponseEntity.ok(ApiResponse.success("OTP sent to email!", null));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid RegisterRequest req) {
        authService.verifyRegisterOTP(req.getEmail(), req.getPassword(), req.getFullName(), req.getPhone(), req.getAddress(), req.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Register successful!", null));
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendResetPasswordOtp(@RequestBody @Valid ForgotPasswordRequest req) {
        authService.sendForgotPasswordOtp(req.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent to email!", null));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getOtp(), req.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful!", null));
    }
}