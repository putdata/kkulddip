package com.kkulddip.customerProfile.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "위치 간 거리 응답")
public record LocationDistanceResponse(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,
    
    @Schema(description = "거리 (km)", example = "1.5")
    Double distance,
    
    @Schema(description = "예상 도착 시간 (분)", example = "5")
    Integer estimatedTimeInMinutes,
    
    @Schema(description = "근처 도착 여부", example = "false")
    Boolean isNearby,
    
    @Schema(description = "계산 시간", example = "2024-01-01T12:00:00")
    LocalDateTime calculatedAt
) {
    public static LocationDistanceResponse of(Long customerId, Double distance) {
        boolean isNearby = distance != null && distance < 0.1;
        Integer estimatedTime = distance != null ? (int) Math.ceil(distance * 3) : null;
        
        return LocationDistanceResponse.builder()
            .customerId(customerId)
            .distance(distance)
            .estimatedTimeInMinutes(estimatedTime)
            .isNearby(isNearby)
            .calculatedAt(LocalDateTime.now())
            .build();
    }
}