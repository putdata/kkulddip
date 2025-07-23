package com.kkulddip.order.application.service;

import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderItem;
import com.kkulddip.order.domain.repository.OrderRepository;
import com.kkulddip.order.domain.service.OrderDomainService;
import com.kkulddip.order.presentation.rest.dto.request.OrderCreateRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.presentation.rest.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        List<OrderItem> orderItems = request.orderItems().stream()
            .map(this::convertToOrderItem)
            .toList();

        OrderAggregate savedOrder = orderRepository.save(
            OrderAggregate.builder()
                .customerName(request.customerName())
                .customerEmail(request.customerEmail())
                .orderItems(orderItems)
                .build()
        );

        return OrderResponse.from(savedOrder);
    }

    public OrderResponse getOrderById(Long orderId) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getAllOrders() {
        List<OrderAggregate> orders = orderRepository.findAll();
        return orders.stream()
            .map(OrderResponse::from)
            .toList();
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<OrderAggregate> orders = orderRepository.findAll(pageable);
        return orders.map(OrderResponse::from);
    }

    public List<OrderResponse> getOrdersByCustomerEmail(String customerEmail) {
        List<OrderAggregate> orders = orderDomainService.findOrdersByCustomerEmail(customerEmail);
        return orders.stream()
            .map(OrderResponse::from)
            .toList();
    }

    public Page<OrderResponse> getOrdersByCustomerEmail(String customerEmail, Pageable pageable) {
        Page<OrderAggregate> orders = orderRepository.findByCustomerEmail(customerEmail, pageable);
        return orders.map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeConfirmed(order);
        order.confirm();
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeCancelled(order);
        order.cancel();
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeDelivered(order);
        order.deliver();
        return OrderResponse.from(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeModified(order);
        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse addOrderItem(Long orderId, OrderItemRequest itemRequest) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeModified(order);
        
        OrderItem newOrderItem = convertToOrderItem(itemRequest);
        order.addOrderItem(newOrderItem);
        
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse removeOrderItem(Long orderId, int itemIndex) {
        OrderAggregate order = orderDomainService.findOrderById(orderId);
        orderDomainService.validateOrderCanBeModified(order);
        
        order.removeOrderItem(itemIndex);
        
        return OrderResponse.from(order);
    }

    private OrderItem convertToOrderItem(OrderItemRequest request) {
        return new OrderItem(
            request.productId(),
            request.productName(),
            request.quantity(),
            new Money(request.unitPrice())
        );
    }
} 