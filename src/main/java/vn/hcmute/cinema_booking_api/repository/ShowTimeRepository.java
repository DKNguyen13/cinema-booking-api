package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.ShowTime;

import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long>{
    List<ShowTime> getShowTimesByMovie_MovieId(Long movieId);
}
