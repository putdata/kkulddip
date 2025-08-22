package com.kkulddip.customerProfile.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "프로필 수정 응답")
public record UpdateProfileResponse(
    @Schema(description = "고객 ID", example = "1")
    Long customerId,

    @Schema(description = "수정된 이름", example = "김철수")
    String name,

    @Schema(description = "수정된 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String profileImageUrl,

    @Schema(description = "수정 일시", example = "2024-01-01T12:00:00")
    LocalDateTime updatedAt
) {}