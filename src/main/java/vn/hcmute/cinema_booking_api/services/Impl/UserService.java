package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.UserDTO;
import vn.hcmute.cinema_booking_api.entity.Role;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.exception.ResourceNotFoundException;
import vn.hcmute.cinema_booking_api.repository.RoleRepository;
import vn.hcmute.cinema_booking_api.repository.UserRepository;
import vn.hcmute.cinema_booking_api.services.IUserService;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailService mailService;

    @Override
    public Boolean checkExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    //Find user by email
    @Override
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

    @Override
    public boolean checkExistEmailOrPhone(String email, String phone) {
        return userRepository.existsByEmailAndPhone(email, phone);
    }

    @Override
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

    @Override
    public List<UserDTO> getAllUser(){
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.getRole().getRoleName().equals("USER"))
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setEmail(user.getEmail());
                    userDTO.setAddr(user.getAddr());
                    userDTO.setFullName(user.getFullName());
                    userDTO.setPhone(user.getPhone());
                    userDTO.setUrlImage(user.getUrlImage());
                    return userDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public void resetPassword(String email){
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }
        User u = userOpt.get();
        String newPass = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        mailService.sendNewPass(u.getEmail(), newPass);
        u.setPsw(passwordEncoder.encode(newPass));
        userRepository.save(u);
    }
}
