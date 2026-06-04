package vn.hcmute.cinema_booking_api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hcmute.cinema_booking_api.utils.enums.OrderPayment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull(message = "Showtime ID cannot be null")
    private Long showtimeId;

    @NotEmpty(message = "At least one seat code must be selected")
    private List<String> seatCodes;

    private String discountCode;

    @NotNull(message = "Payment method cannot be null")
    private OrderPayment payment;
}