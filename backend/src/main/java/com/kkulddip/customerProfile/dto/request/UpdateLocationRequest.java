package com.kkulddip.customerProfile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "고객 위치 정보 수정 요청")
public record UpdateLocationRequest(
    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    @NotBlank(message = "주소는 필수입니다")
    String address,

    @Schema(description = "위도", example = "37.5665")
    @NotNull(message = "위도는 필수입니다")
    @Min(value = -90, message = "위도는 -90 이상이어야 합니다")
    @Max(value = 90, message = "위도는 90 이하여야 합니다")
    Double latitude,

    @Schema(description = "경도", example = "126.9780")
    @NotNull(message = "경도는 필수입니다")
    @Min(value = -180, message = "경도는 -180 이상이어야 합니다")
    @Max(value = 180, message = "경도는 180 이하여야 합니다")
    Double longitude
) {}