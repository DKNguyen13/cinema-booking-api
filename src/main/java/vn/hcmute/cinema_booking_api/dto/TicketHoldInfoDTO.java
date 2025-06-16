package vn.hcmute.cinema_booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHoldInfoDTO {
    private String email;
    private Long seatId;
    private Long showtimeId;
    private String seatCode;
    private String movieTitle;
    private String showTime;
    private Integer price;
}