package vn.hcmute.cinema_booking_api.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreatePaymentResponse {
    private Long orderId;
    private String paymentUrl;
}
