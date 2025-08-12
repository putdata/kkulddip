package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

/**
 * 띱박스 재고 수량 업데이트 요청 DTO
 */
@Builder
public record UpdateDdipBoxQuantityRequest(
    
    @Min(value = 0, message = "잔여 수량은 0개 이상이어야 합니다.")
    Long remainingQuantity,
    
    @Min(value = 1, message = "일일 수량은 1개 이상이어야 합니다.")
    Long dailyQuantity,
    
    Boolean resetRemaining
    
) {
    
    public UpdateDdipBoxQuantityRequest {
        if (resetRemaining == null) {
            resetRemaining = false;
        }
    }
    
    public boolean isDirectQuantityUpdate() {
        return remainingQuantity != null && !resetRemaining;
    }
    
    public boolean isDailyQuantityReset() {
        return dailyQuantity != null && resetRemaining;
    }
    
    public boolean hasValidOperation() {
        return isDirectQuantityUpdate() || isDailyQuantityReset();
    }
}