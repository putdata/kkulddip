package com.kkulddip.payment.application.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 결제 도메인 전용 예외 클래스
 */
public class PaymentException extends BusinessException {
    
    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public PaymentException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public PaymentException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    // 주문 정보를 찾을 수 없을 때
    public static PaymentException orderNotFound(Long orderId) {
        return new PaymentException(ErrorCode.PAYMENT_NOT_FOUND, 
            "주문 정보를 찾을 수 없습니다. OrderId: " + orderId + 
            ". 주문이 아직 처리 중이거나 존재하지 않는 주문입니다.");
    }
    
    // 잘못된 결제 상태일 때
    public static PaymentException invalidPaymentStatus(String currentStatus) {
        return new PaymentException(ErrorCode.PAYMENT_INVALID_STATUS, 
            "READY 상태의 결제만 진행할 수 있습니다. 현재 상태: " + currentStatus);
    }
    
    // 일반적인 결제 처리 실패
    public static PaymentException processingFailed(String message) {
        return new PaymentException(ErrorCode.PAYMENT_API_ERROR, message);
    }
    
    // 결제 중복 처리 방지
    public static PaymentException alreadyProcessing(String paymentOrderId) {
        return new PaymentException(ErrorCode.PAYMENT_ALREADY_PROCESSING,
            "결제가 이미 처리 중입니다. 잠시 후 다시 시도해주세요. PaymentOrderId: " + paymentOrderId);
    }
}