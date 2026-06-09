package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotBlank(message = "User email must not be blank")
    @Size(max = 100, message = "User email must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String userEmail;

    @NotBlank(message = "Movie title must not be blank")
    @Size(max = 100, message = "Movie title must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String movieTitle;

    @NotNull(message = "Showtime date time must not be null")
    @Column(nullable = false)
    private LocalDateTime showTimeDateTime;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer price;

    @NotBlank(message = "Seat code must not be blank")
    @Size(max = 5, message = "Seat code must not exceed 5 characters")
    @Column(nullable = false, length = 5)
    private String seatCode;

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