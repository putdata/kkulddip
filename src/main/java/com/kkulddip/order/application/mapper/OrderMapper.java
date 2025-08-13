package com.kkulddip.order.application.mapper;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderPickupResponse;
import com.kkulddip.order.presentation.rest.dto.response.OwnerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;

/**
 * Order 관련 매핑을 담당하는 통합 매퍼
 * 실제 변환 작업은 역할별 매퍼들에게 위임
 */
@RequiredArgsConstructor
@Component
public class OrderMapper {
    
    private final OrderRequestMapper orderRequestMapper;
    private final OrderResponseMapper orderResponseMapper;
    
    // Request 변환 메서드들 - OrderRequestMapper에 위임
    public CustomerId toCustomerId(Long customerId) {
        return orderRequestMapper.toCustomerId(customerId);
    }
    
    public StoreId toStoreId(Long storeId) {
        return orderRequestMapper.toStoreId(storeId);
    }
    
    public List<AddOrderItemCommand> toAddOrderItemCommands(List<OrderItemRequest> orderItemRequests) {
        return orderRequestMapper.toAddOrderItemCommands(orderItemRequests);
    }
    
    // Response 변환 메서드들 - OrderResponseMapper에 위임
    public CreateOrderResponse toCreateOrderResponse(Order order) {
        return orderResponseMapper.toCreateOrderResponse(order);
    }
    
    public PendingOrderResponse toPendingOrderResponse(Order order) {
        return orderResponseMapper.toPendingOrderResponse(order);
    }
    
    public List<PendingOrderResponse> toPendingOrderResponses(List<Order> orders) {
        return orderResponseMapper.toPendingOrderResponses(orders);
    }
    
    public OrderConfirmationResponse toOrderConfirmationResponse(Order order) {
        return orderResponseMapper.toOrderConfirmationResponse(order);
    }
    
    public CustomerOrderHistoryResponse toCustomerOrderHistoryResponse(Order order) {
        return orderResponseMapper.toCustomerOrderHistoryResponse(order);
    }
    
    public List<CustomerOrderHistoryResponse> toCustomerOrderHistoryResponses(List<Order> orders) {
        return orderResponseMapper.toCustomerOrderHistoryResponses(orders);
    }
    
    public OwnerOrderHistoryResponse toOwnerOrderHistoryResponse(Order order) {
        return orderResponseMapper.toOwnerOrderHistoryResponse(order);
    }
    
    public List<OwnerOrderHistoryResponse> toOwnerOrderHistoryResponses(List<Order> orders) {
        return orderResponseMapper.toOwnerOrderHistoryResponses(orders);
    }
    
    public OrderPickupResponse toOrderPickupResponse(Order order) {
        return orderResponseMapper.toOrderPickupResponse(order);
    }
}