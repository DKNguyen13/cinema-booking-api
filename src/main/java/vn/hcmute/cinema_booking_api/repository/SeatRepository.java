package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Seat;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.seatId NOT IN (SELECT bs.seat.seatId FROM BookedSeat bs WHERE bs.showTime.showtimeId = :showtimeId)")
    List<Seat> findAvailableSeatsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("SELECT s FROM Seat s WHERE s.seatId IN (SELECT bs.seat.seatId FROM BookedSeat bs WHERE bs.showTime.showtimeId = :showTId)")
    List<Seat> findNotAvailableSeatByShowTimeId(@Param("showTId") Long showtimeId);
}
