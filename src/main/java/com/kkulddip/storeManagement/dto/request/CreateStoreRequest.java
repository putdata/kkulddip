package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 가게 생성 요청 DTO
 */
@Builder
public record CreateStoreRequest(
    
    @NotBlank(message = "가게명은 필수입니다.")
    @Size(max = 255, message = "가게명은 255자를 초과할 수 없습니다.")
    String storeName,
    
    @Size(max = 15, message = "전화번호는 15자를 초과할 수 없습니다.")
    String phone,
    
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    String description,
    
    @Size(max = 100, message = "영업시간은 100자를 초과할 수 없습니다.")
    String operatingHours,
    
    @Size(max = 100, message = "사업자번호는 100자를 초과할 수 없습니다.")
    String businessNumber,
    
    @NotBlank(message = "가게 주소는 필수입니다.")
    @Size(max = 100, message = "가게 주소는 100자를 초과할 수 없습니다.")
    String storeAddress,
    
    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
    Double latitude,
    
    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
    Double longitude
    
) {
    
    public CreateStoreRequest {
        if (storeName != null) {
            storeName = storeName.trim();
        }
        if (phone != null) {
            phone = phone.trim();
        }
        if (storeAddress != null) {
            storeAddress = storeAddress.trim();
        }
    }
}