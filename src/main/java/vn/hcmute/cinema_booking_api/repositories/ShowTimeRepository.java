package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.hcmute.cinema_booking_api.entity.ShowTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    Optional<ShowTime> findByShowtimeId(Long showtimeId);
    List<ShowTime> findByMovieMovieIdAndShowTimeAfterOrderByShowTimeAsc(Long movieId, LocalDateTime now);
    List<ShowTime> findAllByOrderByShowTimeDesc();

    @Query("""
        SELECT s
        FROM ShowTime s
        WHERE s.showTime >= :startOfDay
        AND s.showTime < :endOfDay
        ORDER BY s.showTime ASC
    """)
    List<ShowTime> findByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
    FROM show_times s
    JOIN movies m ON s.movie_id = m.movie_id
    WHERE s.room_id = :roomId
      AND (:excludeId IS NULL OR s.showtime_id <> :excludeId)
      AND s.show_time < :newEnd
      AND TIMESTAMPADD(MINUTE, m.duration + 15, s.show_time) > :newStart
    """, nativeQuery = true)
    boolean existsRoomConflict(@Param("roomId") Long roomId, @Param("newStart") LocalDateTime newStart, @Param("newEnd") LocalDateTime newEnd, @Param("excludeId") Long excludeId);

    @Query("""
        SELECT s
        FROM ShowTime s
        WHERE s.room.roomId = :roomId
        ORDER BY s.showTime ASC
    """)
    List<ShowTime> findByRoomId(@Param("roomId") Long roomId);

    @Query("""
        SELECT s
        FROM ShowTime s
        WHERE s.room.roomId = :roomId
        AND s.showTime >= :from
        AND s.showTime < :to
        ORDER BY s.showTime ASC
    """)
    List<ShowTime> findByRoomAndDateRange(
            @Param("roomId") Long roomId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}