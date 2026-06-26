package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.auth.LoginResponse;
import vn.hcmute.cinema_booking_api.entity.Role;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.RoleRepository;
import vn.hcmute.cinema_booking_api.repositories.UserRepository;
import vn.hcmute.cinema_booking_api.security.jwt.JwtUtils;
import vn.hcmute.cinema_booking_api.services.IAuthService;
import vn.hcmute.cinema_booking_api.services.IMailService;
import vn.hcmute.cinema_booking_api.utils.CONSTANT;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IMailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Login user
    @Override
    public LoginResponse loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password!"));
        if (!user.getRole().getRoleName().equals("USER")){
            throw new BadRequestException("Invalid email or password!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())){
            throw new BadRequestException("Your account has been deactivated. Please contact support.");
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return LoginResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .imgUrl(user.getImageUrl())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse loginAdminStaff(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password!"));

        String roleName = user.getRole().getRoleName();

        if (!roleName.equals("ADMIN") && !roleName.equals("STAFF")) {
            throw new BadRequestException("Invalid email or password!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid email or password!");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Your account has been deactivated. Please contact support.");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return LoginResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .imgUrl(user.getImageUrl())
                .token(token)
                .build();
    }

    // Register
    @Override
    public void sendRegisterOTP(String email, String phone) {
        if (checkExistEmailOrPhone(email, phone)){
            throw new BadRequestException("Email or phone already in use!");
        }
        mailService.checkOtpRateLimit(email);
        String otp = mailService.generateOtp(email);
        mailService.sendOtpEmail(email, otp);
    }

    // Verify OTP register
    @Override
    public void verifyRegisterOTP(String email, String password, String fullName, String phone, String address, String otp) {
        if (email == null || phone == null || otp == null) {
            throw new BadRequestException("Missing required fields");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already in use!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already in use!");
        }

        boolean valid = mailService.validateOtp(email, otp);

        if (!valid) {
            throw new BadRequestException("Invalid OTP!");
        }

        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BadRequestException("Role not found"));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .address(address)
                .imageUrl(CONSTANT.DEFAULT_AVATAR)
                .role(role)
                .build();
        userRepository.save(user);
    }

    // Send OTP forget password
    @Override
    public void sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email is invalid!"));

        mailService.checkOtpRateLimit(email);
        String otp = mailService.generateOtp(email);
        mailService.sendOtpEmail(email, otp);
    }

    // Verify forgot password OTP
    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        boolean valid = mailService.validateOtp(email, otp);

        if (!valid) {
            throw new BadRequestException("Invalid OTP!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Logout
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    // Helper
    @Override
    public boolean checkExistEmailOrPhone(String email, String phone) {
        return userRepository.existsByEmail(email) || userRepository.existsByPhone(phone);
    }
}