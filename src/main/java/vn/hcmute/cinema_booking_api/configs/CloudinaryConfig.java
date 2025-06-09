    package vn.hcmute.cinema_booking_api.configs;

    import com.cloudinary.Cloudinary;
    import com.cloudinary.utils.ObjectUtils;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class CloudinaryConfig {

        @Bean
        public Cloudinary cloudinary() {
            return new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "demec8nev",
                    "api_key", "172378761872566",
                    "api_secret","wbMR2pCVkfTXC2uMe9b1_6L7VgY"));
        }
    }
