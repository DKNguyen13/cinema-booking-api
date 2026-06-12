package vn.hcmute.cinema_booking_api.utils;

import java.time.Duration;

public class CONSTANT {
    public static final Duration HOLD_TICKET_DURATION = Duration.ofMinutes(5);

    public static final String DEFAULT_AVATAR = "https://res.cloudinary.com/demec8nev/image/upload/v1745039879/default_avatar_r7xkiv.png";

    // Mail config
    public static final String OTP_PREFIX = "OTP:";
    public static final int OTP_LENGTH = 6;
    public static final long OTP_EXPIRE_MINUTES = 5;
    public static final String OTP_LIMIT_PREFIX = "OTP_LIMIT:";
}