package com.kkulddip.order.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 특정 가게의 월별 정산 데이터 조회 (CONFIRMED 상태만)
     */
    @Query("""
        SELECT 
            COALESCE(SUM(o.finalPrice), 0) as totalRevenue,
            COUNT(o) as orderCount
        FROM OrderEntity o 
        WHERE o.storeId = :storeId 
        AND o.orderStatus = 'CONFIRMED'
        AND YEAR(o.orderDate) = :year 
        AND MONTH(o.orderDate) = :month
        """)
    SettlementProjection findSettlementByStoreIdAndMonth(
        @Param("storeId") Long storeId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 특정 Owner의 모든 가게에 대한 월별 정산 데이터 조회
     */
    @Query("""
        SELECT 
            o.storeId,
            COALESCE(SUM(o.finalPrice), 0) as totalRevenue,
            COUNT(o) as orderCount
        FROM OrderEntity o 
        WHERE o.storeId IN :storeIds 
        AND o.orderStatus = 'CONFIRMED'
        AND YEAR(o.orderDate) = :year 
        AND MONTH(o.orderDate) = :month
        GROUP BY o.storeId
        """)
    List<StoreSettlementProjection> findSettlementByStoreIdsAndMonth(
        @Param("storeIds") List<Long> storeIds,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 특정 가게의 총 주문 수 조회 (CONFIRMED 상태만)
     */
    @Query("""
        SELECT COUNT(o) FROM OrderEntity o 
        WHERE o.storeId = :storeId 
        AND o.orderStatus = 'CONFIRMED'
        """)
    Long countConfirmedOrdersByStoreId(@Param("storeId") Long storeId);

    /**
     * 특정 가게의 총 매출 조회 (CONFIRMED 상태만)
     */
    @Query("""
        SELECT COALESCE(SUM(o.finalPrice), 0) FROM OrderEntity o 
        WHERE o.storeId = :storeId 
        AND o.orderStatus = 'CONFIRMED'
        """)
    Long sumFinalPriceByStoreId(@Param("storeId") Long storeId);

    /**
     * 정산 데이터 조회용 Projection 인터페이스
     */
    interface SettlementProjection {
        Long getTotalRevenue();
        Long getOrderCount();
    }

    /**
     * 가게별 정산 데이터 조회용 Projection 인터페이스
     */
    interface StoreSettlementProjection {
        Long getStoreId();
        Long getTotalRevenue();
        Long getOrderCount();
    }

}