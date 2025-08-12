package com.kkulddip.analytics.controller;

import com.kkulddip.analytics.dto.response.AnalyticsResponseDto;
import com.kkulddip.analytics.service.AnalyticsService;
import com.kkulddip.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/sales-analytics")
    public ApiResponse<AnalyticsResponseDto> getSalesAnalytics(
        @RequestParam Long storeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AnalyticsResponseDto response = analyticsService.generateSalesAnalytics(storeId, startDate, endDate);
        return ApiResponse.of(response);
    }
}

