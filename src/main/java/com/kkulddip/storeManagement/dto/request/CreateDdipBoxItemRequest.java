package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 띱박스 아이템 생성 요청 DTO
 */
@Builder
public record CreateDdipBoxItemRequest(
    
    @NotBlank(message = "띱박스 아이템명은 필수입니다.")
    @Size(max = 255, message = "띱박스 아이템명은 255자를 초과할 수 없습니다.")
    String ddipboxItemName,
    
    @NotNull(message = "개별 정가는 필수입니다.")
    @Min(value = 0, message = "개별 정가는 0원 이상이어야 합니다.")
    Integer originalPrice,
    
    @NotNull(message = "아이템 수량은 필수입니다.")
    @Min(value = 1, message = "아이템 수량은 1개 이상이어야 합니다.")
    Integer itemQuantity
    
) {
    
    public CreateDdipBoxItemRequest {
        if (ddipboxItemName != null) {
            ddipboxItemName = ddipboxItemName.trim();
        }
    }
}