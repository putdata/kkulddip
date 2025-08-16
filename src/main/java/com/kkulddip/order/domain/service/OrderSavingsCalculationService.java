package com.kkulddip.order.domain.service;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.SavingsResult;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;

/**
 * 주문 절약 값 계산 도메인 서비스
 * 
 * 주문의 절약 금액과 CO2 절약량을 계산합니다.
 * - 절약 금액: originalPrice * quantity - salePrice * quantity
 * - CO2 절약량: 총무게(g) * 1.8
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderSavingsCalculationService {
    
    private final DdipBoxRepository ddipBoxRepository;
    private final DdipBoxItemRepository ddipBoxItemRepository;
    
    private static final double CO2_FACTOR = 1.8;
    
    /**
     * 주문의 절약 값을 계산합니다.
     * 
     * @param order 주문
     * @return SavingsResult 절약 금액과 CO2량
     */
    public SavingsResult calculateSavings(Order order) {
        try {
            Money totalSavedMoney = calculateTotalSavedMoney(order);
            Double totalSavedCo2 = calculateTotalSavedCo2(order);
            
            log.debug("주문 절약 값 계산 완료 - orderId: {}, savedMoney: {}, savedCo2: {}kg", 
                order.getOrderId().value(), totalSavedMoney.amount(), totalSavedCo2);
            
            return SavingsResult.of(totalSavedMoney, totalSavedCo2);
                
        } catch (Exception e) {
            log.error("주문 절약 값 계산 실패 - orderId: {}", order.getOrderId().value(), e);
            return SavingsResult.of(Money.of(0L), 0.0);
        }
    }
    
    /**
     * 총 절약 금액을 계산합니다.
     * 
     * @param order 주문
     * @return 총 절약 금액
     */
    private Money calculateTotalSavedMoney(Order order) {
        Money totalSavedMoney = Money.of(0L);
        
        for (OrderItem orderItem : order.getOrderItems()) {
            Long ddipboxId = orderItem.getProductId().value();
            Integer quantity = orderItem.getQuantity();
            
            DdipBox ddipBox = ddipBoxRepository.findById(ddipboxId)
                .orElse(null);
                
            if (ddipBox != null) {
                long originalTotal = ddipBox.getOriginalPrice() * quantity;
                long saleTotal = ddipBox.getSalePrice() * quantity;
                long savedAmount = originalTotal - saleTotal;
                
                if (savedAmount > 0) {
                    totalSavedMoney = totalSavedMoney.add(Money.of(savedAmount));
                }
            }
        }
        
        return totalSavedMoney;
    }
    
    /**
     * 총 CO2 절약량을 계산합니다.
     * 
     * @param order 주문
     * @return 총 CO2 절약량 (kg 단위)
     */
    private Double calculateTotalSavedCo2(Order order) {
        double totalCo2Saved = 0.0;
        
        for (OrderItem orderItem : order.getOrderItems()) {
            Long ddipboxId = orderItem.getProductId().value();
            Integer quantity = orderItem.getQuantity();
            
            Integer totalWeight = ddipBoxItemRepository.sumWeightByDdipboxId(ddipboxId);
            
            if (totalWeight != null && totalWeight > 0) {
                double co2ForThisItem = totalWeight * quantity * CO2_FACTOR;
                totalCo2Saved += co2ForThisItem;
            }
        }
        
        // g 단위를 kg 단위로 변환 (1kg = 1000g)
        double co2InKg = totalCo2Saved / 1000.0;
        return Math.round(co2InKg * 100.0) / 100.0;
    }
}