package com.kkulddip.review.dto.response;

import lombok.Builder;
import java.util.List;

public record ReviewListResponseDto(
    List<ReviewWithHelpfulStatusResponseDto> reviewList,
    Long reviewCount,
    String cursor,
    boolean hasNext
) {
    @Builder
    public ReviewListResponseDto {}
}
