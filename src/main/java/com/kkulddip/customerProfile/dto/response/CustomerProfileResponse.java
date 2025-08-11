package com.kkulddip.customerProfile.dto.response;

import com.kkulddip.domain.customer.enums.CustomerLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "고객 프로필 응답")
public record CustomerProfileResponse(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,

    @Schema(description = "이메일", example = "customer@example.com")
    String email,

    @Schema(description = "이름", example = "김철수")
    String name,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String profileImageUrl,

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    String address,

    @Schema(description = "위도", example = "37.5665")
    Double latitude,

    @Schema(description = "경도", example = "126.9780")
    Double longitude,

    @Schema(description = "고객 레벨", example = "SPROUT_BEE")
    CustomerLevel level,

    @Schema(description = "가입일시", example = "2024-01-01T00:00:00")
    LocalDateTime createdAt,

    @Schema(description = "마지막 활동 일시", example = "2024-01-01T12:00:00")
    LocalDateTime lastActiveAt
) {}