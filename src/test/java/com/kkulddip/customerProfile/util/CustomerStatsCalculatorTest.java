package com.kkulddip.customerProfile.util;

import com.kkulddip.customerProfile.dto.CustomerStatsDto;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderItemJpaRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * CustomerStatsCalculator 단위 테스트
 * 
 * 고객 통계 계산 로직의 정확성을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class CustomerStatsCalculatorTest {
    
    @Mock
    private OrderJpaRepository orderRepository;
    
    @Mock
    private OrderItemJpaRepository orderItemRepository;
    
    @Mock
    private DdipBoxItemRepository ddipBoxItemRepository;
    
    @InjectMocks
    private CustomerStatsCalculator statsCalculator;
    
    private Long customerId;
    private List<OrderEntity> confirmedOrders;
    
    @BeforeEach
    void setUp() {
        customerId = 1L;
    }
    
    @Test
    @DisplayName("확정된 주문이 없을 때 모든 통계가 0이어야 한다")
    void calculateStats_NoOrders_ReturnsZeroStats() {
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStats(customerId);
        
        assertThat(stats.totalOrder()).isEqualTo(0);
        assertThat(stats.totalMoneySaved()).isEqualTo(0L);
        assertThat(stats.totalCo2Saved()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("절약 금액이 정확하게 계산되어야 한다")
    void calculateStats_WithOrders_CalculatesMoneySaved() {
        OrderEntity order1 = createOrder(10000, 7000);
        OrderEntity order2 = createOrder(20000, 15000);
        
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Arrays.asList(order1, order2));
        when(orderItemRepository.findByOrderOrderId(any()))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStats(customerId);
        
        assertThat(stats.totalOrder()).isEqualTo(2);
        assertThat(stats.totalMoneySaved()).isEqualTo(8000L);
    }
    
    @Test
    @DisplayName("CO2 절약량이 정확하게 계산되어야 한다")
    void calculateStats_WithOrders_CalculatesCo2Saved() {
        OrderEntity order = createOrderWithItems();
        
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Collections.singletonList(order));
        when(orderItemRepository.findByOrderOrderId(any()))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStats(customerId);
        
        assertThat(stats.totalCo2Saved()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("빠른 통계 계산이 정확해야 한다")
    void calculateStatsFast_ReturnsCorrectStats() {
        when(orderRepository.countConfirmedOrdersByCustomerId(customerId))
            .thenReturn(5L);
        when(orderRepository.calculateTotalMoneySavedByCustomerId(customerId))
            .thenReturn(15000L);
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Collections.singletonList(createOrderWithItems()));
        when(orderItemRepository.findByOrderOrderId(any()))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStatsFast(customerId);
        
        assertThat(stats.totalOrder()).isEqualTo(5);
        assertThat(stats.totalMoneySaved()).isEqualTo(15000L);
        assertThat(stats.totalCo2Saved()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("null 값 처리가 정확해야 한다")
    void calculateStats_WithNullValues_HandlesGracefully() {
        OrderEntity orderWithNullPrices = createOrder(null, null);
        
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Collections.singletonList(orderWithNullPrices));
        when(orderItemRepository.findByOrderOrderId(any()))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStats(customerId);
        
        assertThat(stats.totalOrder()).isEqualTo(1);
        assertThat(stats.totalMoneySaved()).isEqualTo(0L);
        assertThat(stats.totalCo2Saved()).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("큰 무게 값의 CO2 계산이 정확해야 한다")
    void calculateStats_LargeWeight_CalculatesCorrectCo2() {
        OrderEntity order = createOrderWithItems();
        
        when(orderRepository.findConfirmedOrdersByCustomerId(customerId))
            .thenReturn(Collections.singletonList(order));
        when(orderItemRepository.findByOrderOrderId(any()))
            .thenReturn(Collections.emptyList());
        
        CustomerStatsDto stats = statsCalculator.calculateStats(customerId);
        
        assertThat(stats.totalCo2Saved()).isEqualTo(0.0);
    }
    
    private OrderEntity createOrder(Integer originalPrice, Integer finalPrice) {
        return OrderEntity.builder()
            .orderId(1L)
            .customerId(customerId)
            .originalPrice(originalPrice)
            .finalPrice(finalPrice)
            .orderStatus(OrderStatus.CONFIRMED)
            .orderDate(java.time.LocalDateTime.now())
            .build();
    }
    
    private OrderEntity createOrderWithItems() {
        return createOrder(10000, 7000);
    }
}