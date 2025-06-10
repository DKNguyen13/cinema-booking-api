package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.ShowTimeDTO;
import vn.hcmute.cinema_booking_api.entity.Movie;
import vn.hcmute.cinema_booking_api.entity.ShowTime;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.exception.ResourceNotFoundException;
import vn.hcmute.cinema_booking_api.repository.MovieRepository;
import vn.hcmute.cinema_booking_api.repository.ShowTimeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowTimeService {
    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    public boolean checkExistShowTimeByShowTimeId(Long showTimeId){
        return showTimeRepository.existsById(showTimeId);
    }

    public ShowTime createShowTime(ShowTimeDTO request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie id " + request.getMovieId() + " not found"));
        if(!movie.getIsActive())
            throw new BadRequestException("Movie is not active, cannot create showtime");
        ShowTime showTime = new ShowTime();
        showTime.setShowTime(request.getShowTime());
        showTime.setMovie(movie);

        return showTimeRepository.save(showTime);
    }

    public List<ShowTime> findAllShowTimesByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie id " + movieId + " not found"));
        if(!movie.getIsActive())
            throw new BadRequestException("Movie is not active, cannot create showtime");
        return showTimeRepository.getShowTimesByMovie_MovieId(movieId).stream()
                .filter(showTime -> showTime.getShowTime().isAfter(LocalDateTime.now()))
                .toList();
    }
}
