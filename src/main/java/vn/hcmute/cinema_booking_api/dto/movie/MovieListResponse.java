package vn.hcmute.cinema_booking_api.dto.movie;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieListResponse {
    Long movieId;
    String title;
    Integer duration;
    Integer price;
    String posterUrl;
    LocalDate releaseDate;
}