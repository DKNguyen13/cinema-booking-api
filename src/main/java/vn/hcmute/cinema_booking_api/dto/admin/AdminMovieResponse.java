package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminMovieResponse {
    private Long movieId;
    private String title;
    private String description;
    private Integer duration;
    private Integer price;
    private String posterUrl;
    private String trailerUrl;
    private LocalDate releaseDate;
    private Boolean isActive;
    private List<String> categories;
}
