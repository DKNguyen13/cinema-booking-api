package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {
    Optional<ShowTime> findByShowtimeId(Long showtimeId);
    List<ShowTime> findByMovieMovieIdAndShowTimeAfterOrderByShowTimeAsc(Long movieId, LocalDateTime now);

    @Query("""
        SELECT s
        FROM ShowTime s
        WHERE s.showTime >= :startOfDay AND s.showTime < :endOfDay
        ORDER BY s.showTime ASC
    """)
    List<ShowTime> findByDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
}