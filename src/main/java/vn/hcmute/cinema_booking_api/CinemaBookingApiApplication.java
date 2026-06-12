package vn.hcmute.cinema_booking_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.entity.Role;
import vn.hcmute.cinema_booking_api.entity.Seat;
import vn.hcmute.cinema_booking_api.entity.User;
import vn.hcmute.cinema_booking_api.repository.RoleRepository;
import vn.hcmute.cinema_booking_api.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class CinemaBookingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaBookingApiApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoleRunner(RoleRepository roleRepository) {
		return args -> {
			if(roleRepository.count() == 0) {
				roleRepository.save(new Role(null, "ADMIN", null));
				roleRepository.save(new Role(null, "USER", null));
			}
			else
				System.out.println("Role already exists");
		};
	}
/*
	@Bean
	CommandLineRunner initSeatRunner(SeatRepository seatRepository) {
		return args -> {
			if(seatRepository.count() == 0) {
				char [] blocks = {'A', 'B', 'C'};
				int seatNum = 6;
				for(char block : blocks){
					for(int i = 0; i < seatNum; i++){
						String seatCode = block + String.valueOf(i);
						seatRepository.save(new Seat(null, seatCode, null));
					}
				}
			}
			else {
				System.out.println("Seat already exists");
			}
		};
	}

	@Bean
	CommandLineRunner initCategory(CategoryRepository categoryRepository) {
		return args -> {
			if(categoryRepository.count() == 0) {
				List<Category> categories = Arrays.asList(
						new Category(null, "Action", null),
						new Category(null, "Comedy", null),
						new Category(null, "Drama", null),
						new Category(null, "Horror", null),
						new Category(null, "Romance", null),
						new Category(null, "Science Fiction", null),
						new Category(null, "Documentary", null),
						new Category(null, "Animation", null)
				);
				categoryRepository.saveAll(categories);
			}
			else
				System.out.println("Category already exists");
		};
	}

	@Bean
	CommandLineRunner initAdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		return args -> {
			if(userRepository.count() == 0 || userRepository.findByEmail("admin@admin.com").isEmpty()) {
				User u = new User();
				u.setEmail("admin@admin.com");
				u.setPassword(passwordEncoder.encode("admin"));
				u.setAddress("VN");
				u.setRole(roleRepository.findByRoleName("ADMIN"));
				u.setFullName("Admin");
				u.setPhone("0123323123");
				u.setPoint(0);
				u.setUrlImage("https://res.cloudinary.com/demec8nev/image/upload/v1745039879/default_avatar_r7xkiv.png");
				userRepository.save(u);
			}
			else
				System.out.println("Admin user already exists");
		};
	}
 */
}
