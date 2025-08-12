package com.kkulddip.order.infrastructure.persistence.jpa.mapper;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.DiscountInfo;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.OrderItemId;
import com.kkulddip.order.domain.model.vo.ProductId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.DiscountInfoEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderItemEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderItemJpaRepository;

@RequiredArgsConstructor
@Component
public class OrderEntityMapper {
    
    private final OrderItemJpaRepository orderItemJpaRepository;
    
    /**
     * 도메인 Order를 OrderEntity로 변환
     */
    public OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
            .orderId(order.getOrderId().value())
            .customerId(order.getCustomerId().value())
            .storeId(order.getStoreId().value())
            .originalPrice(order.getOriginalPrice().amount())
            .finalPrice(order.getFinalPrice().amount())
            .orderStatus(order.getOrderStatus())
            .orderDate(order.getOrderDate())
            .pickupTime(order.getPickupTime())
            .build();
        
        return orderEntity;
    }
    
    /**
     * OrderEntity를 도메인 Order로 변환
     */
    public Order toDomain(OrderEntity orderEntity) {
        // OrderItem들 조회 및 변환
        List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findByOrderOrderId(orderEntity.getOrderId());
        List<OrderItem> orderItems = orderItemEntities.stream()
            .map(this::toOrderItemDomain)
            .collect(Collectors.toList());
            
        return Order.restore(
            OrderId.of(orderEntity.getOrderId()),
            CustomerId.of(orderEntity.getCustomerId()),
            StoreId.of(orderEntity.getStoreId()),
            orderItems,
            Money.of(orderEntity.getOriginalPrice()),
            Money.of(orderEntity.getFinalPrice()),
            orderEntity.getOrderStatus(),
            orderEntity.getOrderDate(),
            orderEntity.getPickupTime()
        );
    }
    
    /**
     * 도메인 OrderItem을 OrderItemEntity로 변환
     */
    public OrderItemEntity toOrderItemEntity(OrderItem orderItem, OrderEntity orderEntity) {
        OrderItemEntity orderItemEntity = OrderItemEntity.builder()
            .orderItemId(orderItem.getOrderItemId().value())
            .order(orderEntity)
            .productId(orderItem.getProductId().value())
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice().amount())
            .build();
        
        return orderItemEntity;
    }
    
    /**
     * OrderItemEntity를 도메인 OrderItem으로 변환
     */
    private OrderItem toOrderItemDomain(OrderItemEntity orderItemEntity) {
        OrderItem orderItem = OrderItem.builder()
            .orderItemId(OrderItemId.of(orderItemEntity.getOrderItemId()))
            .order(null) // Order 참조는 나중에 설정
            .productId(ProductId.of(orderItemEntity.getProductId()))
            .quantity(orderItemEntity.getQuantity())
            .unitPrice(Money.of(orderItemEntity.getUnitPrice()))
            .build();
            
        // 현재는 DiscountInfo를 빈 리스트로 처리 (추후 필요시 구현)
        // orderItem의 discountInfos 필드를 직접 설정할 수 없으므로 일단 그대로 둠
        
        return orderItem;
    }
    
    /**
     * 도메인 DiscountInfo를 DiscountInfoEntity로 변환
     */
    public DiscountInfoEntity toDiscountInfoEntity(DiscountInfo discountInfo, OrderItemEntity orderItemEntity) {
        DiscountInfoEntity discountInfoEntity = DiscountInfoEntity.builder()
            .discountInfoId(discountInfo.getDiscountInfoId().value())
            .discountCode(discountInfo.getDiscountCode().code().toString())
            .discountType(discountInfo.getDiscountType())
            .discountAmount(discountInfo.getDiscountAmount().amount())
            .build();
        
        discountInfoEntity.setOrderItem(orderItemEntity);
        return discountInfoEntity;
    }
}