package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "showtimes")
public class ShowTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;

    @Column(nullable = false)
    @NotNull(message = "Show time not null")
    private LocalDateTime showTime;

    @JsonIgnore
    @JoinColumn(name = "movie_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}