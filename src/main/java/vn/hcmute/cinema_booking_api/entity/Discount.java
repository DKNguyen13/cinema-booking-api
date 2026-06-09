package vn.hcmute.cinema_booking_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @NotBlank(message = "Discount code must not be blank")
    @Size(max = 50, message = "Discount code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String discountCode;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String description;

    @Min(value = 0, message = "Percentage must be greater than or equal to 0")
    @Max(value = 100, message = "Percentage must not exceed 100")
    private Integer percentage;

    @Min(value = 0, message = "Fixed amount must be greater than or equal to 0")
    private Integer fixedAmount;

    @NotNull(message = "Active status must not be null")
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Start date must not be null")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Column(nullable = false)
    private LocalDate endDate;
}