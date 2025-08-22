package com.kkulddip.order.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderItemEntity;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    
    /**
     * 주문 아이템 ID로 주문 아이템 조회
     */
    Optional<OrderItemEntity> findByOrderItemId(Long orderItemId);
    
    /**
     * 주문 ID로 주문 아이템 목록 조회
     */
    List<OrderItemEntity> findByOrderOrderId(Long orderId);
    
    /**
     * 상품 ID로 주문 아이템 목록 조회
     */
    List<OrderItemEntity> findByProductId(Long productId);
    
    /**
     * 주문 아이템 ID로 주문 아이템 삭제
     */
    void deleteByOrderItemId(Long orderItemId);
    
    /**
     * 주문 ID로 모든 주문 아이템 삭제
     */
    void deleteByOrderOrderId(Long orderId);
    
    /**
     * 주문 아이템 ID 존재 여부 확인
     */
    boolean existsByOrderItemId(Long orderItemId);
}