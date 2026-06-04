package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Discount;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findDiscountsByActive(boolean active);
    Optional<Discount> findByDiscountCode(String discountCode);
}
