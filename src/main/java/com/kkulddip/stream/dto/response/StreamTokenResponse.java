package com.kkulddip.stream.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "스트림 토큰 응답")
@Builder
public record StreamTokenResponse(
        @Schema(description = "OpenVidu 토큰값", example = "tok_xyz789")
        String token,

        @Schema(description = "세션 ID", example = "ses_abcdef123456")
        String sessionId
) {
}