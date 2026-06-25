package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.cinema_booking_api.entity.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserUserIdOrderByShowTimeDateTimeDesc(Long userId);
    List<Ticket> findByOrderOrderIdAndUserUserId(Long orderId, Long userId);
    Optional<Ticket> findByQrToken(String qrToken);
}