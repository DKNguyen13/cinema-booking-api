package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.admin.*;
import vn.hcmute.cinema_booking_api.dto.movie.MovieRequest;
import java.time.LocalDate;
import java.util.List;

public interface IAdminService {
    // Users
    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserDetail(Long userId);
    void setStatusUser(Long userId);

    // Movies
    List<AdminMovieResponse> adminGetAllMovies();
    AdminMovieResponse adminCreateMovie(MovieRequest req);
    AdminMovieResponse adminUpdateMovie(Long id, MovieRequest req);
    void adminToggleMovie(Long id);

    // ShowTimes
    List<AdminShowTimeResponse> adminGetAllShowTimes();
    AdminShowTimeResponse adminCreateShowTime(AdminShowTimeRequest req);
    AdminShowTimeResponse adminUpdateShowTime(Long id, AdminShowTimeRequest req);
    void adminDeleteShowTime(Long id);
    List<AdminShowTimeResponse> adminGetShowTimesByRoom(Long roomId, LocalDate date);

    // Rooms
    List<AdminRoomResponse> adminGetAllRooms();
    AdminRoomResponse adminCreateRoom(AdminRoomRequest req);
    AdminRoomResponse adminUpdateRoom(Long id, AdminRoomRequest req);
    void adminToggleRoom(Long id);

    // Orders
    List<AdminOrderResponse> adminGetAllOrders();

    // Dashboard
    DashboardStatsResponse getDashboardStats();
    List<AvailableTimeSlotResponse> getAvailableShowTimeSlots(Long movieId, Long roomId, LocalDate date);
}
