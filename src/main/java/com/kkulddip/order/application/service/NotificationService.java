package com.kkulddip.order.application.service;

public interface NotificationService {
    
    /**
     * 고객에게 푸시 알림 전송
     * 
     * @param customerId 고객 ID
     * @param message 알림 메시지
     */
    void sendNotificationToCustomer(Long customerId, String message);
    
    /**
     * 가게 사장에게 푸시 알림 전송
     * 
     * @param storeId 가게 ID
     * @param message 알림 메시지
     */
    void sendNotificationToOwner(Long storeId, String message);
}