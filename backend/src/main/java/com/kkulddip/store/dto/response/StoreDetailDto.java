package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가게 상세 조회 응답 DTO
 * 개별 가게의 상세 정보를 제공
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StoreDetailDto(
    Long storeId,
    Long ownerId,
    String storeName,
    String storeAddress,
    String description,
    String operatingHours,
    String phone,
    Double ratingAverage,
    Long reviewCount,
    String businessNumber,
    String storeProfileImage,
    Double latitude,
    Double longitude,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<DdipBoxSummaryDto> ddipBoxes
) {

    /**
     * 띱박스 요약 정보
     */
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DdipBoxSummaryDto(
        Long ddipboxId,
        String ddipboxName,
        String category,
        Long originalPrice,
        Long salePrice,
        Long remainingQuantity,
        Boolean isActive
    ) {}
}