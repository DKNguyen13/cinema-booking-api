package vn.hcmute.cinema_booking_api.utils;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private boolean success;
    private String message;
    private T data;

    //Success response with data
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().code(200).success(true).message(message).data(data).build();
    }

    //Success response without data
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder().code(200).success(true).message(message).build();
    }

    //Error without data
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder().code(code).success(false).message(message).build();
    }
}
