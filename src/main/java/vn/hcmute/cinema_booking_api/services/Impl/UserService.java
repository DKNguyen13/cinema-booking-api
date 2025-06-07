package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.UserDTO;
import vn.hcmute.cinema_booking_api.entity.Role;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.repository.RoleRepository;
import vn.hcmute.cinema_booking_api.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepository roleRepository;

    public Boolean checkExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    //Find user by email
    public UserDTO findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.get().getEmail());
            userDTO.setPsw("");
            userDTO.setAddr(user.get().getAddr());
            userDTO.setFullName(user.get().getFullName());
            userDTO.setPhone(user.get().getPhone());
            userDTO.setUrlImage(user.get().getUrlImage());
            userDTO.setToken("");
            return userDTO;
        }
        return null;
    }

    public boolean checkExistEmailOrPhone(String email, String phone) {
        Boolean existEmail = userRepository.existsByEmail(email);
        Boolean existPhone = userRepository.existsByPhone(phone);
        return !existEmail && !existPhone;
    }

    public void saveUser(UserDTO userDTO) {
        Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
        Role role = roleRepository.findByRoleName("USER");
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail(userDTO.getEmail());
            newUser.setPsw(passwordEncoder.encode(userDTO.getPsw()));
            newUser.setFullName(userDTO.getFullName());
            newUser.setAddr(userDTO.getAddr());
            newUser.setPhone(userDTO.getPhone());
            newUser.setRole(role);
            newUser.setUrlImage("https://res.cloudinary.com/demec8nev/image/upload/v1745039879/default_avatar_r7xkiv.png");
            userRepository.save(newUser);
        }
        else {
            User existingUser = user.get();
            existingUser.setPsw(passwordEncoder.encode(userDTO.getPsw()));
            existingUser.setFullName(userDTO.getFullName());
            existingUser.setAddr(userDTO.getAddr());
            existingUser.setPhone(userDTO.getPhone());
            existingUser.setRole(role);
            userRepository.save(existingUser);
        }
    }
}
