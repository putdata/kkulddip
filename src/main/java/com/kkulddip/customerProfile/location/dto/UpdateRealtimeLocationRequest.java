package com.kkulddip.customerProfile.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
@Schema(description = "실시간 위치 업데이트 요청")
public record UpdateRealtimeLocationRequest(
    @Schema(description = "위도", example = "37.5665")
    @NotNull(message = "위도는 필수입니다")
    @Min(value = -90, message = "위도는 -90 이상이어야 합니다")
    @Max(value = 90, message = "위도는 90 이하여야 합니다")
    Double latitude,
    
    @Schema(description = "경도", example = "126.9780")
    @NotNull(message = "경도는 필수입니다")
    @Min(value = -180, message = "경도는 -180 이상이어야 합니다")
    @Max(value = 180, message = "경도는 180 이하여야 합니다")
    Double longitude,
    
    @Schema(description = "위치 정확도 (미터)", example = "10.5")
    @Positive(message = "정확도는 양수여야 합니다")
    Double accuracy,
    
    @Schema(description = "디바이스 정보", example = "iPhone 14 Pro")
    String deviceInfo
) {}