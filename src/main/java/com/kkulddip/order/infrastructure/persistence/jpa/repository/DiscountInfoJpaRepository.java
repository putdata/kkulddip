package com.kkulddip.order.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkulddip.order.infrastructure.persistence.jpa.entity.DiscountInfoEntity;

@Repository
public interface DiscountInfoJpaRepository extends JpaRepository<DiscountInfoEntity, Long> {
    
    /**
     * 할인 정보 ID로 할인 정보 조회
     */
    Optional<DiscountInfoEntity> findByDiscountInfoId(Long discountInfoId);
    
    /**
     * 주문 아이템 ID로 할인 정보 목록 조회
     */
    List<DiscountInfoEntity> findByOrderItemOrderItemId(Long orderItemId);
    
    /**
     * 할인 코드로 할인 정보 목록 조회
     */
    List<DiscountInfoEntity> findByDiscountCode(String discountCode);
    
    /**
     * 할인 정보 ID로 할인 정보 삭제
     */
    void deleteByDiscountInfoId(Long discountInfoId);
    
    /**
     * 주문 아이템 ID로 모든 할인 정보 삭제
     */
    void deleteByOrderItemOrderItemId(Long orderItemId);
    
    /**
     * 할인 정보 ID 존재 여부 확인
     */
    boolean existsByDiscountInfoId(Long discountInfoId);
}