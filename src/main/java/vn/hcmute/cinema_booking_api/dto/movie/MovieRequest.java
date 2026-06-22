package vn.hcmute.cinema_booking_api.dto.movie;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {

    @NotBlank(message = "Movie title must not be blank")
    @Size(max = 100, message = "Movie title must not exceed 100 characters")
    String title;

    @NotBlank(message = "Movie description must not be blank")
    @Size(max = 1000, message = "Movie description must not exceed 1000 characters")
    String description;

    @NotNull(message = "Duration must not be null")
    @Min(value = 1, message = "Duration must be greater than 0")
    Integer duration;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    Integer price;

    @NotBlank(message = "Poster URL must not be blank")
    @Size(max = 500, message = "Poster URL must not exceed 500 characters")
    String posterUrl;

    @NotBlank(message = "Trailer URL must not be blank")
    @Size(max = 500, message = "Trailer URL must not exceed 500 characters")
    String trailerUrl;

    @NotNull(message = "Release date must not be null")
    LocalDate releaseDate;

    @NotEmpty(message = "Movie must have at least one category")
    List<Long> categoryIds;
}