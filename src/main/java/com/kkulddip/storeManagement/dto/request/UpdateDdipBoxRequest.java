package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 띱박스 정보 수정 요청 DTO
 */
@Builder
public record UpdateDdipBoxRequest(
    
    @Size(max = 100, message = "띱박스명은 100자를 초과할 수 없습니다.")
    String ddipboxName,
    
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    String description,
    
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다.")
    String category,
    
    @Min(value = 0, message = "정가는 0원 이상이어야 합니다.")
    Long originalPrice,
    
    @Min(value = 0, message = "판매가는 0원 이상이어야 합니다.")
    Long salePrice,
    
    @Min(value = 1, message = "일일 수량은 1개 이상이어야 합니다.")
    Long dailyQuantity,
    
    @Min(value = 1, message = "고객당 최대 구매 수량은 1개 이상이어야 합니다.")
    Long maxPerCustomer
    
) {
    
    public UpdateDdipBoxRequest {
        if (ddipboxName != null) {
            ddipboxName = ddipboxName.trim();
        }
        if (description != null) {
            description = description.trim();
        }
        if (category != null) {
            category = category.trim();
        }
    }
    
    public boolean hasUpdates() {
        return ddipboxName != null || description != null || category != null || 
               originalPrice != null || salePrice != null || dailyQuantity != null || 
               maxPerCustomer != null;
    }
}