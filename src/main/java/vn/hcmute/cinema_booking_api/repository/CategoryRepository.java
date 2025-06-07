package vn.hcmute.cinema_booking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.entity.Movie;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
