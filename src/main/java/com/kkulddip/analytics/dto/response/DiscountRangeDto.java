package com.kkulddip.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "할인 범위별 통계 데이터")
public record DiscountRangeDto(
    @Schema(description = "할인 범위", example = "낮은 할인 (0-10%)")
    String discountRange,
    
    @Schema(description = "해당 범위의 상품 수", example = "25")
    Integer count,
    
    @Schema(description = "전체 대비 비율 (%)", example = "62.5")
    Double percentage
) { }