package com.kkulddip.domain.stream.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스트림 토큰 응답")
public record StreamTokenResponse(
        @Schema(description = "OpenVidu 세션 토큰", example = "wss://stream.kkulddip.store?sessionId=ses_abcdef123456&token=tok_xyz789")
        String token,

        @Schema(description = "세션 ID", example = "ses_abcdef123456")
        String sessionId
) {
}