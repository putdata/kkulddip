package com.kkulddip.order.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.review.repository.ReviewRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderPickupResponse;
import com.kkulddip.order.presentation.rest.dto.response.OwnerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;

/**
 * Order 도메인 객체를 Response DTO로 변환하는 매퍼
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OrderResponseMapper {
    
    private final StoreRepository storeRepository;
    private final DdipBoxRepository ddipBoxRepository;
    private final CustomerRepository customerRepository;
    private final ReviewRepository reviewRepository;
    
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
            .orderItems(toPendingOrderItemResponses(order.getOrderItems()))
            .originalPrice(order.getOriginalPrice().amount())
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
     * OrderItem을 PendingOrderResponse.OrderItemResponse로 변환
     */
    private List<PendingOrderResponse.OrderItemResponse> toPendingOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
            .map(this::toPendingOrderItemResponse)
            .collect(Collectors.toList());
    }
    
    private PendingOrderResponse.OrderItemResponse toPendingOrderItemResponse(OrderItem orderItem) {
        // DdipBox 이름 조회 (비활성화된 상품도 포함)
        String productName = ddipBoxRepository.findDdipBoxNameById(orderItem.getProductId().value())
            .orElse("알 수 없는 상품");
            
        return PendingOrderResponse.OrderItemResponse.builder()
            .productId(orderItem.getProductId().value())
            .productName(productName)
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice().amount())
            .build();
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
        
        // 리뷰 작성 여부 확인
        boolean hasReview = reviewRepository.existsByCustomerIdAndOrderId(
            order.getCustomerId().value(), 
            order.getOrderId().value()
        );
            
        return CustomerOrderHistoryResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .storeId(order.getStoreId().value())
            .storeName(storeName)
            .hasReview(hasReview)
            .orderItems(toCustomerOrderItemResponses(order.getOrderItems()))
            .originalPrice(order.getOriginalPrice().amount())
            .finalPrice(order.getFinalPrice().amount())
            .orderStatus(order.getOrderStatus().name())
            .orderDate(order.getOrderDate())
            .pickupTime(order.getPickupTime())
            .build();
    }
    
    /**
     * List<Order>를 List<CustomerOrderHistoryResponse>로 변환
     * 성능 최적화를 위해 리뷰 존재 여부를 한 번에 조회
     */
    public List<CustomerOrderHistoryResponse> toCustomerOrderHistoryResponses(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        
        // 모든 주문에 대한 리뷰 존재 여부를 한 번에 조회하기 위한 Map 생성
        // 각 주문별로 개별 조회하므로 현재는 stream으로 처리
        // 추후 필요시 bulk 조회 메서드 추가 가능
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
            .unitPrice(orderItem.getUnitPrice().amount())
            .totalPrice(orderItem.calcDiscountPrice().amount())
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
            .originalPrice(order.getOriginalPrice().amount())
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
            .unitPrice(orderItem.getUnitPrice().amount())
            .build();
    }
    
    /**
     * Order를 OrderPickupResponse로 변환
     */
    public OrderPickupResponse toOrderPickupResponse(Order order) {
        return OrderPickupResponse.builder()
            .orderId(String.valueOf(order.getOrderId().value()))
            .orderStatus(order.getOrderStatus().name())
            .pickedUpAt(java.time.LocalDateTime.now())
            .build();
    }
}