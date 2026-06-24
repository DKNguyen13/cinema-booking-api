package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.movie.MovieDetailResponse;
import vn.hcmute.cinema_booking_api.dto.movie.MovieListResponse;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.CategoryRepository;
import vn.hcmute.cinema_booking_api.repositories.MovieRepository;
import vn.hcmute.cinema_booking_api.services.IMovieService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService implements IMovieService {
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovieListResponse> getAllMovies() {
        return movieRepository.findByIsActiveTrueOrderByReleaseDateDesc()
                .stream()
                .map(this::mapToMovieListResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieDetail(Long movieId) {
        Movie movie = movieRepository.findByMovieIdAndIsActiveTrue(movieId)
                .orElseThrow(() -> new BadRequestException("Movie not found"));
        return mapToMovieDetailResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieListResponse> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMovies();
        }

        return movieRepository
                .findByTitleContainingIgnoreCaseAndIsActiveTrueOrderByReleaseDateDesc(keyword.trim())
                .stream()
                .map(this::mapToMovieListResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieListResponse> getMoviesByCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        return movieRepository.findActiveMoviesByCategoryId(categoryId)
                .stream()
                .map(this::mapToMovieListResponse)
                .toList();
    }

    // Helper
    private MovieListResponse mapToMovieListResponse(Movie movie) {
        return MovieListResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .duration(movie.getDuration())
                .price(movie.getPrice())
                .posterUrl(movie.getPosterUrl())
                .releaseDate(movie.getReleaseDate())
                .build();
    }

    private MovieDetailResponse mapToMovieDetailResponse(Movie movie) {
        List<String> categories = movie.getCategories()
                .stream()
                .map(Category::getCategoryName)
                .toList();

        return MovieDetailResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .price(movie.getPrice())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .releaseDate(movie.getReleaseDate())
                .categories(categories)
                .build();
    }
}