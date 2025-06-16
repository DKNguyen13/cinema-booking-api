package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.TicketHoldInfoDTO;
import vn.hcmute.cinema_booking_api.utils.Constant;

@Service
public class TicketHoldService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String getKey(Long seatId, Long showtimeId) {
        System.out.println("hold:ticket:" + showtimeId + ":" + seatId);
        return "hold:ticket:" + showtimeId + ":" + seatId;
    }

    public boolean holdTicket(TicketHoldInfoDTO info) {
        String key = getKey(info.getSeatId(), info.getShowtimeId());
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, info, Constant.HOLD_TICKET_DURATION);
        return Boolean.TRUE.equals(result);
    }

    public TicketHoldInfoDTO getHeldTicket(Long seatId, Long showtimeId) {
        String key = getKey(seatId, showtimeId);
        Object data = redisTemplate.opsForValue().get(key);
        if (data instanceof TicketHoldInfoDTO) {
            return (TicketHoldInfoDTO) data;
        }
        return null;
    }

    public void releaseTicket(Long seatId, Long showtimeId) {
        redisTemplate.delete(getKey(seatId, showtimeId));
    }
}
