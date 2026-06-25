package vn.hcmute.cinema_booking_api.services.Impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.entity.Order;
import vn.hcmute.cinema_booking_api.entity.Ticket;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.services.IMailService;
import vn.hcmute.cinema_booking_api.utils.CONSTANT;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements IMailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redis;

    @Value("${spring.mail.username}")
    private String fromMail;

    // OTP Generation
    @Override
    public String generateOtp(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        String key = CONSTANT.OTP_PREFIX + normalizedEmail;
        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));
        redis.opsForValue().set(key, otp, CONSTANT.OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    // OTP Validation
    @Override
    public boolean validateOtp(String email, String otp) {
        String normalizedEmail = email.trim().toLowerCase();
        String key = CONSTANT.OTP_PREFIX + normalizedEmail;
        String storedOtp = redis.opsForValue().get(key);
        if (storedOtp == null) return false;
        if (!storedOtp.equals(otp)) return false;
        redis.delete(key);
        return true;
    }

    // Send OTP mail
    @Async
    @Override
    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromMail, "CINEMA BOOKING");
            helper.setTo(email.trim());
            helper.setSubject("Your OTP Code");
            String html = buildOtpTemplate(otp);
            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Send OTP failed for email: {}", email, e);
            throw new RuntimeException("Cannot send OTP email");
        }
    }

    // Send new password
    @Async
    @Override
    public void sendNewPassword(String email, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail, "CINEMA BOOKING");
            helper.setTo(email.trim());
            helper.setSubject("New Password");
            helper.setText("""
                <h2>Your new password</h2>
                <h3>%s</h3>
            """.formatted(newPassword), true);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Send new password failed: {}", email, e);
            throw new RuntimeException("Cannot send password email");
        }
    }

    @Async
    @Override
    public void sendTicketEmail(Order order, List<Ticket> tickets) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail, "CINEMA BOOKING");
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Your Cinema Tickets - Order #" + order.getOrderId());

            String html = buildTicketTemplate(order, tickets);
            helper.setText(html, true);

            for (Ticket ticket : tickets) {
                String cid = "qr_" + ticket.getTicketId();
                helper.addInline(cid, new ByteArrayResource(generateQrCode(ticket.getQrToken())), "image/png");
            }

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Send ticket email failed for order: {}", order.getOrderId(), e);
        }
    }

    // Helper
    private String buildTicketTemplate(Order order, List<Ticket> tickets) {
        String ticketRows = tickets.stream()
                .map(ticket -> """
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="margin:18px 0;border:1px solid #e5e5e5;border-radius:14px;
                          border-collapse:separate;background:#fff;
                          box-shadow:0 2px 8px rgba(0,0,0,.05);">

                <tr>
                    <td style="padding:18px;vertical-align:top;width:70%%;">

                        <div style="font-size:22px;
                                    font-weight:bold;
                                    color:#d6336c;
                                    margin-bottom:12px;">
                            🎟 Seat %s
                        </div>

                        <table style="font-size:14px;line-height:1.8;">
                            <tr>
                                <td style="color:#777;">Movie</td>
                                <td><b>%s</b></td>
                            </tr>

                            <tr>
                                <td style="color:#777;">Room</td>
                                <td><b>%s</b></td>
                            </tr>

                            <tr>
                                <td style="color:#777;">Showtime</td>
                                <td><b>%s</b></td>
                            </tr>

                            <tr>
                                <td style="color:#777;">Seat</td>
                                <td><b>%s</b></td>
                            </tr>

                            <tr>
                                <td style="color:#777;">Price</td>
                                <td><b style="color:#e63946;">%,d VND</b></td>
                            </tr>

                            <tr>
                                <td style="color:#777;">Ticket</td>
                                <td style="font-family:monospace;">
                                    %s
                                </td>
                            </tr>

                        </table>

                    </td>

                    <td style="width:30%%;
                               text-align:center;
                               border-left:2px dashed #ddd;
                               padding:18px;">

                        <img src="cid:qr_%d"
                             width="150"
                             height="150"
                             style="display:block;margin:auto;" />

                        <div style="font-size:12px;
                                    color:#777;
                                    margin-top:10px;">
                            Scan QR to enter
                        </div>

                    </td>

                </tr>

            </table>
        """.formatted(
                        ticket.getSeatCode(),
                        ticket.getMovieTitle(),
                        ticket.getRoomName(),
                        ticket.getShowTimeDateTime(),
                        ticket.getSeatCode(),
                        ticket.getPrice(),
                        ticket.getTicketCode(),
                        ticket.getTicketId()
                ))
                .collect(Collectors.joining());

        return """
        <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:30px;">
            <div style="max-width:650px; margin:auto; background:white; padding:25px; border-radius:12px;">
                <h2 style="text-align:center; color:#d6336c;">🎬 CINEMA BOOKING</h2>
                <p style="text-align:center; color:#666;">Your payment was successful.</p>

                <hr/>

                <p><b>Order ID:</b> #%d</p>
                <p><b>Total tickets:</b> %d</p>
                <p><b>Total price:</b> %,d VND</p>

                <h3>Your tickets</h3>
                %s

                <p style="font-size:13px; color:#666;">
                    Please show this email or QR code at the cinema counter for ticket verification.
                </p>
            </div>
        </div>
    """.formatted(
                order.getOrderId(),
                tickets.size(),
                order.getFinalPrice(),
                ticketRows
        );
    }

    private String buildOtpTemplate(String otp) {
        return """
        <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;">
            <div style="max-width: 480px; margin: auto; background: #ffffff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08);">

                <h2 style="text-align: center; color: #d6336c; margin-bottom: 10px;">
                    🎬 CINEMA BOOKING
                </h2>

                <p style="text-align: center; color: #6c757d; font-size: 14px;">
                    OTP Verification Code
                </p>

                <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;" />

                <p style="font-size: 15px; color: #333;">
                    Hello 👋,<br/>
                    Use the following OTP to complete your verification:
                </p>

                <div style="
                    text-align: center;
                    font-size: 32px;
                    font-weight: bold;
                    letter-spacing: 6px;
                    color: #ffffff;
                    background: linear-gradient(135deg, #ff4d6d, #d6336c);
                    padding: 15px;
                    border-radius: 10px;
                    margin: 20px 0;
                ">
                    %s
                </div>

                <p style="font-size: 13px; color: #555;">
                    ⚠️ This OTP is valid for <b>5 minutes</b>. Do not share it with anyone.
                </p>

                <div style="margin-top: 25px; font-size: 12px; color: #aaa; text-align: center;">
                    If you did not request this code, please ignore this email.
                </div>

            </div>
        </div>
    """.formatted(otp);
    }

    @Override
    public void checkOtpRateLimit(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        String key = CONSTANT.OTP_LIMIT_PREFIX + normalizedEmail;

        Boolean exists = redis.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            throw new BadRequestException("Please wait before requesting another OTP!");
        }
        redis.opsForValue().set(key, "1", 60, TimeUnit.SECONDS);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private byte[] generateQrCode(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 180, 180);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate QR code");
        }
    }
}