package vn.hcmute.cinema_booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDTO {
    private String discountCode;
    private String description;
    private Integer percentage;
    private Integer fixedAmount;
    private Boolean active;
    private Integer quantity;
    private LocalDate startDate;
    private LocalDate endDate;
}
