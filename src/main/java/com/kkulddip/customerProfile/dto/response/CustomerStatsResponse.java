package com.kkulddip.customerProfile.dto.response;

import com.kkulddip.domain.customer.enums.CustomerLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "고객 통계 응답")
public record CustomerStatsResponse(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,

    @Schema(description = "고객 레벨", example = "SPROUT_BEE")
    CustomerLevel level,

    @Schema(description = "총 주문 수", example = "42")
    Integer totalOrder,

    @Schema(description = "총 절약 금액 (원)", example = "150000")
    Long totalMoneySaved,

    @Schema(description = "총 CO2 절약량 (kg)", example = "25.5")
    Double totalCo2Saved,

    @Schema(description = "다음 레벨까지 남은 주문 수", example = "8")
    Integer ordersUntilNextLevel,

    @Schema(description = "다음 레벨", example = "WORKER_BEE")
    CustomerLevel nextLevel
) {}