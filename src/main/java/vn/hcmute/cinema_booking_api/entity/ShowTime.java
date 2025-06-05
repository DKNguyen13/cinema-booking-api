package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShowTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;

    @Column(nullable = false)
    @NotNull(message = "Show time not null")
    private LocalDateTime showTime;

    @JsonIgnore
    @JoinColumn(name = "movieId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}
