package vn.hcmute.cinema_booking_api.services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.cinema_booking_api.dto.user.ChangePasswordRequest;
import vn.hcmute.cinema_booking_api.dto.user.UpdateProfileRequest;
import vn.hcmute.cinema_booking_api.dto.user.UserProfileResponse;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repository.UserRepository;
import vn.hcmute.cinema_booking_api.services.ICloudinaryService;
import vn.hcmute.cinema_booking_api.services.IUserService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ICloudinaryService cloudinaryService;

    // Get user profile
    @Override
    public UserProfileResponse getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return  mapUserToUserProfileResponse(user);
    }

    // Update profile
    @Transactional
    @Override
    public void updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        MultipartFile image = request.getImage();

        if (image != null && !image.isEmpty()) {

            String oldPublicId = user.getImagePublicId();
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(image);
            user.setImageUrl(uploadResult.get("secure_url").toString());
            user.setImagePublicId(uploadResult.get("public_id").toString());

            if (oldPublicId != null && !oldPublicId.isBlank()) {
                cloudinaryService.deleteFile(oldPublicId);
            }
        }
        userRepository.save(user);
    }

    // Change password
    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password confirmation does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    //Helper
    private UserProfileResponse mapUserToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .point(user.getPoint())
                .imageUrl(user.getImageUrl())
                .imagePublicId(user.getImagePublicId())
                .build();
    }
}
