package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 띱박스 카드뷰 응답 DTO
 * 가게의 띱박스 목록 조회시 사용
 */
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
    Boolean active,
    Boolean soldOut,
    List<DdipBoxItemDto> items
) {
    
    /**
     * 기본 필드만으로 DdipBoxCardViewDto 생성
     */
    public static DdipBoxCardViewDto of(Long ddipboxId, String ddipboxName) {
        return new DdipBoxCardViewDto(
                ddipboxId, null, ddipboxName, null, null, null, null, null, null, null, null, null, null, null
        );
    }
    
    /**
     * 전체 필드로 DdipBoxCardViewDto 생성
     */
    public static DdipBoxCardViewDto of(
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
            Boolean active,
            Boolean soldOut,
            List<DdipBoxItemDto> items
    ) {
        return new DdipBoxCardViewDto(
                ddipboxId, storeId, ddipboxName, description, category, originalPrice, salePrice,
                discountRate, dailyQuantity, remainingQuantity, maxPerCustomer, active, soldOut, items
        );
    }
    
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