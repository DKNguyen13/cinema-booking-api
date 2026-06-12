package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.auth.AuthResponse;

public interface IAuthService {
    boolean login(String email, String password);
    boolean register(String email, String password, String fullName, String phone, String address);
    boolean checkExistEmailOrPhone(String email, String phone);

    AuthResponse getCurrentUser(String email);
}
