package com.kkulddip.customerProfile.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "위치 정보 수정 응답")
public record UpdateLocationResponse(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,

    @Schema(description = "수정된 주소", example = "서울특별시 강남구 테헤란로 123")
    String address,

    @Schema(description = "수정된 위도", example = "37.5665")
    Double latitude,

    @Schema(description = "수정된 경도", example = "126.9780")
    Double longitude,

    @Schema(description = "수정 일시", example = "2024-01-01T12:00:00")
    LocalDateTime updatedAt
) {}