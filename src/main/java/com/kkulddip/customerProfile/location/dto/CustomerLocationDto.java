package com.kkulddip.customerProfile.location.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "고객 실시간 위치 정보")
public record CustomerLocationDto(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,
    
    @Schema(description = "위도", example = "37.5665")
    Double latitude,
    
    @Schema(description = "경도", example = "126.9780")
    Double longitude,
    
    @Schema(description = "위치 정확도 (미터)", example = "10.5")
    Double accuracy,
    
    @Schema(description = "위치 업데이트 시간", example = "2024-01-01T12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    
    @Schema(description = "디바이스 정보", example = "iPhone 14 Pro")
    String deviceInfo,
    
    @Schema(description = "위치 공유 활성화 여부", example = "true")
    Boolean isLocationSharingEnabled
) {
    public static CustomerLocationDto of(Long customerId, Double latitude, Double longitude, Double accuracy) {
        return CustomerLocationDto.builder()
            .customerId(customerId)
            .latitude(latitude)
            .longitude(longitude)
            .accuracy(accuracy)
            .timestamp(LocalDateTime.now())
            .isLocationSharingEnabled(true)
            .build();
    }
}