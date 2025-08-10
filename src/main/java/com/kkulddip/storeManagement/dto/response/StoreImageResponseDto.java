package com.kkulddip.storeManagement.dto.response;

import com.kkulddip.storeManagement.entity.StoreImage;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 가게 이미지 응답 DTO (Review 스타일)
 */
@Builder
public record StoreImageResponseDto(
    Long storeImgId,
    String imageUrl,
    String originalName,
    Long fileSize,
    Integer uploadOrder,
    LocalDateTime createdAt
) {
    public static StoreImageResponseDto from(StoreImage storeImage) {
        return StoreImageResponseDto.builder()
            .storeImgId(storeImage.getStoreImgId())
            .imageUrl(storeImage.getImageUrl())
            .originalName(storeImage.getOriginalName())
            .fileSize(storeImage.getFileSize())
            .uploadOrder(storeImage.getUploadOrder())
            .createdAt(storeImage.getCreatedAt())
            .build();
    }
}