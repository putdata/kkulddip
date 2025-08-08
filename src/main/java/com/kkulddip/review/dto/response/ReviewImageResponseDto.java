package com.kkulddip.review.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

public record ReviewImageResponseDto(
    Long reviewImgId,
    String imageUrl,
    String originalName,
    Long fileSize,
    Integer uploadOrder,
    LocalDateTime createdAt
) {
    @Builder
    public ReviewImageResponseDto {}
}