package com.kkulddip.notification.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {

    private long totalUsers;
    private long totalCustomers;
    private long totalOwners;
    private long todaySuccess;
    private long todayFailed;
    private long todayTotal;
    private double successRate;
    @Builder.Default
    private Map<String, Long> deviceTypeStats = new HashMap<>();
    @Builder.Default
    private Map<String, Long> notificationTypeStats = new HashMap<>();;

    public static StatisticsDto of(long totalCustomers, long totalOwners,
                                   long todaySuccess, long todayFailed) {
        long totalUsers = totalCustomers + totalOwners;
        long todayTotal = todaySuccess + todayFailed;
        double successRate = todayTotal > 0 ? (double) todaySuccess / todayTotal * 100 : 0.0;

        return StatisticsDto.builder()
            .totalUsers(totalUsers)
            .totalCustomers(totalCustomers)
            .totalOwners(totalOwners)
            .todaySuccess(todaySuccess)
            .todayFailed(todayFailed)
            .todayTotal(todayTotal)
            .successRate(successRate)
            .build();
    }
}