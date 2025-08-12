package com.kkulddip.order.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.ProductId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.domain.service.OrderItemIdGenerator;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.OwnerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderMapper {
    
    private final OrderItemIdGenerator orderItemIdGenerator;
    private final StoreRepository storeRepository;
    private final DdipBoxRepository ddipBoxRepository;
    private final CustomerRepository customerRepository;
    
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
        log.debug("OrderResponse 변환 - orderId: {}, customerId: {}, storeId: {}, originalPrice: {}, finalPrice: {}, orderStatus: {}, orderDate: {}",
            order.getOrderId().value(), order.getCustomerId().value(), order.getStoreId().value(),
            order.getOriginalPrice().amount(), order.getFinalPrice().amount(),
            order.getOrderStatus().name(), order.getOrderDate());
            
        return CreateOrderResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))  // Long을 String으로 변환
            .customerId(order.getCustomerId().value())
            .storeId(order.getStoreId().value())
            .originalPrice(order.getOriginalPrice().amount())
            .finalPrice(order.getFinalPrice().amount())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .build();
    }
    
    /**
     * Order를 PendingOrderResponse로 변환
     */
    public PendingOrderResponse toPendingOrderResponse(Order order) {
        return PendingOrderResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .customerId(order.getCustomerId().value())
            .storeId(order.getStoreId().value())
            .orderItems(List.of())  // TODO: OrderItem -> OrderItemResponse 변환 로직 추가
            .originalPrice(order.getOriginalPrice().amount())
            .finalPrice(order.getFinalPrice().amount())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .build();
    }
    
    /**
     * List<Order>를 List<PendingOrderResponse>로 변환
     */
    public List<PendingOrderResponse> toPendingOrderResponses(List<Order> orders) {
        return orders.stream()
            .map(this::toPendingOrderResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Order를 OrderConfirmationResponse로 변환
     */
    public OrderConfirmationResponse toOrderConfirmationResponse(Order order) {
        return OrderConfirmationResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .orderStatus(order.getOrderStatus().name())
            .pickupTime(order.getPickupTime())
            .confirmedAt(java.time.LocalDateTime.now())
            .build();
    }
    
    /**
     * Order를 CustomerOrderHistoryResponse로 변환
     */
    public CustomerOrderHistoryResponse toCustomerOrderHistoryResponse(Order order) {
        // Store 이름 조회 (비활성화된 상점도 포함)
        String storeName = storeRepository.findStoreNameByStoreId(order.getStoreId().value())
            .orElse("알 수 없는 가게");
            
        return CustomerOrderHistoryResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .storeId(order.getStoreId().value())
            .storeName(storeName)
            .orderItems(toCustomerOrderItemResponses(order.getOrderItems()))
            .originalPrice(order.getOriginalPrice().amount().intValue())
            .finalPrice(order.getFinalPrice().amount().intValue())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .pickupTime(order.getPickupTime())
            .build();
    }
    
    /**
     * List<Order>를 List<CustomerOrderHistoryResponse>로 변환
     */
    public List<CustomerOrderHistoryResponse> toCustomerOrderHistoryResponses(List<Order> orders) {
        return orders.stream()
            .map(this::toCustomerOrderHistoryResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * OrderItem을 CustomerOrderHistoryResponse.OrderItemResponse로 변환
     */
    private List<CustomerOrderHistoryResponse.OrderItemResponse> toCustomerOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
            .map(this::toCustomerOrderItemResponse)
            .collect(Collectors.toList());
    }
    
    private CustomerOrderHistoryResponse.OrderItemResponse toCustomerOrderItemResponse(OrderItem orderItem) {
        // DdipBox 이름 조회 (비활성화된 상품도 포함)
        String productName = ddipBoxRepository.findDdipBoxNameById(orderItem.getProductId().value())
            .orElse("알 수 없는 상품");
            
        return CustomerOrderHistoryResponse.OrderItemResponse.builder()
            .productId(orderItem.getProductId().value())
            .productName(productName)
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice().amount().intValue())
            .totalPrice(orderItem.calcDiscountPrice().amount().intValue())
            .build();
    }
    
    /**
     * Order를 OwnerOrderHistoryResponse로 변환
     */
    public OwnerOrderHistoryResponse toOwnerOrderHistoryResponse(Order order) {
        // Customer 이름 조회
        String customerName = customerRepository.findCustomerNameByCustomerId(order.getCustomerId().value())
            .orElse("알 수 없는 고객");
            
        return OwnerOrderHistoryResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .customerId(order.getCustomerId().value())
            .customerName(customerName)
            .orderItems(toOwnerOrderItemResponses(order.getOrderItems()))
            .originalPrice(order.getOriginalPrice().amount().intValue())
            .finalPrice(order.getFinalPrice().amount().intValue())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .pickupTime(order.getPickupTime())
            .build();
    }
    
    /**
     * List<Order>를 List<OwnerOrderHistoryResponse>로 변환
     */
    public List<OwnerOrderHistoryResponse> toOwnerOrderHistoryResponses(List<Order> orders) {
        return orders.stream()
            .map(this::toOwnerOrderHistoryResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * OrderItem을 OwnerOrderHistoryResponse.OrderItemResponse로 변환
     */
    private List<OwnerOrderHistoryResponse.OrderItemResponse> toOwnerOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
            .map(this::toOwnerOrderItemResponse)
            .collect(Collectors.toList());
    }
    
    private OwnerOrderHistoryResponse.OrderItemResponse toOwnerOrderItemResponse(OrderItem orderItem) {
        // DdipBox 이름 조회 (비활성화된 상품도 포함)
        String productName = ddipBoxRepository.findDdipBoxNameById(orderItem.getProductId().value())
            .orElse("알 수 없는 상품");
            
        return OwnerOrderHistoryResponse.OrderItemResponse.builder()
            .productId(orderItem.getProductId().value())
            .productName(productName)
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice().amount().intValue())
            .totalPrice(orderItem.calcDiscountPrice().amount().intValue())
            .build();
    }
}