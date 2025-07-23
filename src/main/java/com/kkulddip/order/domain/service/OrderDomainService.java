package com.kkulddip.order.domain.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderDomainService {

    private final OrderRepository orderRepository;

    public OrderAggregate findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    public List<OrderAggregate> findOrdersByCustomerEmail(String customerEmail) {
        return orderRepository.findByCustomerEmail(customerEmail);
    }

    public void validateOrderExistence(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
    }

    public void validateOrderCanBeModified(OrderAggregate order) {
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }
    }

    public void validateOrderCanBeCancelled(OrderAggregate order) {
        if (!order.getStatus().canCancel()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }
    }

    public void validateOrderCanBeConfirmed(OrderAggregate order) {
        if (!order.getStatus().canConfirm()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CONFIRMED);
        }
    }

    public void validateOrderCanBeDelivered(OrderAggregate order) {
        if (!order.getStatus().canDeliver()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_DELIVERED);
        }
    }

    public boolean hasActiveOrders(String customerEmail) {
        List<OrderAggregate> orders = orderRepository.findByCustomerEmail(customerEmail);
        return orders.stream()
            .anyMatch(order -> order.getStatus() == OrderStatus.PENDING || 
                             order.getStatus() == OrderStatus.CONFIRMED);
    }

    public List<OrderAggregate> findRecentOrders(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
} 