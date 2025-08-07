package com.kkulddip.order.application.service;

import java.util.List;

import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;

/**
 * 가격 검증 서비스 인터페이스
 * 클라이언트가 전달한 상품 가격이 실제 가격과 일치하는지 검증합니다.
 */
public interface PriceValidationService {
    
    /**
     * 주문 아이템들의 가격을 검증합니다.
     * Store 도메인 서버와 통신하여 실제 상품 가격과 대조합니다.
     * 
     * @param storeId 매장 ID
     * @param orderItems 검증할 주문 아이템 목록
     * @throws PriceValidationException 가격이 일치하지 않는 경우
     */
    void validatePrices(Long storeId, List<OrderItemRequest> orderItems);
    
    /**
     * 특정 상품의 실제 가격을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param productId 상품 ID
     * @return 실제 상품 가격
     */
    Money getActualProductPrice(Long storeId, Long productId);
}