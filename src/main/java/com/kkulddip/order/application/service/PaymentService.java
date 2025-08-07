package com.kkulddip.order.application.service;

public interface PaymentService {
    
    /**
     * 결제 요청 (비동기)
     * 
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 결제 금액
     * @return 결제 URL
     */
    String requestPayment(Long orderId, Long customerId, Integer amount);
}