package vn.hcmute.cinema_booking_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.hcmute.cinema_booking_api.dto.request.BookingRequest;
import vn.hcmute.cinema_booking_api.dto.response.BookingResponse;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.repository.*;
import vn.hcmute.cinema_booking_api.services.Impl.BookingService;
import vn.hcmute.cinema_booking_api.utils.enums.OrderPayment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookedSeatRepository bookedSeatRepository;

    @Test
    public void testConcurrentCheckoutSameSeat() throws InterruptedException, ExecutionException {
        // Setup Roles and Users
        Role role = roleRepository.findByRoleName("USER");
        if (role == null) {
            role = new Role();
            role.setRoleName("USER");
            role = roleRepository.save(role);
        }

        String email1 = "test_user_1_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        User user1 = new User();
        user1.setEmail(email1);
        user1.setPsw("password123!");
        user1.setFullName("Test User One");
        user1.setPhone("0987" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 6));
        user1.setAddr("123 Street");
        user1.setRole(role);
        user1 = userRepository.save(user1);

        String email2 = "test_user_2_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        User user2 = new User();
        user2.setEmail(email2);
        user2.setPsw("password123!");
        user2.setFullName("Test User Two");
        user2.setPhone("0988" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 6));
        user2.setAddr("456 Street");
        user2.setRole(role);
        user2 = userRepository.save(user2);

        // Setup Category
        Category category = new Category();
        category.setCategoryName("Action - " + UUID.randomUUID().toString().substring(0, 8));
        category = categoryRepository.save(category);

        // Setup Movie
        Movie movie = new Movie();
        movie.setTitle("Concurrent Movie " + UUID.randomUUID().toString().substring(0, 8));
        movie.setPrice(100000);
        movie.setDuration(120);
        movie.setIsActive(true);
        movie.setDescription("Test Description");
        movie.setReleaseDate(LocalDate.now());
        movie.setPosterUrl("http://example.com/poster.jpg");
        movie.setTrailerUrl("http://example.com/trailer.mp4");
        movie.setCategory(category);
        movie = movieRepository.save(movie);

        // Setup Showtime
        ShowTime showTime = new ShowTime();
        showTime.setMovie(movie);
        showTime.setShowTime(LocalDateTime.now().plusDays(1));
        showTime = showTimeRepository.save(showTime);

        // Setup Seat
        String seatCode = "S_" + UUID.randomUUID().toString().substring(0, 3);
        Seat seat = new Seat();
        seat.setSeatCode(seatCode);
        seat = seatRepository.save(seat);

        // Create booking request for both users
        BookingRequest request1 = new BookingRequest();
        request1.setShowtimeId(showTime.getShowtimeId());
        request1.setSeatCodes(List.of(seatCode));
        request1.setPayment(OrderPayment.VNPAY);

        BookingRequest request2 = new BookingRequest();
        request2.setShowtimeId(showTime.getShowtimeId());
        request2.setSeatCodes(List.of(seatCode));
        request2.setPayment(OrderPayment.VNPAY);

        // Run concurrently
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        final String finalEmail1 = email1;
        final String finalEmail2 = email2;
        final ShowTime finalShowTime = showTime;
        final Seat finalSeat = seat;

        Callable<BookingResponse> task1 = () -> {
            barrier.await();
            return bookingService.checkout(finalEmail1, request1);
        };

        Callable<BookingResponse> task2 = () -> {
            barrier.await();
            return bookingService.checkout(finalEmail2, request2);
        };

        Future<BookingResponse> future1 = executor.submit(task1);
        Future<BookingResponse> future2 = executor.submit(task2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        try {
            future1.get();
            successCount.incrementAndGet();
        } catch (ExecutionException e) {
            failCount.incrementAndGet();
            System.out.println("Task1 failed: " + e.getCause());
            e.getCause().printStackTrace();
        }

        try {
            future2.get();
            successCount.incrementAndGet();
        } catch (ExecutionException e) {
            failCount.incrementAndGet();
            System.out.println("Task2 failed: " + e.getCause());
            e.getCause().printStackTrace();
        }

        // Verify only 1 succeeded and 1 failed
        assertEquals(1, successCount.get(), "Exactly one request should succeed");
        assertEquals(1, failCount.get(), "Exactly one request should fail due to seat concurrency");

        // Verify database state: only one BookedSeat should be recorded for this seat and showtime
        boolean exists = bookedSeatRepository.existsBySeatSeatIdAndShowTimeShowtimeId(finalSeat.getSeatId(), finalShowTime.getShowtimeId());
        assertTrue(exists, "Seat should be booked in database");

        // Clean up
        executor.shutdown();
    }
}
