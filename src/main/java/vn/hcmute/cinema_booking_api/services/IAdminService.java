package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.admin.UserDetailResponse;
import vn.hcmute.cinema_booking_api.dto.admin.UserListResponse;

import java.util.List;

public interface IAdminService {
    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserDetail(Long userId);
    void setStatusUser(Long userId);
}