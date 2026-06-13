package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.auth.LoginResponse;

public interface IAuthService {
    LoginResponse login(String email, String password);
    void sendRegisterOTP(String email, String phone);
    void verifyRegisterOTP(String email, String password, String fullName, String phone, String address, String otp);
    boolean checkExistEmailOrPhone(String email, String phone);
}