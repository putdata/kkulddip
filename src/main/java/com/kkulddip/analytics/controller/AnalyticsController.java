package com.kkulddip.analytics.controller;

import com.kkulddip.analytics.dto.response.AnalyticsResponseDto;
import com.kkulddip.analytics.dto.response.DailyAnalyticsResponseDto;
import com.kkulddip.analytics.service.AnalyticsService;
import com.kkulddip.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Analytics API", description = "매출 분석 및 예측 관련 API")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(
        summary = "매출 분석 데이터 조회",
        description = "지정된 기간의 매출 분석 데이터를 조회합니다. " +
                     "인기 상품, 할인 범위별 통계, 매출 예측, 재고 예측 등의 정보를 제공합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "매출 분석 데이터 조회 성공",
            content = @Content(schema = @Schema(implementation = AnalyticsResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "매장을 찾을 수 없음"
        )
    })
    @GetMapping("/sales-analytics")
    public ApiResponse<AnalyticsResponseDto> getSalesAnalytics(
        @Parameter(description = "매장 ID", required = true, example = "1")
        @RequestParam("storeId") Long storeId,
        
        @Parameter(description = "시작 날짜 (미입력시 3개월 전)", required = false, example = "2024-01-01")
        @RequestParam(value = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        
        @Parameter(description = "종료 날짜 (미입력시 오늘)", required = false, example = "2024-01-31")
        @RequestParam(value = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AnalyticsResponseDto response = analyticsService.generateSalesAnalytics(storeId, startDate, endDate);
        return ApiResponse.of(response);
    }

    @Operation(
        summary = "일별 매출 분석 데이터 조회",
        description = "특정 날짜의 상세한 매출 분석 데이터를 조회합니다. " +
                     "일일 매출 개요, 인기 띱박스 TOP 5, 재고 현황, 수익성 분석 등의 정보를 제공합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "일별 매출 분석 데이터 조회 성공",
            content = @Content(schema = @Schema(implementation = DailyAnalyticsResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "매장을 찾을 수 없음"
        )
    })
    @GetMapping("/daily-analytics")
    public ApiResponse<DailyAnalyticsResponseDto> getDailyAnalytics(
        @Parameter(description = "매장 ID", required = true, example = "1")
        @RequestParam("storeId") Long storeId,
        
        @Parameter(description = "분석 대상 날짜 (미입력시 오늘)", required = false, example = "2024-01-15")
        @RequestParam(value = "targetDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {

        DailyAnalyticsResponseDto response = analyticsService.generateDailyAnalytics(storeId, targetDate);
        return ApiResponse.of(response);
    }
}

