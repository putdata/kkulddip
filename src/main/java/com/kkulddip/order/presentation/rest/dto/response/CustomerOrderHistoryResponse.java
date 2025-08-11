package com.kkulddip.order.presentation.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CustomerOrderHistoryResponse(
    String orderId,
    Long storeId,
    String storeName,
    List<OrderItemResponse> orderItems,
    Integer originalPrice,
    Integer finalPrice,
    String orderStatus,
    LocalDateTime orderDate,
    LocalDateTime pickupTime
) {
    
    @Builder
    public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Integer totalPrice
    ) {}
}