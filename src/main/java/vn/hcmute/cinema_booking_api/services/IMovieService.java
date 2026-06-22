package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.movie.MovieDetailResponse;
import vn.hcmute.cinema_booking_api.dto.movie.MovieListResponse;
import java.util.List;

public interface IMovieService {
    List<MovieListResponse> getAllMovies();
    MovieDetailResponse getMovieDetail(Long movieId);
    List<MovieListResponse> searchMovies(String keyword);
    List<MovieListResponse> getMoviesByCategory(Long categoryId);
}