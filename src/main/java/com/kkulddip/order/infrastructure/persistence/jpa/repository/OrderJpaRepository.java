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
     * 고객별 CONFIRMED 상태의 주문 수 조회
     */
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.customerId = :customerId AND o.orderStatus = 'CONFIRMED'")
    Long countConfirmedOrdersByCustomerId(@Param("customerId") Long customerId);

    /**
     * 고객별 CONFIRMED 상태의 주문들 조회 (단순 조회)
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId AND o.orderStatus = 'CONFIRMED'")
    List<OrderEntity> findConfirmedOrdersByCustomerId(@Param("customerId") Long customerId);

    /**
     * 고객별 CONFIRMED 상태 주문의 절약 금액 총합 계산
     */
    @Query("SELECT SUM(o.originalPrice - o.finalPrice) FROM OrderEntity o WHERE o.customerId = :customerId AND o.orderStatus = 'CONFIRMED'")
    Long calculateTotalMoneySavedByCustomerId(@Param("customerId") Long customerId);

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
     * 특정 가게의 기간별 정산 데이터 조회 (CONFIRMED 상태만)
     */
    @Query("""
        SELECT 
            YEAR(o.orderDate) as year,
            MONTH(o.orderDate) as month,
            COALESCE(SUM(o.finalPrice), 0) as totalRevenue,
            COUNT(o) as orderCount
        FROM OrderEntity o 
        WHERE o.storeId = :storeId 
        AND o.orderStatus = 'CONFIRMED'
        AND ((YEAR(o.orderDate) = :startYear AND MONTH(o.orderDate) >= :startMonth) OR YEAR(o.orderDate) > :startYear)
        AND ((YEAR(o.orderDate) = :endYear AND MONTH(o.orderDate) <= :endMonth) OR YEAR(o.orderDate) < :endYear)
        GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
        ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)
        """)
    List<MonthlySettlementProjection> findMonthlySettlementByStoreIdAndPeriod(
        @Param("storeId") Long storeId,
        @Param("startYear") Integer startYear,
        @Param("startMonth") Integer startMonth,
        @Param("endYear") Integer endYear,
        @Param("endMonth") Integer endMonth
    );

    /**
     * 가게별 정산 데이터 조회용 Projection 인터페이스
     */
    interface StoreSettlementProjection {
        Long getStoreId();
        Long getTotalRevenue();
        Long getOrderCount();
    }

    /**
     * 월별 정산 데이터 조회용 Projection 인터페이스
     */
    interface MonthlySettlementProjection {
        Integer getYear();
        Integer getMonth();
        Long getTotalRevenue();
        Long getOrderCount();
    }

}