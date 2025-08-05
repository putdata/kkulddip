package com.kkulddip.domain.stream.dto.response;

import com.kkulddip.domain.stream.entity.Stream;
import com.kkulddip.domain.stream.entity.StreamStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "스트림 정보 응답")
public record StreamResponse(
        @Schema(description = "스트림 ID", example = "1")
        Long id,

        @Schema(description = "세션 ID", example = "ses_abcdef123456")
        String sessionId,

        @Schema(description = "스트림 제목", example = "라이브 스트리밍 제목")
        String title,

        @Schema(description = "스트림 설명", example = "스트림에 대한 설명입니다.")
        String description,

        @Schema(description = "스트리머 이름", example = "스트리머")
        String ownerName,

        @Schema(description = "스트림 상태", example = "LIVE")
        StreamStatus status,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "시작 시간")
        LocalDateTime startedAt,

        @Schema(description = "종료 시간")
        LocalDateTime endedAt
) {
    public static StreamResponse from(Stream stream) {
        return new StreamResponse(
                stream.getId(),
                stream.getSessionId(),
                stream.getTitle(),
                stream.getDescription(),
                stream.getOwner().getName(),
                stream.getStatus(),
                stream.getCreatedAt(),
                stream.getStartedAt(),
                stream.getEndedAt()
        );
    }
}