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
}
