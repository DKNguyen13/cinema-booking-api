package vn.hcmute.cinema_booking_api.controllers.Admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.admin.*;
import vn.hcmute.cinema_booking_api.dto.movie.MovieRequest;
import vn.hcmute.cinema_booking_api.services.IAdminService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final IAdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.getDashboardStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserListResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.getUserDetail(id)));
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleUser(@PathVariable Long id) {
        adminService.setStatusUser(id);
        return ResponseEntity.ok(ApiResponse.success("Updated"));
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<AdminMovieResponse>>> getMovies() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.adminGetAllMovies()));
    }

    @PostMapping("/movies")
    public ResponseEntity<ApiResponse<AdminMovieResponse>> createMovie(@Valid @RequestBody MovieRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Created", adminService.adminCreateMovie(req)));
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<AdminMovieResponse>> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Updated", adminService.adminUpdateMovie(id, req)));
    }

    @PutMapping("/movies/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleMovie(@PathVariable Long id) {
        adminService.adminToggleMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Toggled"));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<ApiResponse<List<AdminShowTimeResponse>>> getShowTimes() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.adminGetAllShowTimes()));
    }

    @PostMapping("/showtimes")
    public ResponseEntity<ApiResponse<AdminShowTimeResponse>> createShowTime(@Valid @RequestBody AdminShowTimeRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Created", adminService.adminCreateShowTime(req)));
    }

    @PutMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<AdminShowTimeResponse>> updateShowTime(@PathVariable Long id, @Valid @RequestBody AdminShowTimeRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Updated", adminService.adminUpdateShowTime(id, req)));
    }

    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowTime(@PathVariable Long id) {
        adminService.adminDeleteShowTime(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted"));
    }

    @GetMapping("/showtimes/room/{roomId}")
    public ResponseEntity<ApiResponse<List<AdminShowTimeResponse>>> getShowTimesByRoom(@PathVariable Long roomId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.adminGetShowTimesByRoom(roomId, date)));
    }

    @GetMapping("/showtimes/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableTimeSlotResponse>>> getAvailableShowTimeSlots(@RequestParam Long movieId, @RequestParam Long roomId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.getAvailableShowTimeSlots(movieId, roomId, date)));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<AdminRoomResponse>>> getRooms() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.adminGetAllRooms()));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<AdminRoomResponse>> createRoom(@Valid @RequestBody AdminRoomRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Created", adminService.adminCreateRoom(req)));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<AdminRoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoomRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success("Updated", adminService.adminUpdateRoom(id, req)));
    }

    @PutMapping("/rooms/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleRoom(@PathVariable Long id) {
        adminService.adminToggleRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Toggled"));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getOrders() {
        return ResponseEntity.ok(ApiResponse.success("OK", adminService.adminGetAllOrders()));
    }
}