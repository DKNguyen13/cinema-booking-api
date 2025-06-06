package vn.hcmute.cinema_booking_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;
    private boolean success;
    private String message;
    private T data;

    //Success reponse with data
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(200, true, message, data);
    }

    //Succes without data
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<T>(200, true, message, null);
    }

    //Error without data
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<T>(code, false, message, null);
    }
}
