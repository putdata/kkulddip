package com.kkulddip.customerProfile.dto;

import lombok.Builder;

/**
 * 고객 통계 정보 DTO
 * 
 * CustomerStatsCalculator에서 계산된 통계 정보를 담는 내부 DTO입니다.
 */
@Builder
public record CustomerStatsDto(
    Integer totalOrder,
    Long totalMoneySaved,
    Double totalCo2Saved
) {}