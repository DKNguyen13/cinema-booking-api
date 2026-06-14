package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.admin.UserDetailResponse;
import vn.hcmute.cinema_booking_api.dto.admin.UserListResponse;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements vn.hcmute.cinema_booking_api.services.IAdminService {
    private final UserRepository userRepository;

    // Get all user
    @Override
    public List<UserListResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mappingUserToUserList)
                .toList();
    }

    // Get user detail
    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return mappingUserToUserDetail(user);
    }

    // Set active/inactive account
    @Override
    public void setStatusUser(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        boolean newStatus = !Boolean.TRUE.equals(user.getIsActive());
        user.setIsActive(newStatus);
        userRepository.save(user);
    }

    //Helper
    private UserListResponse mappingUserToUserList(User user) {
        return UserListResponse.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .build();
    }

    private UserDetailResponse mappingUserToUserDetail(User user) {
        return UserDetailResponse.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .point(user.getPoint())
                .imageUrl(user.getImageUrl())
                .isActive(user.getIsActive())
                .roleName(user.getRole().getRoleName())
                .build();
    }
}
