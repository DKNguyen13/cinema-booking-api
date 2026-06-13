package vn.hcmute.cinema_booking_api.controllers.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.cinema_booking_api.dto.auth.LoginResponse;
import vn.hcmute.cinema_booking_api.dto.auth.LoginRequest;
import vn.hcmute.cinema_booking_api.dto.auth.RegisterRequest;
import vn.hcmute.cinema_booking_api.dto.auth.SendOtpRequest;
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
}