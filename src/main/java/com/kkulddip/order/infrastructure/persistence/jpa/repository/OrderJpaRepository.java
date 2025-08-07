package com.kkulddip.order.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    
    /**
     * 주문 ID로 주문 조회
     */
    Optional<OrderEntity> findByOrderId(Long orderId);
    
    /**
     * 고객 ID로 주문 목록 조회
     */
    List<OrderEntity> findByCustomerId(Long customerId);
    
    /**
     * 가게 ID로 주문 목록 조회
     */
    List<OrderEntity> findByStoreId(Long storeId);
    
    /**
     * 가게 ID와 주문 상태로 주문 목록 조회
     */
    List<OrderEntity> findByStoreIdAndOrderStatus(Long storeId, OrderStatus orderStatus);
    
    /**
     * 고객 ID와 주문 상태로 주문 목록 조회
     */
    List<OrderEntity> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus orderStatus);
    
    /**
     * 주문 상태로 주문 목록 조회
     */
    List<OrderEntity> findByOrderStatus(OrderStatus orderStatus);
    
    /**
     * 주문 ID로 주문 삭제
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * 주문 ID 존재 여부 확인
     */
    boolean existsByOrderId(Long orderId);
    

}