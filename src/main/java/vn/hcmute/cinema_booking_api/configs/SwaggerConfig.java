package vn.hcmute.cinema_booking_api.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()//http://localhost:8082/swagger-ui/index.html
                .info(new Info()
                        .title("Cinema Booking API")
                        .description("API documentation for Cinema Booking App")
                        .version("1.0"));
    }
}
