package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Movie;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAllByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    List<Movie> findAllByIsActiveTrue();
    List<Movie> findAllByIsActiveFalse();
    List<Movie> findAllByCategory_CategoryNameContainingIgnoreCaseAndIsActiveTrue(String categoryName);
}
