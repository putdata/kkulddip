package com.kkulddip.order.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.application.exception.OrderException;

/**
 * 고객 통계 업데이트 서비스
 * 
 * 주문 확정 시 고객의 통계 정보를 업데이트합니다.
 * - totalOrder: +1 증가
 * - totalMoneySaved: savedMoney 추가
 * - totalCo2Saved: savedCo2 추가
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerStatsUpdateService {
    
    private final CustomerRepository customerRepository;
    
    /**
     * 주문 확정 시 고객 통계를 업데이트합니다.
     * 
     * @param order 확정된 주문
     */
    @Transactional
    public void updateCustomerStatsOnOrderConfirmation(Order order) {
        try {
            Long customerId = order.getCustomerId().value();
            
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> OrderException.customerNotFound(customerId));
            
            // 고객 통계 업데이트
            customer.updateStats(
                1, // 주문 수 +1
                order.getSavedMoney().amount(), // 절약 금액 추가
                order.getSavedCo2() // CO2 절약량 추가
            );
            
            customerRepository.save(customer);
            
            log.info("고객 통계 업데이트 완료 - customerId: {}, orderId: {}, savedMoney: {}, savedCo2: {}", 
                customerId, order.getOrderId().value(), 
                order.getSavedMoney().amount(), order.getSavedCo2());
                
        } catch (Exception e) {
            log.error("고객 통계 업데이트 실패 - customerId: {}, orderId: {}", 
                order.getCustomerId().value(), order.getOrderId().value(), e);
            throw OrderException.customerStatsUpdateFailed(order.getCustomerId().value(), e);
        }
    }
}