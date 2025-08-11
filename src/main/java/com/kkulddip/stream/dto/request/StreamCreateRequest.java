package com.kkulddip.stream.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "스트림 생성 요청")
public record StreamCreateRequest(
        @Schema(description = "스토어 ID", example = "1")
        @NotNull(message = "스토어 ID는 필수입니다.")
        @Positive(message = "스토어 ID는 양수여야 합니다.")
        Long storeId,

        @Schema(description = "스트림 제목", example = "라이브 스트리밍 제목")
        @NotBlank(message = "스트림 제목은 필수입니다.")
        @Size(max = 200, message = "스트림 제목은 200자를 초과할 수 없습니다.")
        String title,

        @Schema(description = "스트림 설명", example = "스트림에 대한 설명입니다.")
        @Size(max = 1000, message = "스트림 설명은 1000자를 초과할 수 없습니다.")
        String description
) {
}