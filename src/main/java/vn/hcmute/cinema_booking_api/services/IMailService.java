package vn.hcmute.cinema_booking_api.services;

public interface IMailService {
    String generateOtp(String email);
    boolean validateOtp(String email, String otp);
    void sendOtpEmail(String email, String otp);
    void sendNewPassword(String email, String newPassword);

    void checkOtpRateLimit(String email);
}