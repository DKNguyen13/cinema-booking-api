package vn.hcmute.cinema_booking_api.dto.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.cinema_booking_api.utils.enums.TicketStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TicketHistoryResponse {
    private Long ticketId;
    private String ticketCode;
    private String qrToken;
    private Long orderId;
    private String movieTitle;
    private String roomName;
    private String seatCode;
    private LocalDateTime showTimeDateTime;
    private Integer price;
    private TicketStatus status;
}