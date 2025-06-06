package vn.hcmute.cinema_booking_api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean//Dung cho luu kieu don gian cap value String, String
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
}
