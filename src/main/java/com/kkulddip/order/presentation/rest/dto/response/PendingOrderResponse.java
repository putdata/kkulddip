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
    Integer originalPrice,
    Integer finalPrice,
    String orderStatus,
    LocalDateTime orderDate
) {
    
    @Builder
    public record OrderItemResponse(
        String menuName,
        Integer quantity,
        Integer basePrice,
        Integer discountPrice,
        List<String> options
    ) {}
}