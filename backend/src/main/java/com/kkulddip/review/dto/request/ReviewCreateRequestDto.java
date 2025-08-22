package com.kkulddip.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequestDto(

    @NotNull(message = "고객 ID는 필수입니다.")
    Long customerId,

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 1, max = 300, message = "1자 이상, 300자 이하로 작성해주세요.")
    String content,

    @NotNull(message = "주문 ID는 필수입니다.")
    Long orderId,

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    Integer rating
) { }
