package vn.hcmute.cinema_booking_api.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePaymentRequest {
    private Long showtimeId;
    private List<Long> seatIds;
}