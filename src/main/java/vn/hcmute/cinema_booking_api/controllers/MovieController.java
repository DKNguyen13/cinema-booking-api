package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.MovieDTO;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.services.Impl.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping
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

    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody MovieDTO dto) {
        try{
            movieService.createMovie(dto);
            return ResponseEntity.ok(ApiResponse.success("Added movie"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
