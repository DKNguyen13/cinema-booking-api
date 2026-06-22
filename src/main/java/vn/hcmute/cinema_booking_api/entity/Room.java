package vn.hcmute.cinema_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @NotBlank(message = "Room name must not be blank")
    @Size(max = 100, message = "Room name must not exceed 100 characters")
    @Column(name = "room_name", nullable = false, unique = true, length = 100)
    private String roomName;

    @NotNull(message = "Total seats must not be null")
    @Min(value = 1, message = "Total seats must be greater than 0")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Seat> seats;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<ShowTime> showTimes;
}