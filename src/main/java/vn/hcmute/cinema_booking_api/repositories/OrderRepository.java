package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Order;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUserIdOrderByCreatedDateDesc(Long userId);
    List<Order> findAllByOrderByCreatedDateDesc();
    long countByStatus(OrderStatus status);
    long countByCreatedDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT COALESCE(SUM(o.finalPrice),0)
        FROM Order o
        WHERE o.status = :status
    """)
    Long sumRevenueByStatus(@Param("status") OrderStatus status);

    @Query("""
        SELECT COALESCE(SUM(o.finalPrice),0)
        FROM Order o
        WHERE o.status = :status
        AND o.paidAt >= :from
        AND o.paidAt < :to
    """)
    Long sumRevenueByStatusAndDateRange(@Param("status") OrderStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
