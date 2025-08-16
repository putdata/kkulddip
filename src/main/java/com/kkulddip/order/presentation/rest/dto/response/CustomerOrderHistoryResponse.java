package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CustomerOrderHistoryResponse(
    String orderId,
    Long storeId,
    String storeName,
    boolean hasReview,
    List<OrderItemResponse> orderItems,
    Long originalPrice,
    Long finalPrice,
    String orderStatus,
    LocalDateTime orderDate,
    LocalDateTime pickupTime
) {
    
    @Builder
    public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        Long unitPrice,
        Long totalPrice
    ) {}
}