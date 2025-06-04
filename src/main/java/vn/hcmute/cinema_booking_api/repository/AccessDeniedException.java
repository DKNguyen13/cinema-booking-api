package vn.hcmute.cinema_booking_api.repository;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
