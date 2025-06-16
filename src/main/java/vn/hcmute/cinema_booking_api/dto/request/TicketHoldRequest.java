package vn.hcmute.cinema_booking_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketHoldRequest {
    private Long showtimeId;
    private String seatCode;
}
