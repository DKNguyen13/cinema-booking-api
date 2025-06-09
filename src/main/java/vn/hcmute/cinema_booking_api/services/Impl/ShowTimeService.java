package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.repository.ShowTimeRepository;

@Service
public class ShowTimeService {
    @Autowired
    private ShowTimeRepository showTimeRepository;

    public boolean checkExistShowTimeByShowTimeId(Long showTimeId){
        return showTimeRepository.existsById(showTimeId);
    }
}
