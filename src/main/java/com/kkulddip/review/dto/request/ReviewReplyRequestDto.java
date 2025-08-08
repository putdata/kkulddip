package com.kkulddip.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewReplyRequestDto(

    @NotNull(message = "가게 ID는 필수입니다.")
    Long storeId,
    
    @NotBlank(message = "답글 내용은 필수입니다.")
    @Size(min = 1, max = 300, message = "1자 이상 300자 이하로 작성해주세요.")
    String content

) {}