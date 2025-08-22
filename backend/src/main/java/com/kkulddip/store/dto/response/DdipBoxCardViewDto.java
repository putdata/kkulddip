package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * 띱박스 카드뷰 응답 DTO
 * 가게의 띱박스 목록 조회시 사용
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DdipBoxCardViewDto(
    Long ddipboxId,
    Long storeId,
    String ddipboxName,
    String description,
    String category,
    Long originalPrice,
    Long salePrice,
    Integer discountRate,
    Long dailyQuantity,
    Long remainingQuantity,
    Long maxPerCustomer,
    Boolean isActive,
    Boolean soldOut,
    List<DdipBoxItemDto> items
) {
    /**
     * 할인율 계산 메서드
     */
    public Integer calculateDiscountRate() {
        if (originalPrice == null || salePrice == null || originalPrice <= 0) {
            return 0;
        }
        return (int) Math.round(((double) (originalPrice - salePrice) / originalPrice) * 100);
    }

    /**
     * 품절 여부 확인 메서드
     */
    public Boolean calculateIsSoldOut() {
        return remainingQuantity != null && remainingQuantity <= 0;
    }
}