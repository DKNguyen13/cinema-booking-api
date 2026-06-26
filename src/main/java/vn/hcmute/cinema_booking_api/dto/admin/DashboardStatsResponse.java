package vn.hcmute.cinema_booking_api.dto.admin;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStatsResponse {
    private long totalMovies;
    private long totalUsers;
    private long totalOrders;
    private long totalRevenue;
    private long totalShowtimes;
    private long ordersToday;
    private long revenueToday;
}