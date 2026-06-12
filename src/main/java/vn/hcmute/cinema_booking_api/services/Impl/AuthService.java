package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.auth.AuthResponse;
import vn.hcmute.cinema_booking_api.entity.Role;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.repository.RoleRepository;
import vn.hcmute.cinema_booking_api.repository.UserRepository;
import vn.hcmute.cinema_booking_api.services.IAuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Login
    @Override
    public boolean login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return false;
        if (!user.get().getPassword().equals(password)) return false;
        // [JWT_SECURITY]
        return true;
    }

    // Register
    @Override
    public boolean register(String email, String password, String fullName, String phone, String address) {
        if (checkExistEmailOrPhone(email, phone)) return false;
        Role role = roleRepository
                .findByRoleName("USER")
                .orElseThrow(() ->
                        new RuntimeException("Role USER not found"));
        User user = User.builder()
                .email(email)
                .password(password)//passwordEncoder.encode(password) [JWT_SECURITY]
                .fullName(fullName)
                .phone(phone)
                .address(address)
                .urlImage("https://res.cloudinary.com/demec8nev/image/upload/v1745039879/default_avatar_r7xkiv.png")// default url
                .role(role)
                .build();
        userRepository.save(user);
        return true;
    }

    // Helper
    @Override
    public boolean checkExistEmailOrPhone(String email, String phone) {
        return userRepository.existsByEmailOrPhone(email, phone);
    }

    @Override
    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return mapToAuthResponse(user);
    }

    public AuthResponse mapToAuthResponse(User user) {
        return AuthResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getRoleName())
                .build();
    }
}