package com.kkulddip.order.presentation.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record OwnerOrderHistoryResponse(
    String orderId,
    Long customerId,
    String customerName,
    List<OrderItemResponse> orderItems,
    Integer originalPrice,
    String orderStatus,
    LocalDateTime orderDate,
    LocalDateTime pickupTime
) {
    
    @Builder
    public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        Integer unitPrice
    ) {}
}