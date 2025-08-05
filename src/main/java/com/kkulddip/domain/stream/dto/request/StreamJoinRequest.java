package com.kkulddip.domain.stream.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "스트림 참가 요청")
public record StreamJoinRequest(
        @Schema(description = "세션 ID", example = "ses_abcdef123456")
        @NotBlank(message = "세션 ID는 필수입니다.")
        String sessionId
) {
}