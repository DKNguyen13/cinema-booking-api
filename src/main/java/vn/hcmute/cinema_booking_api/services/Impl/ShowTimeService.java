package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.cinema_booking_api.dto.showtime.ShowTimeResponse;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.MovieRepository;
import vn.hcmute.cinema_booking_api.repositories.ShowTimeRepository;
import vn.hcmute.cinema_booking_api.services.IShowTimeService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowTimeService implements IShowTimeService {
    private final ShowTimeRepository showTimeRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShowTimeResponse> getShowTimesByMovie(Long movieId) {
        movieRepository.findByMovieIdAndIsActiveTrue(movieId)
                .orElseThrow(() -> new BadRequestException("Movie not found"));

        return showTimeRepository
                .findByMovieMovieIdAndShowTimeAfterOrderByShowTimeAsc(movieId, LocalDateTime.now())
                .stream()
                .map(this::mapToShowTimeResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowTimeResponse> getShowTimesByDate(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();

        return showTimeRepository.findByDate(startOfDay, endOfDay)
                .stream()
                .map(this::mapToShowTimeResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShowTimeResponse getShowTimeDetail(Long showtimeId) {
        ShowTime showTime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new BadRequestException("Showtime not found"));
        return mapToShowTimeResponse(showTime);
    }

    private ShowTimeResponse mapToShowTimeResponse(ShowTime showTime) {
        LocalDateTime endTime = showTime.getShowTime()
                .plusMinutes(showTime.getMovie().getDuration());

        return ShowTimeResponse.builder()
                .showtimeId(showTime.getShowtimeId())
                .showTime(showTime.getShowTime())
                .endTime(endTime)
                .movieId(showTime.getMovie().getMovieId())
                .movieTitle(showTime.getMovie().getTitle())
                .roomId(showTime.getRoom().getRoomId())
                .roomName(showTime.getRoom().getRoomName())
                .build();
    }
}