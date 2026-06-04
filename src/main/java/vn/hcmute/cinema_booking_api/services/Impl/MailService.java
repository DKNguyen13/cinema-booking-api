package vn.hcmute.cinema_booking_api.services.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.utils.Constant;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class MailService{
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redis;

    @Value("${spring.mail.username}")
    private String fromMail;

    public MailService(JavaMailSender mailSender, StringRedisTemplate redis) {
        this.mailSender = mailSender;
        this.redis = redis;
    }

    public boolean validateOtp(String key, String otp) {
        String storeOtp = redis.opsForValue().get(key);
        if (storeOtp != null && storeOtp.equals(otp)) {
            redis.delete(key);
            return true;
        }
        return false;
    }

    //Generate OTP 6 number
    public String generateOtp(String key){
        redis.delete(key);
        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(random.nextInt(900000) + 100000); // 6 so OTP
        redis.opsForValue().set(key, otp, Constant.OTP_EXPIRE_TIME, TimeUnit.MINUTES);
        return otp;
    }

    @Async
    public void sendOTP(String toMail, String otp) throws MessagingException, UnsupportedEncodingException {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            //Câu hinh noi dung mail
            helper.setFrom(fromMail, "CINEMA BOOKING"); //Ten nguoi gui
            helper.setTo(toMail.trim()); //mail nguoi nhan
            helper.setSubject("YOUR OTP CODE"); //Tieu de mail

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #2e6c80;'>Hello from CINEMA BOOKING!</h2>"
                    + "<p>Your OTP code is:</p>"
                    + "<div style='font-size: 28px; font-weight: bold; color: #d6336c;'>" + otp + "</div>"
                    + "<p>This code is valid for <b>5 minutes</b>. Please do not share it with anyone.</p>"
                    + "<br><p style='font-size: 12px; color: gray;'>If you did not request this, please ignore this email.</p>"
                    + "</div>";

            helper.setText(htmlContent, true); // true de gui html, noi dung mail
            mailSender.send(message);//Gui mail
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendNewPass(String toMail, String newPass){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromMail, "CINEMA BOOKING");
            helper.setTo(toMail.trim());
            helper.setSubject("🔒 Reset Password - Your New Temporary Password");

            String htmlContent = """
                <div style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;'>
                    <div style='max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>
                        <h2 style='color: #2c3e50;'>🎬 CINEMA BOOKING</h2>
                        <p style='font-size: 16px; color: #333;'>You have requested to reset your password.</p>
                        <p style='font-size: 16px; color: #333;'>Here is your new temporary password:</p>
                        <div style='margin: 20px 0; padding: 15px; font-size: 24px; font-weight: bold; color: #d6336c; background-color: #f8d7da; border-radius: 5px; text-align: center;'>
                    """
                    + newPass +
                    """
                                </div>
                                <p style='font-size: 14px; color: #555;'>Please use this password to log in and remember to change it immediately after logging in for your security.</p>
                                <p style='font-size: 12px; color: gray;'>If you didn’t request this password reset, you can safely ignore this email.</p>
                                <hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;' />
                                <p style='font-size: 12px; color: #aaa;'>This is an automated message. Please do not reply.</p>
                            </div>
                        </div>
                    """;

            helper.setText(htmlContent, true);
            mailSender.send(message);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Async
    public void sendBookingConfirmation(String toMail, vn.hcmute.cinema_booking_api.entity.Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail, "CINEMA BOOKING");
            helper.setTo(toMail.trim());
            helper.setSubject("Booking Confirmation - Thank You for Your Purchase!");

            StringBuilder ticketLines = new StringBuilder();
            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            
            String movieTitle = "N/A";
            String showTimeStr = "N/A";
            
            if (order.getTickets() != null && !order.getTickets().isEmpty()) {
                vn.hcmute.cinema_booking_api.entity.Ticket firstTicket = order.getTickets().get(0);
                movieTitle = firstTicket.getMovieTitle();
                if (firstTicket.getShowTimeDateTime() != null) {
                    showTimeStr = firstTicket.getShowTimeDateTime().format(timeFormatter);
                }
                
                for (vn.hcmute.cinema_booking_api.entity.Ticket ticket : order.getTickets()) {
                    ticketLines.append("<tr style='border-bottom: 1px solid #eee;'>")
                            .append("<td style='padding: 10px 0; color: #333;'>Seat: <b>").append(ticket.getSeatCode()).append("</b></td>")
                            .append("<td style='padding: 10px 0; text-align: right; color: #333;'>").append(String.format("%,d", ticket.getPrice())).append(" VND</td>")
                            .append("</tr>");
                }
            }

            String htmlContent = """
                <div style='font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;'>
                    <div style='max-width: 600px; margin: auto; background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.08);'>
                        <div style='text-align: center; border-bottom: 2px solid #e9ecef; padding-bottom: 15px; margin-bottom: 20px;'>
                            <h1 style='color: #d6336c; margin: 0; font-size: 28px;'>🎬 CINEMA BOOKING</h1>
                            <p style='color: #6c757d; margin: 5px 0 0 0; font-size: 14px;'>Booking Confirmation & Invoice</p>
                        </div>
                        
                        <p style='font-size: 16px; color: #212529;'>Dear customer,</p>
                        <p style='font-size: 16px; color: #495057; line-height: 1.5;'>Thank you for choosing <b>Cinema Booking</b>! Your tickets have been successfully booked and confirmed. Below is your booking summary:</p>
                        
                        <div style='background-color: #f8f9fa; border-left: 4px solid #d6336c; padding: 15px; border-radius: 4px; margin: 20px 0;'>
                            <table style='width: 100%; border-collapse: collapse;'>
                                <tr>
                                    <td style='padding: 5px 0; color: #6c757d; width: 120px;'>Order ID:</td>
                                    <td style='padding: 5px 0; color: #212529; font-weight: bold;'>#""" + order.getOrderId() + """
                                    </td>
                                </tr>
                                <tr>
                                    <td style='padding: 5px 0; color: #6c757d;'>Movie:</td>
                                    <td style='padding: 5px 0; color: #212529; font-weight: bold; font-size: 16px;'>""" + movieTitle + """
                                    </td>
                                </tr>
                                <tr>
                                    <td style='padding: 5px 0; color: #6c757d;'>Showtime:</td>
                                    <td style='padding: 5px 0; color: #212529;'>""" + showTimeStr + """
                                    </td>
                                </tr>
                                <tr>
                                    <td style='padding: 5px 0; color: #6c757d;'>Payment:</td>
                                    <td style='padding: 5px 0; color: #28a745; font-weight: bold;'>""" + order.getPayment() + """ 
                                (PAID)</td>
                                </tr>
                            </table>
                        </div>

                        <h3 style='color: #343a40; margin-top: 25px; border-bottom: 1px solid #dee2e6; padding-bottom: 5px;'>Ticket Details</h3>
                        <table style='width: 100%; border-collapse: collapse;'>
                            """ + ticketLines.toString() + """
                            <tr>
                                <td style='padding: 15px 0 5px 0; font-size: 18px; font-weight: bold; color: #212529;'>Total Paid:</td>
                                <td style='padding: 15px 0 5px 0; font-size: 20px; font-weight: bold; text-align: right; color: #d6336c;'>
                                    """ + String.format("%,d", order.getFinalPrice()) + """ 
                                VND
                                </td>
                            </tr>
                        </table>

                        <div style='margin-top: 30px; padding: 15px; background: #e8f4fd; border-radius: 8px; text-align: center; border: 1px solid #b8daff;'>
                            <p style='margin: 0; font-size: 14px; color: #004085;'>
                                🎫 <b>How to get your physical ticket:</b> Present this email invoice at the cinema counter or kiosk 15 minutes before the showtime to print your physical ticket. Enjoy your movie!
                            </p>
                        </div>

                        <hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;' />
                        <div style='text-align: center; color: #adb5bd; font-size: 12px;'>
                            <p style='margin: 0;'>This is an automated transaction receipt. Please do not reply directly to this email.</p>
                            <p style='margin: 5px 0 0 0;'>&copy; Cinema Booking API Project. All rights reserved.</p>
                        </div>
                    </div>
                </div>
            """;

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
