package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.ShowTime;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long>{
}
