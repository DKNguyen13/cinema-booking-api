package vn.hcmute.cinema_booking_api.services;

import jakarta.transaction.Transactional;
import vn.hcmute.cinema_booking_api.dto.user.UpdatePasswordRequest;
import vn.hcmute.cinema_booking_api.dto.user.UpdateProfileRequest;
import vn.hcmute.cinema_booking_api.dto.user.UserProfileResponse;

public interface IUserService {
    UserProfileResponse getUserProfile();

    @Transactional
    void updateProfile(UpdateProfileRequest request);

    @Transactional
    void changePassword(UpdatePasswordRequest request);
}
