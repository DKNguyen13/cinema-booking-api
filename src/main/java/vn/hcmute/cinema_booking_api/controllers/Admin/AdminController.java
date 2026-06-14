package vn.hcmute.cinema_booking_api.controllers.Admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.admin.UserDetailResponse;
import vn.hcmute.cinema_booking_api.dto.admin.UserListResponse;
import vn.hcmute.cinema_booking_api.services.Impl.AdminService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserListResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Get all users successful!", adminService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Get user detail successful!", adminService.getUserDetail(id)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        adminService.setStatusUser(id);
        return ResponseEntity.ok(ApiResponse.success("Update user status successfully"));
    }
}