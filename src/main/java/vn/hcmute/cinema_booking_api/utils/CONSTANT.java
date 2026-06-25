package vn.hcmute.cinema_booking_api.utils;

import java.time.Duration;

public class CONSTANT {
    // Default value
    public static final String DEFAULT_AVATAR = "https://res.cloudinary.com/demec8nev/image/upload/v1745039879/default_avatar_r7xkiv.png";

    // Hold Seat Config
    public static final int HOLD_MINUTES = 5;
    public static final int OWNER_BLOCK_MINUTES = 6;
    public static final int MAX_SEATS = 8;
    public static final int PAYMENT_HOLD_MINUTES = 15;

    // Mail config
    public static final String OTP_PREFIX = "OTP:";
    public static final int OTP_LENGTH = 6;
    public static final long OTP_EXPIRE_MINUTES = 5;
    public static final String OTP_LIMIT_PREFIX = "OTP_LIMIT:";
}