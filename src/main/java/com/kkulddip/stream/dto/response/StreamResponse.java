package com.kkulddip.stream.dto.response;

import com.kkulddip.stream.entity.Stream;
import com.kkulddip.stream.entity.enums.StreamStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "스트림 정보 응답")
@Builder
public record StreamResponse(
        @Schema(description = "스트림 ID", example = "1")
        Long id,

        @Schema(description = "세션 ID", example = "ses_abcdef123456")
        String sessionId,

        @Schema(description = "스트림 제목", example = "라이브 스트리밍 제목")
        String title,

        @Schema(description = "스트림 설명", example = "스트림에 대한 설명입니다.")
        String description,

        @Schema(description = "스토어 ID", example = "1")
        Long storeId,

        @Schema(description = "스토어 이름", example = "마이 스토어")
        String storeName,

        @Schema(description = "시청자 수", example = "100")
        Integer viewerCount,

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
        return StreamResponse.builder()
                .id(stream.getId())
                .sessionId(stream.getSessionId())
                .title(stream.getTitle())
                .description(stream.getDescription())
                .storeId(stream.getStoreId())
                .storeName(stream.getStore().getStoreName())
                .viewerCount(stream.getViewerCount())
                .status(stream.getStatus())
                .createdAt(stream.getCreatedAt())
                .startedAt(stream.getStartedAt())
                .endedAt(stream.getEndedAt())
                .build();
    }
}