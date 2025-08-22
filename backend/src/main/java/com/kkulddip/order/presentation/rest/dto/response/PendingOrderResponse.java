package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record PendingOrderResponse(
    String orderId,
    Long customerId,
    Long storeId,
    List<OrderItemResponse> orderItems,
    Long originalPrice,
    String orderStatus,
    LocalDateTime orderDate
) {
    
    @Builder
    public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        Long unitPrice
    ) {}
}