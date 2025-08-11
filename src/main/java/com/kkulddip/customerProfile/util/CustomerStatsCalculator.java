package com.kkulddip.customerProfile.util;

import com.kkulddip.customerProfile.dto.CustomerStatsDto;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderItemEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderItemJpaRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 고객 통계 계산 유틸리티 클래스
 * 
 * 고객의 주문 데이터를 기반으로 각종 통계를 계산합니다.
 * - 총 주문 수
 * - 절약 금액
 * - CO2 절약량
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerStatsCalculator {
    
    private final OrderJpaRepository orderRepository;
    private final OrderItemJpaRepository orderItemRepository;
    private final DdipBoxItemRepository ddipBoxItemRepository;
    
    private static final double CO2_FACTOR = 2.1;
    private static final int GRAM_TO_KG = 1000;
    
    /**
     * 고객의 통계 정보를 계산합니다.
     * 
     * @param customerId 고객 ID
     * @return 계산된 통계 정보
     */
    public CustomerStatsDto calculateStats(Long customerId) {
        List<OrderEntity> confirmedOrders = orderRepository
            .findConfirmedOrdersByCustomerId(customerId);
        
        int totalOrders = confirmedOrders.size();
        
        long totalMoneySaved = calculateTotalMoneySaved(confirmedOrders);
        
        double totalCo2Saved = calculateCo2Saved(confirmedOrders);
        
        log.debug("고객 통계 계산 완료 - customerId: {}, orders: {}, saved: {}, co2: {}kg", 
            customerId, totalOrders, totalMoneySaved, totalCo2Saved);
        
        return CustomerStatsDto.builder()
            .totalOrder(totalOrders)
            .totalMoneySaved(totalMoneySaved)
            .totalCo2Saved(totalCo2Saved)
            .build();
    }
    
    /**
     * 절약 금액 총합을 계산합니다.
     * 
     * @param orders CONFIRMED 상태의 주문 목록
     * @return 절약 금액 총합
     */
    private long calculateTotalMoneySaved(List<OrderEntity> orders) {
        return orders.stream()
            .mapToLong(order -> {
                Integer originalPrice = order.getOriginalPrice();
                Integer finalPrice = order.getFinalPrice();
                
                if (originalPrice == null || finalPrice == null) {
                    return 0L;
                }
                
                long saved = originalPrice.longValue() - finalPrice.longValue();
                return saved > 0 ? saved : 0L;
            })
            .sum();
    }
    
    /**
     * CO2 절약량을 계산합니다.
     * 
     * 계산식: (총 무게(g) * 2.1) / 1000
     * 
     * @param orders CONFIRMED 상태의 주문 목록
     * @return CO2 절약량 (kg)
     */
    private double calculateCo2Saved(List<OrderEntity> orders) {
        List<Long> orderIds = orders.stream()
            .map(OrderEntity::getOrderId)
            .collect(Collectors.toList());
        
        if (orderIds.isEmpty()) {
            return 0.0;
        }
        
        List<Long> ddipboxIds = orderIds.stream()
            .flatMap(orderId -> orderItemRepository.findByOrderOrderId(orderId).stream())
            .map(OrderItemEntity::getProductId)
            .distinct()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        if (ddipboxIds.isEmpty()) {
            return 0.0;
        }
        
        Integer totalWeightGrams = ddipBoxItemRepository.sumWeightByDdipboxIds(ddipboxIds);
        
        if (totalWeightGrams == null || totalWeightGrams == 0) {
            log.debug("띱박스 아이템 무게 정보 없음 - ddipboxIds: {}", ddipboxIds);
            return 0.0;
        }
        
        double co2Saved = (totalWeightGrams * CO2_FACTOR) / GRAM_TO_KG;
        
        log.debug("CO2 계산 완료 - totalWeight: {}g, co2Saved: {}kg", totalWeightGrams, co2Saved);
        
        return Math.round(co2Saved * 100.0) / 100.0;
    }
    
    /**
     * 빠른 통계 조회 (DB 집계 함수 사용)
     * 
     * @param customerId 고객 ID
     * @return 계산된 통계 정보
     */
    public CustomerStatsDto calculateStatsFast(Long customerId) {
        Long orderCount = orderRepository.countConfirmedOrdersByCustomerId(customerId);
        Long moneySaved = orderRepository.calculateTotalMoneySavedByCustomerId(customerId);
        
        List<OrderEntity> confirmedOrders = orderRepository
            .findConfirmedOrdersByCustomerId(customerId);
        double co2Saved = calculateCo2Saved(confirmedOrders);
        
        return CustomerStatsDto.builder()
            .totalOrder(orderCount != null ? orderCount.intValue() : 0)
            .totalMoneySaved(moneySaved != null ? moneySaved : 0L)
            .totalCo2Saved(co2Saved)
            .build();
    }
}