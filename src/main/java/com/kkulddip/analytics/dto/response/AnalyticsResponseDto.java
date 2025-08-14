package com.kkulddip.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Schema(description = "매출 분석 응답 데이터")
@Builder
public record AnalyticsResponseDto(
    @Schema(description = "인기 상품 목록", example = "[{\"itemName\": \"사과\", \"totalSold\": 50}]")
    List<TopSellingItemDto> topSellingItems,
    
    @Schema(description = "인기 띱박스 목록", example = "[{\"productName\": \"과일 띱박스\", \"totalSales\": 150000}]")
    List<TopSellingProductDto> topSellingProducts,
    
    @Schema(description = "할인 범위별 통계", example = "[{\"discountRange\": \"낮은 할인 (0-10%)\", \"count\": 25, \"percentage\": 62.5}]")
    List<DiscountRangeDto> topDiscountRanges,
    
    @Schema(description = "매출 예측 데이터", example = "[{\"date\": \"2024-02-01\", \"predictedSales\": 120000}]")
    List<SalesPredictionDto> salesPrediction,
    
    @Schema(description = "재고 예측 데이터", example = "[{\"date\": \"2024-02-01\", \"predictedInventory\": 80}]")
    List<InventoryPredictionDto> inventoryPrediction,
    
    @Schema(description = "총 매출액", example = "1500000.0")
    Double totalRevenue,
    
    @Schema(description = "총 주문 수", example = "45")
    Integer totalOrders,
    
    @Schema(description = "총 무게 (현재 사용 안함)", example = "null")
    Double totalWeight
) { }
