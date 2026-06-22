package vn.hcmute.cinema_booking_api.dto.movie;

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
public class MovieDetailResponse {
    Long movieId;
    String title;
    String description;
    Integer duration;
    Integer price;
    String posterUrl;
    String trailerUrl;
    LocalDate releaseDate;
    List<String> categories;
}