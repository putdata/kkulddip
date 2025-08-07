package com.kkulddip.order.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.application.exception.OrderException;

/**
 * 임시 가격 검증 서비스 구현체
 * Store 도메인이 구현되기 전까지 사용하는 임시 구현체입니다.
 * 실제 가격 검증은 수행하지 않고 로그만 남깁니다.
 */
@Slf4j
@Service
public class TemporaryPriceValidationService implements PriceValidationService {
    
    @Override
    public void validatePrices(Long storeId, List<OrderItemRequest> orderItems) {
        log.warn("가격 검증 생략 - Store 도메인 미구현 (storeId: {}, itemCount: {})", 
            storeId, orderItems.size());
        
        // Store 도메인 구현 후 실제 검증 로직으로 교체 예정
        // 현재는 클라이언트가 전달한 가격을 신뢰함 (개발 환경에서만)
        
        for (OrderItemRequest item : orderItems) {
            log.debug("가격 검증 생략 - productId: {}, unitPrice: {}, quantity: {}", 
                item.productId(), item.unitPrice(), item.quantity());
        }
    }
    
    @Override
    public Money getActualProductPrice(Long storeId, Long productId) {
        log.warn("실제 상품 가격 조회 생략 - Store 도메인 미구현 (storeId: {}, productId: {})", 
            storeId, productId);
        
        // Store 도메인 구현 후 실제 가격 조회 로직으로 교체 예정
        throw OrderException.orderExternalApiError(
            new UnsupportedOperationException("Store 도메인 미구현으로 실제 가격 조회 불가"));
    }
}