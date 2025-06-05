package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false, length = 50)
    private String userEmail;

    @Column(nullable = false, length = 100)
    private String movieTitle;

    @Column(nullable = false)
    private LocalDateTime showTimeDateTime;

    @Column(nullable = false)
    @Min(0)
    private Integer price;

    @Column(nullable = false, length = 5)
    private String seatCode;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "showtimeId")
    private ShowTime showtime;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "seatId")
    private Seat seat;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "orderId")
    private Order order;
}
