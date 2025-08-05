package com.kkulddip.store.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 띱박스 카드뷰 응답 DTO
 * 가게의 띱박스 목록 조회시 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DdipBoxCardViewDto {

    /**
     * 띱박스 ID
     */
    private Long ddipboxId;

    /**
     * 가게 ID
     */
    private Long storeId;

    /**
     * 띱박스명
     */
    private String ddipboxName;

    /**
     * 설명
     */
    private String description;

    /**
     * 카테고리
     */
    private String category;

    /**
     * 원가
     */
    private Long originalPrice;

    /**
     * 할인가
     */
    private Long salePrice;

    /**
     * 할인율 (계산된 값)
     */
    private Integer discountRate;

    /**
     * 일일 수량
     */
    private Long dailyQuantity;

    /**
     * 남은 수량
     */
    private Long remainingQuantity;

    /**
     * 고객당 최대 구매 수량
     */
    private Long maxPerCustomer;

    /**
     * 활성화 상태
     */
    private Boolean isActive;

    /**
     * 품절 여부 (계산된 값)
     */
    private Boolean isSoldOut;

    /**
     * 구성 상품 목록
     */
    private List<DdipBoxItemDto> items;

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

    /**
     * Builder 후처리 메서드
     */
    public static class DdipBoxCardViewDtoBuilder {
        public DdipBoxCardViewDto build() {
            DdipBoxCardViewDto dto = new DdipBoxCardViewDto(
                    ddipboxId, storeId, ddipboxName, description, category,
                    originalPrice, salePrice, discountRate, dailyQuantity,
                    remainingQuantity, maxPerCustomer, isActive, isSoldOut, items
            );
            
            // 계산된 값들 설정
            if (dto.discountRate == null) {
                dto.discountRate = dto.calculateDiscountRate();
            }
            if (dto.isSoldOut == null) {
                dto.isSoldOut = dto.calculateIsSoldOut();
            }
            
            return dto;
        }
    }
}