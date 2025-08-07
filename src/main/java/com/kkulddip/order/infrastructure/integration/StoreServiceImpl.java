package com.kkulddip.order.infrastructure.integration;

import com.kkulddip.order.application.service.StoreService;
import com.kkulddip.order.application.exception.OrderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreServiceImpl implements StoreService {
    
    @Override
    public void requestOrderConfirmation(Long orderId, Long storeId) {
        log.info("가게에 주문 확정 요청 - orderId: {}, storeId: {}", orderId, storeId);
        
        try {
            // TODO: 실제 가게 시스템에 주문 확정 요청을 보내는 로직 구현
            // 현재는 로그만 출력하고 즉시 완료 처리
            
            log.info("가게 주문 확정 요청 완료 - orderId: {}, storeId: {}", orderId, storeId);
        } catch (Exception e) {
            log.error("Store API 호출 실패 - orderId: {}, storeId: {}, operation: requestOrderConfirmation, details: {}", 
                orderId, storeId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }
}