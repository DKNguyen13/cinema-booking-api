package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.UserDTO;
import vn.hcmute.cinema_booking_api.dto.request.EmailRequest;
import vn.hcmute.cinema_booking_api.dto.request.LoginRequest;
import vn.hcmute.cinema_booking_api.dto.request.OtpRequest;
import vn.hcmute.cinema_booking_api.dto.request.ProfileRequest;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.services.Impl.MailService;
import vn.hcmute.cinema_booking_api.services.Impl.UserService;
import vn.hcmute.cinema_booking_api.utils.JwtUtils;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MailService mailService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginAccount(@RequestBody LoginRequest loginRequest) {
        try{
            String email = loginRequest.getEmail();
            String password = loginRequest.getPsw();

            if(!userService.checkExistEmail(email)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Email not exist"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo token JWT
            String token = jwtUtils.generateToken(email);

            UserDTO userDTO = userService.findByEmail(email);
            userDTO.setToken(token);
            return ResponseEntity.ok(ApiResponse.success("Login successful", userDTO));
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "Login failed"));

    }

    @PostMapping("/auth/otp")
    public ResponseEntity<?> sendOTP(@RequestBody EmailRequest emailRequest) {
        String otp;
        try {
            if (emailRequest.getEmail() == null || emailRequest.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            System.out.println(emailRequest.getEmail());

            // Regex kiểm tra địa chỉ email hợp lệ
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if (!Pattern.matches(emailRegex, emailRequest.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid email format"));
            }

            //Kiem tra ton tai cua email va phone
            if(!userService.checkExistEmailOrPhone(emailRequest.getEmail(), emailRequest.getPhone())) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Email or Phone Exist!"));
            }

            otp = mailService.generateOtp(emailRequest.getEmail().trim());
            mailService.sendOTP(emailRequest.getEmail().trim(), otp);
            ApiResponse<String> response = ApiResponse.success("OTP sent", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to send OTP" + e.getMessage()));
        }
    }

    //API Xac thuc otp
    @PostMapping("/auth/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
        if (otpRequest.getInfUser() == null || otpRequest.getOtpCode() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Missing information or OTP"));
        }

        String email = otpRequest.getInfUser().getEmail();
        String otp = otpRequest.getOtpCode();

        //Xac thuc email va otp thanh cong
        if(mailService.validateOtp(email, otp)){
            String password = otpRequest.getInfUser().getPsw();
            String phone = otpRequest.getInfUser().getPhone();
            String address = otpRequest.getInfUser().getAddr();
            String fullName = otpRequest.getInfUser().getFullName();
            UserDTO userDTO = new UserDTO(email, password, fullName, phone, address, "", "");
            userService.saveUser(userDTO);
            return ResponseEntity.ok(ApiResponse.success("OTP verified"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "OTP is incorrect"));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return ResponseEntity.badRequest().body(ApiResponse.error(401, "Authentication is required"));
            }
            if(profileRequest == null || profileRequest.getAddr().equals("") || profileRequest.getFullName().equals("") || profileRequest.getUrlImage().equals("")){
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid information"));
            }
            String email = authentication.getName();
            System.out.println(email);
            UserDTO userDTO = userService.findByEmail(email);
            userDTO.setFullName(profileRequest.getFullName());
            userDTO.setAddr(profileRequest.getAddr());
            userDTO.setUrlImage(profileRequest.getUrlImage());
            userService.saveUser(userDTO);
            return ResponseEntity.ok(ApiResponse.success("Profile updated"));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to save profile"));
        }
    }

}
