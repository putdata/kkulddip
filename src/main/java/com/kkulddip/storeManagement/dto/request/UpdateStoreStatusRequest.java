package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 가게 활성화/비활성화 상태 변경 요청 DTO
 */
@Builder
public record UpdateStoreStatusRequest(
    
    @NotNull(message = "활성화 상태는 필수입니다.")
    Boolean isActive,
    
    String reason
    
) {
    
    public UpdateStoreStatusRequest {
        if (reason != null) {
            reason = reason.trim();
        }
    }
}