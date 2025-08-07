package com.kkulddip.order.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.ProductId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.domain.service.OrderItemIdGenerator;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderMapper {
    
    private final OrderItemIdGenerator orderItemIdGenerator;
    
    /**
     * CreateOrderRequest를 도메인 객체들로 변환
     */
    public CustomerId toCustomerId(Long customerId) {
        return CustomerId.of(customerId);
    }
    
    public StoreId toStoreId(Long storeId) {
        return StoreId.of(storeId);
    }
    
    public List<AddOrderItemCommand> toAddOrderItemCommands(List<OrderItemRequest> orderItemRequests) {
        return orderItemRequests.stream()
            .map(this::toAddOrderItemCommand)
            .collect(Collectors.toList());
    }
    
    private AddOrderItemCommand toAddOrderItemCommand(OrderItemRequest request) {
        log.debug("Converting OrderItemRequest - productId: {}, quantity: {}, unitPrice: {}", 
            request.productId(), request.quantity(), request.unitPrice());
        
        Money unitPrice = Money.of(request.unitPrice());
        log.debug("Created Money object - amount: {}", unitPrice.amount());
        
        AddOrderItemCommand command = new AddOrderItemCommand(
            orderItemIdGenerator.generate(),
            ProductId.of(request.productId()),
            request.quantity(),
            unitPrice,
            List.of()
        );
        
        log.debug("Created AddOrderItemCommand - quantity: {}, unitPrice: {}", 
            command.quantity(), command.unitPrice().amount());
        
        return command;
    }
    
    /**
     * Order를 CreateOrderResponse로 변환
     */
    public CreateOrderResponse toCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
            .orderId(order.getOrderId().value())
            .customerId(order.getCustomerId().value())
            .storeId(order.getStoreId().value())
            .originalPrice(order.getOriginalPrice().amount())
            .finalPrice(order.getFinalPrice().amount())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .build();
    }
}