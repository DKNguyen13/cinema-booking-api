package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.hcmute.cinema_booking_api.utils.enums.TicketStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ticket_code", columnNames = "ticket_code"),
                @UniqueConstraint(name = "uk_ticket_qr_token", columnNames = "qr_token"),
                @UniqueConstraint(name = "uk_ticket_order_seat", columnNames = {"order_id", "seat_id"})
        }
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotBlank(message = "Ticket code must not be blank")
    @Size(max = 50, message = "Ticket code must not exceed 50 characters")
    @Column(name = "ticket_code", nullable = false, length = 50)
    private String ticketCode;

    @NotBlank(message = "QR token must not be blank")
    @Size(max = 100, message = "QR token must not exceed 100 characters")
    @Column(name = "qr_token", nullable = false, length = 100)
    private String qrToken;

    @NotNull(message = "Ticket status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;

    @NotBlank(message = "User email must not be blank")
    @Size(max = 100, message = "User email must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String userEmail;

    @NotBlank(message = "Movie title must not be blank")
    @Size(max = 150, message = "Movie title must not exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String movieTitle;

    @NotNull(message = "Showtime date time must not be null")
    @Column(nullable = false)
    private LocalDateTime showTimeDateTime;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer price;

    @NotBlank(message = "Seat code must not be blank")
    @Size(max = 10, message = "Seat code must not exceed 10 characters")
    @Column(nullable = false, length = 10)
    private String seatCode;

    @Size(max = 50, message = "Room name must not exceed 50 characters")
    @Column(length = 50)
    private String roomName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime checkedInAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "showtime_id", nullable = false)
    private ShowTime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}