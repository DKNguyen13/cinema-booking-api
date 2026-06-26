package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableTimeSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String label;
}