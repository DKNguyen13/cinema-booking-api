package vn.hcmute.cinema_booking_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import vn.hcmute.cinema_booking_api.entity.*;
import vn.hcmute.cinema_booking_api.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@Bean
	CommandLineRunner initCategory(CategoryRepository categoryRepository) {
		return args -> {
			if(categoryRepository.count() == 0) {
				List<Category> categories = List.of(
						Category.builder().categoryName("Action").build(),
						Category.builder().categoryName("Comedy").build(),
						Category.builder().categoryName("Drama").build(),
						Category.builder().categoryName("Horror").build(),
						Category.builder().categoryName("Romance").build(),
						Category.builder().categoryName("Science Fiction").build(),
						Category.builder().categoryName("Documentary").build(),
						Category.builder().categoryName("Animation").build()
				);

				categoryRepository.saveAll(categories);
			}
			else
				System.out.println("Category already exists");
		};
	}

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
								.build()
				));
			} else {
				System.out.println("Movie already exists");
			}
		};
	}

	@Bean
	CommandLineRunner initRoomAndShowTime(RoomRepository roomRepository, MovieRepository movieRepository, ShowTimeRepository showTimeRepository) {
		return args -> {

			if (roomRepository.count() == 0) {
				roomRepository.saveAll(List.of(
						Room.builder().roomName("Room 1").totalSeats(40).build(),
						Room.builder().roomName("Room 2").totalSeats(40).build(),
						Room.builder().roomName("Room 3").totalSeats(40).build(),
						Room.builder().roomName("Room 4").totalSeats(40).build(),
						Room.builder().roomName("Room 5").totalSeats(40).build()
				));
			}

			if (showTimeRepository.count() == 0 && movieRepository.count() > 0) {
				List<Movie> movies = movieRepository.findAll();
				List<Room> rooms = roomRepository.findAll();

				if (movies.size() < 3 || rooms.size() < 5) return;

				LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

				showTimeRepository.saveAll(List.of(
						ShowTime.builder()
								.showTime(tomorrow.withHour(9).withMinute(0).withSecond(0).withNano(0))
								.movie(movies.get(0))
								.room(rooms.get(0))
								.build(),

						ShowTime.builder()
								.showTime(tomorrow.withHour(14).withMinute(0).withSecond(0).withNano(0))
								.movie(movies.get(0))
								.room(rooms.get(1))
								.build(),

						ShowTime.builder()
								.showTime(tomorrow.withHour(19).withMinute(30).withSecond(0).withNano(0))
								.movie(movies.get(0))
								.room(rooms.get(2))
								.build(),

						ShowTime.builder()
								.showTime(tomorrow.withHour(10).withMinute(0).withSecond(0).withNano(0))
								.movie(movies.get(1))
								.room(rooms.get(3))
								.build(),

						ShowTime.builder()
								.showTime(tomorrow.withHour(15).withMinute(30).withSecond(0).withNano(0))
								.movie(movies.get(1))
								.room(rooms.get(4))
								.build(),

						ShowTime.builder()
								.showTime(tomorrow.withHour(20).withMinute(0).withSecond(0).withNano(0))
								.movie(movies.get(2))
								.room(rooms.get(0))
								.build()
				));
			}
		};
	}
}