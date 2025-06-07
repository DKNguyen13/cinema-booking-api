package vn.hcmute.cinema_booking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private String title;
    private String description;
    private LocalDate releaseDate;
    private Boolean isActive;
    private Long categoryId;
    private Integer price;
    private Integer duration;
    private String trailerUrl;
    private String posterUrl;
}
