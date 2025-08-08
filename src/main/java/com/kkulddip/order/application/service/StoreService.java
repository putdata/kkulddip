package com.kkulddip.order.application.service;

public interface StoreService {
    
    /**
     * 가게에 주문 확정 요청
     * 
     * @param orderId 주문 ID
     * @param storeId 가게 ID
     */
    void requestOrderConfirmation(Long orderId, Long storeId);
}