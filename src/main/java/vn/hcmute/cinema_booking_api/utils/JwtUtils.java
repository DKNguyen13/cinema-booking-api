package vn.hcmute.cinema_booking_api.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final long JWT_EXPIRATION = 1 * 60 * 60 * 1000;//Thoi gian ton tai (1 gio)
    private final String SECRET_KEY = "c99cad7fa62cf6617fde7d78dcd86a5676b536919ff4f099f5fb982cc143d746"; //Tao khoa bi mat

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Tạo JWT token dựa trên email
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy email từ JWT token
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())//Thiết lập khóa bí mật để xác thực token
                    .build()
                    .parseClaimsJws(token);//Giải mã token, xác thực chữ ký và thời hạn
            return true;//Nếu thành công => Token hợp lệ
        } catch (ExpiredJwtException e) {
            System.out.println("Token đã hết hạn");
        } catch (UnsupportedJwtException e) {
            System.out.println("Token không được hỗ trợ");
        } catch (MalformedJwtException e) {
            System.out.println("Token sai định dạng");
        } catch (SignatureException e) {
            System.out.println("Chữ ký token không hợp lệ");
        } catch (IllegalArgumentException e) {
            System.out.println("Token null hoặc rỗng");
        }
        return false;
    }

    //Giải mã lấy toàn bộ claims từ token
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
