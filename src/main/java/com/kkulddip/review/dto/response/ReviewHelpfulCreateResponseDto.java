package com.kkulddip.review.dto.response;

import lombok.Builder;

public record ReviewHelpfulCreateResponseDto(
    Long reviewId,
    Long customerId,
    boolean isHelpful
) {
    @Builder
    public ReviewHelpfulCreateResponseDto {}
}