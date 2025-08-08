package com.kkulddip.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewUpdateRequestDto(

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 300, message = "300자 이하로 작성해주세요.")
    String content,

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    Integer rating,

    // 삭제할 기존 이미지 ID들
    List<Long> deleteImageIds
) {}