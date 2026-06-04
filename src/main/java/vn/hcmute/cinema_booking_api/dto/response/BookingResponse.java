package vn.hcmute.cinema_booking_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hcmute.cinema_booking_api.utils.enums.OrderPayment;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long orderId;
    private Integer finalPrice;
    private Integer quantity;
    private OrderStatus status;
    private OrderPayment payment;
    private String createdDate;
    private List<TicketDetail> tickets;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketDetail {
        private Long ticketId;
        private String seatCode;
        private Integer price;
        private String movieTitle;
        private String showTime;
    }
}