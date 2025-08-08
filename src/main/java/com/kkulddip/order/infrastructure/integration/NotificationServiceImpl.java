package com.kkulddip.order.infrastructure.integration;

import org.springframework.stereotype.Service;

import com.kkulddip.order.application.service.NotificationService;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.infrastructure.integration.dto.enums.NotificationType;
import com.kkulddip.order.infrastructure.persistence.redis.RedisNotificationPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final RedisNotificationPublisher redisNotificationPublisher;

    @Override
    public void sendNotificationToCustomer(Long customerId, String message) {
        try {
            log.info("고객에게 알림 전송 시작 - customerId: {}", customerId);
            
            redisNotificationPublisher.publishCustomerNotification(
                customerId,
                "주문 알림", // 기본 제목
                message,
                NotificationType.ORDER_AWAITING_CONFIRMATION
            );
            
            log.info("고객에게 알림 전송 완료 - customerId: {}", customerId);
        } catch (Exception e) {
            log.error("고객 알림 API 호출 실패 - customerId: {}, operation: sendCustomerNotification, details: {}", 
                customerId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    @Override
    public void sendNotificationToOwner(Long storeId, String message) {
        try {
            log.info("가게 사장에게 알림 전송 시작 - storeId: {}", storeId);
            
            redisNotificationPublisher.publishOwnerNotification(
                storeId,
                "주문 알림", // 기본 제목
                message,
                NotificationType.ORDER_AWAITING_CONFIRMATION
            );
            
            log.info("가게 사장에게 알림 전송 완료 - storeId: {}", storeId);
        } catch (Exception e) {
            log.error("사장 알림 API 호출 실패 - storeId: {}, operation: sendOwnerNotification, details: {}", 
                storeId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 특정 알림 타입으로 고객에게 알림 전송
     */
    public void sendNotificationToCustomer(Long customerId, String title, String message, NotificationType notificationType) {
        try {
            log.info("고객에게 특정 타입 알림 전송 시작 - customerId: {}, type: {}", customerId, notificationType);
            
            redisNotificationPublisher.publishCustomerNotification(
                customerId,
                title,
                message,
                notificationType
            );
            
            log.info("고객에게 특정 타입 알림 전송 완료 - customerId: {}, type: {}", customerId, notificationType);
        } catch (Exception e) {
            log.error("고객 특정 타입 알림 API 호출 실패 - customerId: {}, type: {}, operation: sendCustomerNotificationWithType, details: {}", 
                customerId, notificationType, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 특정 알림 타입으로 사장에게 알림 전송
     */
    public void sendNotificationToOwner(Long storeId, String title, String message, NotificationType notificationType) {
        try {
            log.info("가게 사장에게 특정 타입 알림 전송 시작 - storeId: {}, type: {}", storeId, notificationType);
            
            redisNotificationPublisher.publishOwnerNotification(
                storeId,
                title,
                message,
                notificationType
            );
            
            log.info("가게 사장에게 특정 타입 알림 전송 완료 - storeId: {}, type: {}", storeId, notificationType);
        } catch (Exception e) {
            log.error("사장 특정 타입 알림 API 호출 실패 - storeId: {}, type: {}, operation: sendOwnerNotificationWithType, details: {}", 
                storeId, notificationType, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 액션 URL이 포함된 고객 알림 전송
     */
    public void sendNotificationToCustomerWithAction(
            Long customerId, 
            String title, 
            String message, 
            NotificationType notificationType,
            String actionUrl
    ) {
        try {
            log.info("고객에게 액션 포함 알림 전송 시작 - customerId: {}, type: {}, actionUrl: {}", 
                customerId, notificationType, actionUrl);
            
            redisNotificationPublisher.publishCustomerNotificationWithAction(
                customerId,
                title,
                message,
                notificationType,
                actionUrl
            );
            
            log.info("고객에게 액션 포함 알림 전송 완료 - customerId: {}", customerId);
        } catch (Exception e) {
            log.error("고객 액션 포함 알림 API 호출 실패 - customerId: {}, operation: sendCustomerNotificationWithAction, details: {}", 
                customerId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 액션 URL이 포함된 사장 알림 전송
     */
    public void sendNotificationToOwnerWithAction(
            Long storeId, 
            String title, 
            String message, 
            NotificationType notificationType,
            String actionUrl
    ) {
        try {
            log.info("가게 사장에게 액션 포함 알림 전송 시작 - storeId: {}, type: {}, actionUrl: {}", 
                storeId, notificationType, actionUrl);
            
            redisNotificationPublisher.publishOwnerNotificationWithAction(
                storeId,
                title,
                message,
                notificationType,
                actionUrl
            );
            
            log.info("가게 사장에게 액션 포함 알림 전송 완료 - storeId: {}", storeId);
        } catch (Exception e) {
            log.error("사장 액션 포함 알림 API 호출 실패 - storeId: {}, operation: sendOwnerNotificationWithAction, details: {}", 
                storeId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }
}