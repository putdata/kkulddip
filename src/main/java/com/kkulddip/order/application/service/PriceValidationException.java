package com.kkulddip.order.application.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 가격 검증 실패 시 발생하는 예외
 */
public class PriceValidationException extends BusinessException {
    
    public PriceValidationException(String message) {
        super(ErrorCode.ORDER_PRICE_VALIDATION_FAILED, message);
    }
    
    /**
     * 가격 불일치 예외
     */
    public static PriceValidationException priceMismatch(Long productId, Integer expectedPrice, Integer actualPrice) {
        String message = String.format("상품 가격이 일치하지 않습니다. 상품ID: %d, 요청 가격: %d, 실제 가격: %d", 
            productId, expectedPrice, actualPrice);
        return new PriceValidationException(message);
    }
    
    /**
     * 잘못된 수량 예외
     */
    public static PriceValidationException invalidQuantity(Long productId, Integer quantity) {
        String message = String.format("잘못된 주문 수량입니다. 상품ID: %d, 수량: %d", productId, quantity);
        return new PriceValidationException(message);
    }
    
    /**
     * 잘못된 단가 예외
     */
    public static PriceValidationException invalidUnitPrice(Long productId, Integer unitPrice) {
        String message = String.format("잘못된 단가입니다. 상품ID: %d, 단가: %d", productId, unitPrice);
        return new PriceValidationException(message);
    }
}