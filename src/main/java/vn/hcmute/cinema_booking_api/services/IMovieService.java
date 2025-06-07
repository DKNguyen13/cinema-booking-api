package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.MovieDTO;
import vn.hcmute.cinema_booking_api.entity.Movie;

import java.util.List;

public interface IMovieService {
    List<Movie> getAllMoviesActive();

    List<Movie> getMovieByTitle(String title);

    void createMovie(Movie movie);

    void deactivateMovie(Long movieId);

    void updateMovie(Long movieId, MovieDTO dto);

    List<Movie> getMoviesByCategoryName(String categoryName);

    MovieDTO getMovieByMovieId(Long movieId);
}
