package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 100)
    @Length(min = 1, max = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    @Length(min = 1, max = 1000)
    private String description;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    @Min(0)
    private int price;

    @Column(nullable = false, length = 500)
    private String posterUrl;

    @Column(nullable = false, length = 500)
    private String trailerUrl;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ShowTime> showTimes;

}
