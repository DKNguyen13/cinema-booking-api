package vn.hcmute.cinema_booking_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.cinema_booking_api.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    @Query("select u from User u join fetch u.role where u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailOrPhone(String email, String phone);
}