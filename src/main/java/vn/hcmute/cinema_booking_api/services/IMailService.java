package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.entity.Order;
import vn.hcmute.cinema_booking_api.entity.Ticket;
import java.util.List;

public interface IMailService {
    String generateOtp(String email);
    boolean validateOtp(String email, String otp);
    void sendOtpEmail(String email, String otp);
    void sendNewPassword(String email, String newPassword);
    void checkOtpRateLimit(String email);
    void sendTicketEmail(Order order, List<Ticket> tickets);
}