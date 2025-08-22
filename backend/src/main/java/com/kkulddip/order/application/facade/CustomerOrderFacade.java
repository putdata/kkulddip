package com.kkulddip.order.application.facade;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.kkulddip.order.application.mapper.OrderMapper;
import com.kkulddip.order.application.service.OrderService;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;

/**
 * 고객 관련 주문 기능 담당 Facade
 * - 고객 주문 내역 조회
 * - 기타 고객 전용 주문 기능
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerOrderFacade {
    
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    
    /**
     * 고객 주문 내역 조회
     * - 권한 검증: 본인의 주문만 조회 가능
     * - 고객 ID로 주문 목록 조회
     */
    public List<CustomerOrderHistoryResponse> getMyOrderHistory(Long customerId) {
        log.info("고객 주문 내역 조회 시작 - customerId: {}", customerId);
        
        try {
            CustomerId customerIdVO = CustomerId.of(customerId);
            List<Order> orders = orderService.findByCustomerId(customerIdVO);
            
            log.info("고객 주문 내역 조회 완료 - customerId: {}, count: {}", customerId, orders.size());
            
            return orderMapper.toCustomerOrderHistoryResponses(orders);
            
        } catch (Exception e) {
            log.error("고객 주문 내역 조회 중 오류 발생 - customerId: {}", customerId, e);
            throw OrderException.orderDatabaseError(e);
        }
    }
}