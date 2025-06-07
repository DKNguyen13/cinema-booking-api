package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.UserDTO;

import java.util.List;

public interface IUserService {
    Boolean checkExistEmail(String email);

    //Find user by email
    UserDTO findByEmail(String email);

    boolean checkExistEmailOrPhone(String email, String phone);

    void saveUser(UserDTO userDTO);

    List<UserDTO> getAllUser();
}
