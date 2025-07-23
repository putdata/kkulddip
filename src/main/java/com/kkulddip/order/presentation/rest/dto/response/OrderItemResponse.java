package com.kkulddip.order.presentation.rest.dto.response;

import com.kkulddip.order.domain.model.vo.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
            .productId(orderItem.getProductId())
            .productName(orderItem.getProductName())
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice().getAmount())
            .totalPrice(orderItem.getTotalPrice().getAmount())
            .build();
    }
} 