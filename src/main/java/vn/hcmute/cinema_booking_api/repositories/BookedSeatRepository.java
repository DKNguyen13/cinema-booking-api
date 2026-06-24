package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.cinema_booking_api.entity.BookedSeat;

import java.util.Collection;
import java.util.List;

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    List<BookedSeat> findByShowTimeShowtimeId(Long showtimeId);

    boolean existsByShowTimeShowtimeIdAndSeatSeatId(Long showtimeId, Long seatId);

    @Query("""
        select bs.seat.seatId
        from BookedSeat bs
        where bs.showTime.showtimeId = :showtimeId
    """)
    List<Long> findBookedSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("""
        select bs.seat.seatId
        from BookedSeat bs
        where bs.showTime.showtimeId = :showtimeId
        and bs.seat.seatId in :seatIds
    """)
    List<Long> findBookedSeatIdsByShowtimeAndSeatIds(
            @Param("showtimeId") Long showtimeId,
            @Param("seatIds") Collection<Long> seatIds
    );
}