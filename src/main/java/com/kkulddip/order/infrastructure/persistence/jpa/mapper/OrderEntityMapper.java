package com.kkulddip.order.infrastructure.persistence.jpa.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.DiscountInfo;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.DiscountInfoEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderItemEntity;

@RequiredArgsConstructor
@Component
public class OrderEntityMapper {
    
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
            .build();
        
        return orderEntity;
    }
    
    /**
     * OrderEntity를 도메인 Order로 변환
     */
    public Order toDomain(OrderEntity orderEntity) {
        return Order.restore(
            OrderId.of(orderEntity.getOrderId()),
            CustomerId.of(orderEntity.getCustomerId()),
            StoreId.of(orderEntity.getStoreId()),
            new java.util.ArrayList<>(), // OrderItem들은 필요시 별도 로딩
            com.kkulddip.order.domain.model.vo.Money.of(orderEntity.getOriginalPrice()),
            com.kkulddip.order.domain.model.vo.Money.of(orderEntity.getFinalPrice()),
            orderEntity.getOrderStatus(),
            orderEntity.getOrderDate()
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