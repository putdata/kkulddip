package com.kkulddip.order.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkulddip.order.domain.model.enums.OrderStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;
    
    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;
    
    @Builder
    protected OrderEntity(Long orderId, Long customerId, Long storeId, Integer originalPrice, 
        Integer finalPrice, OrderStatus orderStatus, LocalDateTime orderDate, LocalDateTime pickupTime) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.pickupTime = pickupTime;
    }
    
    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }
    
    public void updatePrices(Integer originalPrice, Integer finalPrice) {
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
    }
}