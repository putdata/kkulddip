package com.kkulddip.review.dto.response;

import lombok.Builder;

public record ReviewOneResponseDto(
    ReviewWithHelpfulStatusResponseDto review,
    String cursor,
    boolean hasNext
) {
    @Builder
    public ReviewOneResponseDto{}
}
