package vn.hcmute.cinema_booking_api.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ICloudinaryService {
    Map<String, Object> uploadFile(MultipartFile file);
    void deleteFile(String publicId);
}
