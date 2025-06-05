package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import vn.hcmute.cinema_booking_api.utils.OrderPayment;
import vn.hcmute.cinema_booking_api.utils.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private Integer finalPrice;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)//Save enum value dạng chuỗi
    private OrderStatus status;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderPayment payment;

    @NotNull
    @Column(nullable = false, updatable = false)
    @CreationTimestamp // Hibernate tự động tạo giá trị thời gian
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Ticket> tickets;
}
