package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Movie;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsActiveTrueOrderByReleaseDateDesc();
    @EntityGraph(attributePaths = "categories")
    List<Movie> findAllByOrderByReleaseDateDesc();
    Optional<Movie> findByMovieId(Long movieId);
    Optional<Movie> findByMovieIdAndIsActiveTrue(Long movieId);
    List<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrueOrderByReleaseDateDesc(String keyword);
    long countByIsActiveTrue();

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categories c WHERE c.categoryId = :categoryId AND m.isActive = true ORDER BY m.releaseDate DESC")
    List<Movie> findActiveMoviesByCategoryId(Long categoryId);
}
