package vn.hcmute.cinema_booking_api.services;

import jakarta.transaction.Transactional;
import vn.hcmute.cinema_booking_api.dto.user.ChangePasswordRequest;
import vn.hcmute.cinema_booking_api.dto.user.UpdateProfileRequest;
import vn.hcmute.cinema_booking_api.dto.user.UserProfileResponse;
import vn.hcmute.cinema_booking_api.entity.User;

public interface IUserService {
    UserProfileResponse getUserProfile();

    @Transactional
    void updateProfile(UpdateProfileRequest request);

    @Transactional
    void changePassword(ChangePasswordRequest request);
}
