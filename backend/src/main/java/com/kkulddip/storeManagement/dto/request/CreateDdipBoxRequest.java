package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 띱박스 생성 요청 DTO
 */
@Builder
public record CreateDdipBoxRequest(
    
    @NotBlank(message = "띱박스명은 필수입니다.")
    @Size(max = 100, message = "띱박스명은 100자를 초과할 수 없습니다.")
    String ddipboxName,
    
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    String description,
    
    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다.")
    String category,
    
    @NotNull(message = "정가는 필수입니다.")
    @Min(value = 0, message = "정가는 0원 이상이어야 합니다.")
    Long originalPrice,
    
    @NotNull(message = "판매가는 필수입니다.")
    @Min(value = 0, message = "판매가는 0원 이상이어야 합니다.")
    Long salePrice,
    
    @NotNull(message = "일일 수량은 필수입니다.")
    @Min(value = 1, message = "일일 수량은 1개 이상이어야 합니다.")
    Long dailyQuantity,
    
    @NotNull(message = "고객당 최대 구매 수량은 필수입니다.")
    @Min(value = 1, message = "고객당 최대 구매 수량은 1개 이상이어야 합니다.")
    Long maxPerCustomer
    
) {
    
    public CreateDdipBoxRequest {
        if (ddipboxName != null) {
            ddipboxName = ddipboxName.trim();
        }
        if (description != null) {
            description = description.trim();
        }
        if (category != null) {
            category = category.trim();
        }
        
        // 판매가는 정가보다 높을 수 없음
        if (originalPrice != null && salePrice != null && salePrice > originalPrice) {
            throw new IllegalArgumentException("판매가는 정가보다 높을 수 없습니다.");
        }
        
        // 고객당 최대 구매 수량은 일일 수량보다 많을 수 없음
        if (dailyQuantity != null && maxPerCustomer != null && maxPerCustomer > dailyQuantity) {
            throw new IllegalArgumentException("고객당 최대 구매 수량은 일일 수량보다 많을 수 없습니다.");
        }
    }
}