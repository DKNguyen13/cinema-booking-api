package vn.hcmute.cinema_booking_api.dto.seat;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.hcmute.cinema_booking_api.utils.enums.SeatStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatStatusResponse {
    Long seatId;
    String seatCode;
    SeatStatus status;
}