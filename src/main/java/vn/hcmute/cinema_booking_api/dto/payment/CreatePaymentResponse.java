package vn.hcmute.cinema_booking_api.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentResponse {
    private Long orderId;
    private String paymentUrl;
}
