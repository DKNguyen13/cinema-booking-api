package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.MovieDTO;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.services.Impl.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies() {
        try {
            List<Movie> movies = movieService.getAllMoviesActive();
            if(movies.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("List movies", movies));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/movie")
    public ResponseEntity<?> createMovie(@RequestBody MovieDTO dto) {
        try{
            movieService.createMovie(dto);
            return ResponseEntity.ok(ApiResponse.success("Added movie"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/movies/search")
    public ResponseEntity<?> getMoviesByTitle(@RequestParam String title) {
        try{
            List<Movie> movies = movieService.getMovieByTitle(title);
            if(movies.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("List movies", movies));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/movies/{id}")
    public ResponseEntity<?> deactivateMovie(@PathVariable Long id) {
        try{
            movieService.deactivateMovie(id);
            return ResponseEntity.ok(ApiResponse.success("Deactivated movie"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
