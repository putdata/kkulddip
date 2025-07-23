package com.kkulddip.order.presentation.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderItemRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    @Min(value = 1, message = "상품 ID는 1 이상이어야 합니다")
    Long productId,
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다")
    String productName,
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    Integer quantity,
    
    @NotNull(message = "단가는 필수입니다")
    @Min(value = 0, message = "단가는 0 이상이어야 합니다")
    BigDecimal unitPrice
) {} 