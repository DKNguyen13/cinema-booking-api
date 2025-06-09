package vn.hcmute.cinema_booking_api.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    //Upload image
    public String uploadImage(MultipartFile file) {
        try {
            // Gửi dữ liệu ảnh (byte[]) lên Cloudinary, không có tùy chọn thêm
            Map<String, Object> result = cloudinary.uploader()
                    .upload(file.getBytes(),// Lấy dữ liệu ảnh duoi dạng byte[]
                            ObjectUtils.emptyMap());// Map trống, ko truyen tuy chon gì
            return (String) result.get("secure_url");
        }
        catch (Exception e) {
            throw new RuntimeException("Upload image error: " + e.getMessage());
        }
    }

    //Delete image
    public void deleteImage(String imgUrl) {
        try {
            String publicId = getPublicIdFromUrl(imgUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
        catch (Exception e) {
            throw new RuntimeException("Delete image error: " + e.getMessage());
        }
    }

    //Get publicId image on Cloudinary
    private String getPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String publicIdExtend = parts[parts.length - 1]; // lay ra file name co ca .jpg
        String publicId = publicIdExtend.split("\\.")[0]; // chi lay phan name bo phan duoi .jpg
        return publicId;
    }

}
