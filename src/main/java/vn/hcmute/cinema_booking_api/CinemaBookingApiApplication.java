package vn.hcmute.cinema_booking_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class CinemaBookingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaBookingApiApplication.class, args);
	}

	/*
	@Bean
	CommandLineRunner initRoleRunner(RoleRepository roleRepository) {
		return args -> {
			if(roleRepository.count() == 0) {
				roleRepository.save(new Role(null, "ADMIN", null));
				roleRepository.save(new Role(null, "STAFF", null));
				roleRepository.save(new Role(null, "USER", null));
			}
			else
				System.out.println("Role already exists");
		};
	}
	 */

	/*
	@Bean
	CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
			Role userRole = roleRepository.findByRoleName("USER").orElseThrow();
			Role staffRole = roleRepository.findByRoleName("STAFF").orElseThrow();

			List<User> users = List.of(

					User.builder()
							.email("admin@admin.com")
							.password(passwordEncoder.encode("admin@@"))
							.fullName("Cinema Admin")
							.phone("0900000000")
							.address("Ho Chi Minh City")
							.point(0)
							.isActive(true)
							.role(adminRole)
							.build(),

					User.builder()
							.email("user1@gmail.com")
							.password(passwordEncoder.encode("123456"))
							.fullName("Nguyen Van A")
							.phone("0900000001")
							.address("Ho Chi Minh City")
							.point(120)
							.isActive(true)
							.role(userRole)
							.build(),

					User.builder()
							.email("user2@gmail.com")
							.password(passwordEncoder.encode("123456"))
							.fullName("Tran Thi B")
							.phone("0900000002")
							.address("Binh Duong")
							.point(250)
							.isActive(true)
							.role(userRole)
							.build(),

					User.builder()
							.email("staff@cinema.com")
							.password(passwordEncoder.encode("123456"))
							.fullName("Cinema Staff")
							.phone("0900000003")
							.address("Ho Chi Minh City")
							.point(0)
							.isActive(true)
							.role(staffRole)
							.build()
			);

			for (User user : users) {
				if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
					userRepository.save(user);
					System.out.println("Created: " + user.getEmail());
				}
			}
		};
	}
	*/

	/*
	@Bean
	CommandLineRunner initCategoryAndMovie(CategoryRepository categoryRepository, MovieRepository movieRepository) {
		return args -> {
			if (categoryRepository.count() == 0) {
				categoryRepository.saveAll(List.of(
						Category.builder().categoryName("Action").build(),
						Category.builder().categoryName("Comedy").build(),
						Category.builder().categoryName("Drama").build(),
						Category.builder().categoryName("Horror").build(),
						Category.builder().categoryName("Romance").build(),
						Category.builder().categoryName("Science Fiction").build(),
						Category.builder().categoryName("Documentary").build(),
						Category.builder().categoryName("Animation").build()
				));
			}

			if (movieRepository.count() == 0) {
				Category action = categoryRepository.findByCategoryNameIgnoreCase("Action").orElseThrow();
				Category comedy = categoryRepository.findByCategoryNameIgnoreCase("Comedy").orElseThrow();
				Category drama = categoryRepository.findByCategoryNameIgnoreCase("Drama").orElseThrow();
				Category horror = categoryRepository.findByCategoryNameIgnoreCase("Horror").orElseThrow();
				Category romance = categoryRepository.findByCategoryNameIgnoreCase("Romance").orElseThrow();
				Category sciFi = categoryRepository.findByCategoryNameIgnoreCase("Science Fiction").orElseThrow();
				Category animation = categoryRepository.findByCategoryNameIgnoreCase("Animation").orElseThrow();

				movieRepository.saveAll(List.of(
						Movie.builder()
								.title("Avengers: Endgame")
								.description("After the devastating events of Infinity War, the Avengers assemble once more to reverse Thanos' actions.")
								.duration(181)
								.price(90000)
								.posterUrl("https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=TcMBFSGVi1c")
								.releaseDate(LocalDate.of(2019, 4, 26))
								.isActive(true)
								.categories(List.of(action, sciFi))
								.build(),

						Movie.builder()
								.title("Inside Out 2")
								.description("Riley enters her teenage years and faces new emotions inside her mind.")
								.duration(96)
								.price(75000)
								.posterUrl("https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=LEjhY15eCx0")
								.releaseDate(LocalDate.of(2024, 6, 14))
								.isActive(true)
								.categories(List.of(animation, comedy))
								.build(),

						Movie.builder()
								.title("The Conjuring")
								.description("Paranormal investigators help a family terrorized by a dark presence in their farmhouse.")
								.duration(112)
								.price(80000)
								.posterUrl("https://image.tmdb.org/t/p/w500/wVYREutTvI2tmxr6ujrHT704wGF.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=k10ETZ41q5o")
								.releaseDate(LocalDate.of(2013, 7, 19))
								.isActive(true)
								.categories(List.of(horror))
								.build(),

						Movie.builder()
								.title("La La Land")
								.description("A pianist and an actress fall in love while pursuing their dreams in Los Angeles.")
								.duration(128)
								.price(85000)
								.posterUrl("https://image.tmdb.org/t/p/w500/uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=0pdqf4P9MB8")
								.releaseDate(LocalDate.of(2016, 12, 9))
								.isActive(true)
								.categories(List.of(romance, drama))
								.build(),

						Movie.builder()
								.title("Interstellar")
								.description("A team of explorers travels through a wormhole in space in an attempt to ensure humanity's survival.")
								.duration(169)
								.price(95000)
								.posterUrl("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=zSWdZVtXT7E")
								.releaseDate(LocalDate.of(2014, 11, 7))
								.isActive(true)
								.categories(List.of(sciFi, drama))
								.build(),

						Movie.builder()
								.title("Oppenheimer")
								.description("The story of physicist J. Robert Oppenheimer and the creation of the atomic bomb during World War II.")
								.duration(180)
								.price(95000)
								.posterUrl("https://image.tmdb.org/t/p/w500/ptpr0kGAckfQkJeJIt8st5dglvd.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=uYPbbksJxIg")
								.releaseDate(LocalDate.of(2023, 7, 21))
								.isActive(true)
								.categories(List.of(drama))
								.build(),

						Movie.builder()
								.title("Spider-Man: No Way Home")
								.description("Peter Parker seeks help from Doctor Strange after his identity is revealed, opening the multiverse.")
								.duration(148)
								.price(90000)
								.posterUrl("https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=JfVOs4VSpmA")
								.releaseDate(LocalDate.of(2021, 12, 17))
								.isActive(true)
								.categories(List.of(action, sciFi))
								.build(),

						Movie.builder()
								.title("Avatar: The Way of Water")
								.description("Jake Sully and Neytiri protect their family while discovering the oceans of Pandora.")
								.duration(192)
								.price(100000)
								.posterUrl("https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=d9MyW72ELq0")
								.releaseDate(LocalDate.of(2022, 12, 16))
								.isActive(true)
								.categories(List.of(action, sciFi))
								.build(),

						Movie.builder()
								.title("Top Gun: Maverick")
								.description("After more than thirty years of service, Maverick returns to train a new generation of elite fighter pilots.")
								.duration(131)
								.price(85000)
								.posterUrl("https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=giXco2jaZ_4")
								.releaseDate(LocalDate.of(2022, 5, 27))
								.isActive(true)
								.categories(List.of(action, drama))
								.build(),

						Movie.builder()
								.title("Coco")
								.description("A young boy journeys to the Land of the Dead to uncover his family's history and pursue his dream of music.")
								.duration(105)
								.price(75000)
								.posterUrl("https://image.tmdb.org/t/p/w500/gGEsBPAijhVUFoiNpgZXqRVWJt2.jpg")
								.trailerUrl("https://www.youtube.com/watch?v=Ga6RYejo6Hk")
								.releaseDate(LocalDate.of(2017, 11, 22))
								.isActive(true)
								.categories(List.of(animation, comedy))
								.build()
				));
			} else {
				System.out.println("Movie already exists");
			}
		};
	}
	*/

	/*
	@Bean
	CommandLineRunner initRoomSeatAndShowTime(RoomRepository roomRepository, SeatRepository seatRepository, MovieRepository movieRepository, ShowTimeRepository showTimeRepository) {
		return args -> {

			if (roomRepository.count() == 0) {
				roomRepository.saveAll(List.of(
						Room.builder().roomName("Room 1").totalSeats(80).build(),
						Room.builder().roomName("Room 2").totalSeats(80).build(),
						Room.builder().roomName("Room 3").totalSeats(80).build(),
						Room.builder().roomName("Room 4").totalSeats(80).build(),
						Room.builder().roomName("Room 5").totalSeats(80).build()
				));
			}

			List<Room> rooms = roomRepository.findAll();

			if (seatRepository.count() == 0) {
				List<Seat> seats = new ArrayList<>();
				List<String> rows = List.of("A", "B", "C", "D", "E", "F", "G", "H");

				for (Room room : rooms) {
					for (String row : rows) {
						for (int number = 1; number <= 10; number++) {
							seats.add(
									Seat.builder()
											.seatCode(row + number)
											.room(room)
											.build()
							);
						}
					}
				}

				seatRepository.saveAll(seats);
				System.out.println("Created " + seats.size() + " seats");
			} else {
				System.out.println("Seat already exists");
			}

			if (showTimeRepository.count() > 0 || movieRepository.count() == 0) {
				System.out.println("ShowTime already exists");
				return;
			}

			List<Movie> movies = movieRepository.findAll();

			if (movies.isEmpty() || rooms.isEmpty()) return;

			List<LocalTime> timeSlots = List.of(
					LocalTime.of(8, 30),
					LocalTime.of(10, 45),
					LocalTime.of(13, 0),
					LocalTime.of(15, 15),
					LocalTime.of(17, 30),
					LocalTime.of(19, 45),
					LocalTime.of(22, 0)
			);

			List<ShowTime> showTimes = new ArrayList<>();

			for (int day = 1; day <= 7; day++) {
				LocalDate showDate = LocalDate.now().plusDays(day);

				for (int movieIndex = 0; movieIndex < movies.size(); movieIndex++) {
					Movie movie = movies.get(movieIndex);

					for (int slotIndex = 0; slotIndex < timeSlots.size(); slotIndex++) {
						Room room = rooms.get((movieIndex + slotIndex) % rooms.size());

						showTimes.add(
								ShowTime.builder()
										.showTime(LocalDateTime.of(showDate, timeSlots.get(slotIndex)))
										.movie(movie)
										.room(room)
										.build()
						);
					}
				}
			}

			showTimeRepository.saveAll(showTimes);
			System.out.println("Created " + showTimes.size() + " showtimes");
		};
	}
	 */
}