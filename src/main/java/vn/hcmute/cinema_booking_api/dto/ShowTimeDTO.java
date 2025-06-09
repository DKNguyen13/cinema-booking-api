package vn.hcmute.cinema_booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowTimeDTO {
    private LocalDateTime showTime;
    private Long movieId;
}
