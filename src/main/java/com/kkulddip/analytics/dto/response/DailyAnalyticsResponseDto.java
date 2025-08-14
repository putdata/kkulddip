package com.kkulddip.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "일별 매출 분석 응답 데이터")
@Builder
public record DailyAnalyticsResponseDto(
    @Schema(description = "분석 대상 날짜", example = "2024-01-15")
    LocalDate analysisDate,
    
    @Schema(description = "매장 ID", example = "1")
    Long storeId,
    
    @Schema(description = "일일 매출 개요")
    DailySalesOverviewDto salesOverview,
    
    @Schema(description = "인기 띱박스 TOP 5")
    List<TopSellingDdipBoxDto> topSellingDdipBoxes,
    
    @Schema(description = "재고 현황")
    DailyInventoryStatusDto inventoryStatus,
    
    @Schema(description = "재고 많이 남은 띱박스 TOP 5")
    List<HighInventoryDdipBoxDto> highInventoryDdipBoxes,
    
    @Schema(description = "수익성 분석")
    ProfitMarginAnalysisDto profitMarginAnalysis
) { }