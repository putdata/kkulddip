package com.kkulddip.order.application.facade;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.kkulddip.order.application.mapper.OrderMapper;
import com.kkulddip.order.application.service.OrderService;
import com.kkulddip.order.application.service.StoreAuthService;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.presentation.rest.dto.response.OwnerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;

/**
 * 사장님 관련 주문 조회 기능 담당 Facade
 * - 대기 중인 주문 조회
 * - 가게 주문 내역 조회
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OwnerOrderFacade {
    
    private final OrderService orderService;
    private final StoreAuthService storeAuthService;
    private final OrderMapper orderMapper;
    
    /**
     * 가게의 대기 중인 주문 목록 조회 (사장님용)
     * - 권한 검증 후 특정 가게의 AWAITING_CONFIRMATION 상태 주문들을 조회
     */
    public List<PendingOrderResponse> getPendingOrdersByStore(Long ownerId, StoreId storeId) {
        log.info("가게 대기 주문 조회 시작 - ownerId: {}, storeId: {}", ownerId, storeId.value());
        
        try {
            // 권한 검증: 사장님이 해당 가게를 소유하고 있는지 확인
            storeAuthService.validateOwnerPermission(ownerId, storeId);
            
            List<Order> pendingOrders = orderService.findPendingOrdersByStore(storeId);
            log.info("가게 대기 주문 조회 완료 - ownerId: {}, storeId: {}, count: {}", 
                ownerId, storeId.value(), pendingOrders.size());
            
            return orderMapper.toPendingOrderResponses(pendingOrders);
            
        } catch (Exception e) {
            if (e instanceof OrderException) {
                throw e;
            }
            log.error("가게 대기 주문 조회 중 오류 발생 - ownerId: {}, storeId: {}", ownerId, storeId.value(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    /**
     * 사장님용 가게 주문 내역 조회
     * - 권한 검증: 사장님이 소유한 가게의 주문만 조회 가능
     * - 가게 ID로 주문 목록 조회
     */
    public List<OwnerOrderHistoryResponse> getStoreOrderHistory(Long ownerId, StoreId storeId) {
        log.info("가게 주문 내역 조회 시작 - ownerId: {}, storeId: {}", ownerId, storeId.value());
        
        try {
            // 권한 검증: 사장님이 해당 가게를 소유하고 있는지 확인
            storeAuthService.validateOwnerPermission(ownerId, storeId);
            
            List<Order> orders = orderService.findByStoreIdAfterPaymentPending(storeId);
            
            log.info("가게 주문 내역 조회 완료 - ownerId: {}, storeId: {}, count: {}", 
                ownerId, storeId.value(), orders.size());
            
            return orderMapper.toOwnerOrderHistoryResponses(orders);
            
        } catch (Exception e) {
            if (e instanceof OrderException) {
                throw e;
            }
            log.error("가게 주문 내역 조회 중 오류 발생 - ownerId: {}, storeId: {}", ownerId, storeId.value(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
}