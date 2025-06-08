package vn.hcmute.cinema_booking_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @Column(nullable = false, unique = true)
    @NotNull
    private String discountCode;

    @NotNull
    @Column(nullable = false)
    private String description;

    @Min(0)
    private Integer percentage;

    @Min(value = 0)
    private Integer fixedAmount;

    @Column(nullable = false)
    @NotNull
    private Boolean active = true;

    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
