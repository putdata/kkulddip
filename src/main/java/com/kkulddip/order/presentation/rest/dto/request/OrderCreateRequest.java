package com.kkulddip.order.presentation.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(
    @NotBlank(message = "고객명은 필수입니다")
    @Size(max = 50, message = "고객명은 50자 이하여야 합니다")
    String customerName,
    
    @NotBlank(message = "고객 이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    String customerEmail,
    
    @Valid
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    List<OrderItemRequest> orderItems
) {} 