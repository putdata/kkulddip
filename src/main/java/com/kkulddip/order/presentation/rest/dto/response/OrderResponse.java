package com.kkulddip.order.presentation.rest.dto.response;

import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
    Long orderId,
    String customerName,
    String customerEmail,
    OrderStatus status,
    List<OrderItemResponse> orderItems,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime cancelledAt,
    LocalDateTime deliveredAt
) {
    public static OrderResponse from(OrderAggregate orderAggregate) {
        List<OrderItemResponse> orderItemResponses = orderAggregate.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .toList();

        return OrderResponse.builder()
            .orderId(orderAggregate.getOrderId())
            .customerName(orderAggregate.getCustomerName())
            .customerEmail(orderAggregate.getCustomerEmail())
            .status(orderAggregate.getStatus())
            .orderItems(orderItemResponses)
            .totalAmount(orderAggregate.getTotalAmount().getAmount())
            .createdAt(orderAggregate.getCreatedAt())
            .updatedAt(orderAggregate.getUpdatedAt())
            .cancelledAt(orderAggregate.getCancelledAt())
            .deliveredAt(orderAggregate.getDeliveredAt())
            .build();
    }
} 