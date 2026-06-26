package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.admin.*;
import vn.hcmute.cinema_booking_api.dto.movie.MovieRequest;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.*;
import vn.hcmute.cinema_booking_api.services.IAdminService;
import vn.hcmute.cinema_booking_api.utils.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private static final int CLEANUP_MINUTES = 15;
    private static final int SLOT_STEP_MINUTES = 30;
    private static final int CINEMA_OPEN_HOUR = 8;
    private static final int CINEMA_CLOSE_HOUR = 23;

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final ShowTimeRepository showTimeRepository;
    private final RoomRepository roomRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserList).toList();
    }

    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return toUserDetail(user);
    }

    @Override
    public void setStatusUser(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
    }

    @Override
    public List<AdminMovieResponse> adminGetAllMovies() {
        return movieRepository.findAllByOrderByReleaseDateDesc()
                .stream()
                .map(this::toAdminMovie)
                .toList();
    }

    @Override
    @Transactional
    public AdminMovieResponse adminCreateMovie(MovieRequest req) {
        validateMovieRequest(req);

        List<Category> categories = resolveCategories(req.getCategoryIds());

        Movie movie = Movie.builder()
                .title(req.getTitle().trim())
                .description(req.getDescription().trim())
                .duration(req.getDuration())
                .price(req.getPrice())
                .posterUrl(req.getPosterUrl().trim())
                .trailerUrl(req.getTrailerUrl().trim())
                .releaseDate(req.getReleaseDate())
                .isActive(true)
                .categories(categories)
                .build();

        return toAdminMovie(movieRepository.save(movie));
    }

    @Override
    @Transactional
    public AdminMovieResponse adminUpdateMovie(Long id, MovieRequest req) {
        validateMovieRequest(req);

        Movie movie = movieRepository.findByMovieId(id)
                .orElseThrow(() -> new BadRequestException("Movie not found"));

        movie.setTitle(req.getTitle().trim());
        movie.setDescription(req.getDescription().trim());
        movie.setDuration(req.getDuration());
        movie.setPrice(req.getPrice());
        movie.setPosterUrl(req.getPosterUrl().trim());
        movie.setTrailerUrl(req.getTrailerUrl().trim());
        movie.setReleaseDate(req.getReleaseDate());
        movie.setCategories(resolveCategories(req.getCategoryIds()));

        return toAdminMovie(movieRepository.save(movie));
    }

    @Override
    public void adminToggleMovie(Long id) {
        Movie movie = movieRepository.findByMovieId(id)
                .orElseThrow(() -> new BadRequestException("Movie not found"));

        movie.setIsActive(!Boolean.TRUE.equals(movie.getIsActive()));
        movieRepository.save(movie);
    }

    @Override
    public List<AdminShowTimeResponse> adminGetAllShowTimes() {
        return showTimeRepository.findAllByOrderByShowTimeDesc()
                .stream()
                .map(this::toAdminShowTime)
                .toList();
    }

    @Override
    @Transactional
    public AdminShowTimeResponse adminCreateShowTime(AdminShowTimeRequest req) {
        Movie movie = getActiveMovie(req.getMovieId());
        Room room = getActiveRoom(req.getRoomId());

        validateShowTime(req.getShowTime(), movie, room, null);

        ShowTime showTime = ShowTime.builder()
                .movie(movie)
                .room(room)
                .showTime(req.getShowTime())
                .build();

        return toAdminShowTime(showTimeRepository.save(showTime));
    }

    @Override
    @Transactional
    public AdminShowTimeResponse adminUpdateShowTime(Long id, AdminShowTimeRequest req) {
        ShowTime showTime = showTimeRepository.findByShowtimeId(id)
                .orElseThrow(() -> new BadRequestException("Showtime not found"));

        Movie movie = getActiveMovie(req.getMovieId());
        Room room = getActiveRoom(req.getRoomId());

        validateShowTime(req.getShowTime(), movie, room, id);

        showTime.setMovie(movie);
        showTime.setRoom(room);
        showTime.setShowTime(req.getShowTime());

        return toAdminShowTime(showTimeRepository.save(showTime));
    }

    @Override
    public void adminDeleteShowTime(Long id) {
        ShowTime showTime = showTimeRepository.findByShowtimeId(id)
                .orElseThrow(() -> new BadRequestException("Showtime not found"));

        showTimeRepository.delete(showTime);
    }

    @Override
    public List<AdminShowTimeResponse> adminGetShowTimesByRoom(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("Room not found"));

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        return showTimeRepository.findByRoomAndDateRange(room.getRoomId(), from, to)
                .stream()
                .map(this::toAdminShowTime)
                .toList();
    }

    @Override
    public List<AvailableTimeSlotResponse> getAvailableShowTimeSlots(Long movieId, Long roomId, LocalDate date) {
        Movie movie = getActiveMovie(movieId);
        Room room = getActiveRoom(roomId);

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime dayStart = date.atTime(CINEMA_OPEN_HOUR, 0);
        LocalDateTime dayEnd = date.atTime(CINEMA_CLOSE_HOUR, 0);

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        List<ShowTime> bookedShowTimes = showTimeRepository.findByRoomAndDateRange(
                room.getRoomId(),
                from,
                to
        );

        List<AvailableTimeSlotResponse> result = new ArrayList<>();

        int movieDuration = movie.getDuration();
        int requiredMinutes = movieDuration + CLEANUP_MINUTES;

        LocalDateTime cursor = dayStart;

        for (ShowTime booked : bookedShowTimes) {
            LocalDateTime busyStart = booked.getShowTime();
            LocalDateTime busyEnd = busyStart
                    .plusMinutes(booked.getMovie().getDuration())
                    .plusMinutes(CLEANUP_MINUTES);

            while (!cursor.plusMinutes(requiredMinutes).isAfter(busyStart)) {
                result.add(toAvailableSlot(cursor, movieDuration));
                cursor = cursor.plusMinutes(SLOT_STEP_MINUTES);
            }

            if (cursor.isBefore(busyEnd)) {
                cursor = busyEnd;
            }
        }

        while (!cursor.plusMinutes(requiredMinutes).isAfter(dayEnd)) {
            result.add(toAvailableSlot(cursor, movieDuration));
            cursor = cursor.plusMinutes(SLOT_STEP_MINUTES);
        }

        return result;
    }

    @Override
    public List<AdminRoomResponse> adminGetAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::toAdminRoom)
                .toList();
    }

    @Override
    public AdminRoomResponse adminCreateRoom(AdminRoomRequest req) {
        validateRoomRequest(req);

        Room room = Room.builder()
                .roomName(req.getRoomName().trim())
                .totalSeats(req.getTotalSeats())
                .isActive(true)
                .build();

        return toAdminRoom(roomRepository.save(room));
    }

    @Override
    public AdminRoomResponse adminUpdateRoom(Long id, AdminRoomRequest req) {
        validateRoomRequest(req);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found"));

        room.setRoomName(req.getRoomName().trim());
        room.setTotalSeats(req.getTotalSeats());

        return toAdminRoom(roomRepository.save(room));
    }

    @Override
    public void adminToggleRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found"));

        room.setIsActive(!Boolean.TRUE.equals(room.getIsActive()));
        roomRepository.save(room);
    }

    @Override
    public List<AdminOrderResponse> adminGetAllOrders() {
        return orderRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(this::toAdminOrder)
                .toList();
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime startToday = LocalDate.now().atStartOfDay();
        LocalDateTime endToday = startToday.plusDays(1);

        Long totalRevenue = orderRepository.sumRevenueByStatus(OrderStatus.PAID);
        Long revenueToday = orderRepository.sumRevenueByStatusAndDateRange(
                OrderStatus.PAID,
                startToday,
                endToday
        );

        return DashboardStatsResponse.builder()
                .totalMovies(movieRepository.countByIsActiveTrue())
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .totalRevenue(totalRevenue == null ? 0L : totalRevenue)
                .totalShowtimes(showTimeRepository.count())
                .ordersToday(orderRepository.countByCreatedDateBetween(startToday, endToday))
                .revenueToday(revenueToday == null ? 0L : revenueToday)
                .build();
    }

    private void validateMovieRequest(MovieRequest req) {
        if (req == null) {
            throw new BadRequestException("Movie request is required");
        }

        if (isBlank(req.getTitle())) {
            throw new BadRequestException("Movie title is required");
        }

        if (isBlank(req.getDescription())) {
            throw new BadRequestException("Movie description is required");
        }

        if (req.getDuration() == null || req.getDuration() <= 0) {
            throw new BadRequestException("Movie duration must be greater than 0");
        }

        if (req.getPrice() == null || req.getPrice() < 0) {
            throw new BadRequestException("Movie price is invalid");
        }

        if (isBlank(req.getPosterUrl())) {
            throw new BadRequestException("Poster URL is required");
        }

        if (isBlank(req.getTrailerUrl())) {
            throw new BadRequestException("Trailer URL is required");
        }

        if (req.getReleaseDate() == null) {
            throw new BadRequestException("Release date is required");
        }

        if (req.getCategoryIds() == null || req.getCategoryIds().isEmpty()) {
            throw new BadRequestException("At least one category is required");
        }
    }

    private void validateRoomRequest(AdminRoomRequest req) {
        if (req == null) {
            throw new BadRequestException("Room request is required");
        }

        if (isBlank(req.getRoomName())) {
            throw new BadRequestException("Room name is required");
        }

        if (req.getTotalSeats() == null || req.getTotalSeats() <= 0) {
            throw new BadRequestException("Total seats must be greater than 0");
        }
    }

    private void validateShowTime(LocalDateTime startTime, Movie movie, Room room, Long excludeId) {
        if (startTime == null) {
            throw new BadRequestException("Showtime is required");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Không thể tạo suất chiếu trong quá khứ");
        }

        LocalDateTime cinemaOpen = startTime.toLocalDate().atTime(CINEMA_OPEN_HOUR, 0);
        LocalDateTime cinemaClose = startTime.toLocalDate().atTime(CINEMA_CLOSE_HOUR, 0);

        LocalDateTime endTimeWithCleanup = startTime
                .plusMinutes(movie.getDuration())
                .plusMinutes(CLEANUP_MINUTES);

        if (startTime.isBefore(cinemaOpen) || endTimeWithCleanup.isAfter(cinemaClose)) {
            throw new BadRequestException("Suất chiếu phải nằm trong khung giờ 08:00 - 23:00");
        }

        boolean conflict = showTimeRepository.existsRoomConflict(
                room.getRoomId(),
                startTime,
                endTimeWithCleanup,
                excludeId
        );

        if (conflict) {
            throw new BadRequestException(
                    "Phòng " + room.getRoomName() + " đã có suất chiếu trùng giờ. Vui lòng chọn khung giờ khác."
            );
        }
    }

    private Movie getActiveMovie(Long movieId) {
        Movie movie = movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new BadRequestException("Movie not found"));

        if (!Boolean.TRUE.equals(movie.getIsActive())) {
            throw new BadRequestException("Movie is inactive");
        }

        return movie;
    }

    private Room getActiveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("Room not found"));

        if (!Boolean.TRUE.equals(room.getIsActive())) {
            throw new BadRequestException("Room is inactive");
        }

        return room;
    }

    private List<Category> resolveCategories(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("At least one category is required");
        }

        return ids.stream()
                .distinct()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new BadRequestException("Category not found: " + categoryId)))
                .toList();
    }

    private AvailableTimeSlotResponse toAvailableSlot(LocalDateTime startTime, int movieDuration) {
        LocalDateTime endTime = startTime.plusMinutes(movieDuration);

        return AvailableTimeSlotResponse.builder()
                .startTime(startTime)
                .endTime(endTime)
                .label(String.format(
                        "%02d:%02d - %02d:%02d",
                        startTime.getHour(),
                        startTime.getMinute(),
                        endTime.getHour(),
                        endTime.getMinute()
                ))
                .build();
    }

    private AdminMovieResponse toAdminMovie(Movie movie) {
        return AdminMovieResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .price(movie.getPrice())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .releaseDate(movie.getReleaseDate())
                .isActive(movie.getIsActive())
                .categories(movie.getCategories() != null
                        ? movie.getCategories().stream().map(Category::getCategoryName).toList()
                        : List.of())
                .build();
    }

    private AdminShowTimeResponse toAdminShowTime(ShowTime showTime) {
        LocalDateTime endTime = showTime.getShowTime()
                .plusMinutes(showTime.getMovie().getDuration());

        return AdminShowTimeResponse.builder()
                .showtimeId(showTime.getShowtimeId())
                .showTime(showTime.getShowTime())
                .endTime(endTime)
                .movieId(showTime.getMovie().getMovieId())
                .movieTitle(showTime.getMovie().getTitle())
                .roomId(showTime.getRoom().getRoomId())
                .roomName(showTime.getRoom().getRoomName())
                .build();
    }

    private AdminRoomResponse toAdminRoom(Room room) {
        return AdminRoomResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .totalSeats(room.getTotalSeats())
                .isActive(room.getIsActive())
                .build();
    }

    private AdminOrderResponse toAdminOrder(Order order) {
        return AdminOrderResponse.builder()
                .orderId(order.getOrderId())
                .userEmail(order.getUser().getEmail())
                .userFullName(order.getUser().getFullName())
                .movieTitle(order.getShowtime().getMovie().getTitle())
                .showTime(order.getShowtime().getShowTime())
                .finalPrice(order.getFinalPrice())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .payment(order.getPayment())
                .createdDate(order.getCreatedDate())
                .paidAt(order.getPaidAt())
                .build();
    }

    private UserListResponse toUserList(User user) {
        return UserListResponse.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .build();
    }

    private UserDetailResponse toUserDetail(User user) {
        return UserDetailResponse.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .point(user.getPoint())
                .imageUrl(user.getImageUrl())
                .isActive(user.getIsActive())
                .roleName(user.getRole().getRoleName())
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}