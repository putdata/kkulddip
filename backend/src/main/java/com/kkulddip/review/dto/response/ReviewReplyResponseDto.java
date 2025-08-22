package com.kkulddip.review.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

public record ReviewReplyResponseDto(
    Long replyId,
    Long ownerId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    @Builder
    public ReviewReplyResponseDto {}
}