package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @NotBlank(message = "Seat code must not be blank")
    @Size(max = 5, message = "Seat code must not exceed 5 characters")
    @Column(nullable = false, unique = true, length = 5)
    private String seatCode;

    @OneToMany(mappedBy = "seat")
    @JsonIgnore
    private List<BookedSeat> bookedSeats;
}