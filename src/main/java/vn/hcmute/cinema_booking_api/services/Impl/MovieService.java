package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.MovieDTO;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.repository.CategoryRepository;
import vn.hcmute.cinema_booking_api.repository.MovieRepository;
import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Movie> getAllMoviesActive() {
        return movieRepository.findAllByIsActiveTrue();
    }

    public List<Movie> getMovieByTitle(String title) {
        return movieRepository.findAllByTitleContainingIgnoreCaseAndIsActiveTrue(title);
    }

    public void createMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public void deactivateMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        movie.setIsActive(false);
        movieRepository.save(movie);
    }

    public void updateMovie(Long movieId, MovieDTO dto) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setIsActive(dto.getIsActive());
        movie.setPrice(dto.getPrice());
        movie.setDuration(dto.getDuration());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setTrailerUrl(dto.getTrailerUrl());

        // Gán lại category nếu cần
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            movie.setCategory(category);
        }

        movieRepository.save(movie);
    }

    public List<Movie> getMoviesByCategoryName(String categoryName) {
        return movieRepository.findAllByCategory_CategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName);
    }

}
