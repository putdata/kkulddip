package com.kkulddip.review.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewWithHelpfulStatusResponseDto(
    Long reviewId,
    Long customerId,
    Long storeId,
    Long orderId,
    String userName,
    String profileImage,
    String content,
    Integer rating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int helpfulCount,
    List<ReviewImageResponseDto> images,
    ReviewReplyResponseDto reply,
    // 좋아요 눌렀는지
    boolean isHelpful
) {
    @Builder
    public ReviewWithHelpfulStatusResponseDto {}
}