package vn.hcmute.cinema_booking_api.controllers.Movie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.movie.MovieDetailResponse;
import vn.hcmute.cinema_booking_api.dto.movie.MovieListResponse;
import vn.hcmute.cinema_booking_api.services.IMovieService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {
    private final IMovieService movieService;

    // Get all movies
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieListResponse>>> getAllMovies() {
        return ResponseEntity.ok(ApiResponse.success("Get all movies successful!", movieService.getAllMovies()));
    }

    // Get movie detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> getMovieDetail(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Get movie detail successful!", movieService.getMovieDetail(id))
        );
    }

    // Search movies
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieListResponse>>> searchMovies(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search movies successful!", movieService.searchMovies(keyword)));
    }

    // Get movies by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<MovieListResponse>>> getMoviesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success("Get movies by category successful!", movieService.getMoviesByCategory(categoryId)));
    }
}