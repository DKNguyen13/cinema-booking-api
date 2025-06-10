package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.ShowTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long>{
    List<ShowTime> getShowTimesByMovie_MovieId(Long movieId);

    @Query("SELECT s FROM ShowTime s WHERE s.showTime BETWEEN :start AND :end")
    List<ShowTime> findShowTimesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
