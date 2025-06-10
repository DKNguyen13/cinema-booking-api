package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.MovieDTO;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.repository.CategoryRepository;
import vn.hcmute.cinema_booking_api.repository.MovieRepository;
import vn.hcmute.cinema_booking_api.services.IMovieService;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Movie> getAllMoviesActive() {
        return movieRepository.findAllByIsActiveTrue();
    }

    @Override
    public List<Movie> getAllMoviesInactive(){
        return movieRepository.findAllByIsActiveFalse();
    }

    @Override
    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> getMovieByTitle(String title) {
        return movieRepository.findAllByTitleContainingIgnoreCaseAndIsActiveTrue(title);
    }

    @Override
    public void createMovie(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setPrice(dto.getPrice());
        movie.setDuration(dto.getDuration());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setTrailerUrl(dto.getTrailerUrl());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        movie.setCategory(category);
        movie.setIsActive(true);
        movieRepository.save(movie);
    }

    @Override
    public void deactivateMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        movie.setIsActive(false);
        movieRepository.save(movie);
    }

    @Override
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

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            movie.setCategory(category);
        }
        movieRepository.save(movie);
    }

    @Override
    public List<Movie> getMoviesByCategoryName(String categoryName) {
        return movieRepository.findAllByCategory_CategoryNameContainingIgnoreCaseAndIsActiveTrue(categoryName);
    }

    @Override
    public MovieDTO getMovieByMovieId(Long movieId){
        Optional<Movie> movie = movieRepository.findById(movieId);
        if(movie.isEmpty()){
            return null;
        }
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle(movie.get().getTitle());
        movieDTO.setDescription(movie.get().getDescription());
        movieDTO.setReleaseDate(movie.get().getReleaseDate());
        movieDTO.setIsActive(movie.get().getIsActive());
        movieDTO.setPrice(movie.get().getPrice());
        movieDTO.setDuration(movie.get().getDuration());
        movieDTO.setPosterUrl(movie.get().getPosterUrl());
        movieDTO.setTrailerUrl(movie.get().getTrailerUrl());
        return movieDTO;
    }
}
