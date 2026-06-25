package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.cinema_booking_api.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}