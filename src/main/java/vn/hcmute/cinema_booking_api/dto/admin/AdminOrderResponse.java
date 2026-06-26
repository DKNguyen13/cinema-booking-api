package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;
import vn.hcmute.cinema_booking_api.utils.enums.PaymentMethod;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminOrderResponse {
    private Long orderId;
    private String userEmail;
    private String userFullName;
    private String movieTitle;
    private LocalDateTime showTime;
    private Integer finalPrice;
    private Integer quantity;
    private OrderStatus status;
    private PaymentMethod payment;
    private LocalDateTime createdDate;
    private LocalDateTime paidAt;
}
