package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @NotBlank(message = "Movie title must not be blank")
    @Size(max = 100, message = "Movie title must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Movie description must not be blank")
    @Size(max = 1000, message = "Movie description must not exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Duration must not be null")
    @Min(value = 1, message = "Duration must be greater than 0")
    @Column(nullable = false)
    private Integer duration;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer price;

    @NotBlank(message = "Poster URL must not be blank")
    @Size(max = 500, message = "Poster URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String posterUrl;

    @NotBlank(message = "Trailer URL must not be blank")
    @Size(max = 500, message = "Trailer URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String trailerUrl;

    @NotNull(message = "Release date must not be null")
    @Column(nullable = false)
    private LocalDate releaseDate;

    @NotNull(message = "Active status must not be null")
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_categories",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    private List<Category> categories;

    @OneToMany(mappedBy = "movie")
    @JsonIgnore
    private List<ShowTime> showTimes;
}