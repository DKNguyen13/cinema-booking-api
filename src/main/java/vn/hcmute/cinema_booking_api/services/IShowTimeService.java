package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.showtime.ShowTimeResponse;
import java.time.LocalDate;
import java.util.List;

public interface IShowTimeService {
    List<ShowTimeResponse> getShowTimesByMovie(Long movieId);
    List<ShowTimeResponse> getShowTimesByDate(LocalDate date);
    ShowTimeResponse getShowTimeDetail(Long showtimeId);
}