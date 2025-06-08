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

            helper.setFrom(fromMail, "CINEMA BOOKING"); // Tên người gửi
            helper.setTo(toMail.trim()); // Email người nhận
            helper.setSubject("🔒 Reset Password - Your New Temporary Password");

            String htmlContent = """
                <div style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;'>
                    <div style='max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>
                        <h2 style='color: #2c3e50;'>🎬 CINEMA BOOKING</h2>
                        <p style='font-size: 16px; color: #333;'>You have requested to reset your password.</p>
                        <p style='font-size: 16px; color: #333;'>Here is your new temporary password:</p>
                        <div style='margin: 20px 0; padding: 15px; font-size: 24px; font-weight: bold; color: #d6336c; background-color: #f8d7da; border-radius: 5px; text-align: center;'>
                            """ + newPass + """
                        </div>
                        <p style='font-size: 14px; color: #555;'>Please use this password to log in and remember to change it immediately after logging in for your security.</p>
                        <p style='font-size: 12px; color: gray;'>If you didn’t request this password reset, you can safely ignore this email.</p>
                        <hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;' />
                        <p style='font-size: 12px; color: #aaa;'>This is an automated message. Please do not reply.</p>
                    </div>
                </div>
            """;

            helper.setText(htmlContent, true); // true để gửi nội dung HTML
            mailSender.send(message); // Gửi mail
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
