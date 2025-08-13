package com.kkulddip.order.infrastructure.integration;

import org.springframework.stereotype.Service;

import com.kkulddip.order.application.service.NotificationService;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.common.util.RedisNotificationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final RedisNotificationUtil redisNotificationUtil;

    @Override
    public void sendNotificationToCustomer(Long customerId, String message) {
        try {
            log.info("고객에게 알림 전송 시작 - customerId: {}", customerId);
            
            redisNotificationUtil.publishCustomerNotification(
                customerId,
                "주문 알림", // 기본 제목
                message,
                NotificationType.ORDER
            );
            
            log.info("고객에게 알림 전송 완료 - customerId: {}", customerId);
        } catch (Exception e) {
            log.error("고객 알림 API 호출 실패 - customerId: {}, operation: sendCustomerNotification, details: {}", 
                customerId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    @Override
    public void sendNotificationToOwner(Long ownerId, String message) {
        try {
            log.info("특정 사장에게 알림 전송 시작 - ownerId: {}", ownerId);
            
            redisNotificationUtil.publishOwnerNotification(
                ownerId,
                "주문 알림", // 기본 제목
                message,
                NotificationType.ORDER
            );
            
            log.info("특정 사장에게 알림 전송 완료 - ownerId: {}", ownerId);
        } catch (Exception e) {
            log.error("사장 알림 API 호출 실패 - ownerId: {}, operation: sendOwnerNotification, details: {}", 
                ownerId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    @Override
    public void sendNotificationToStore(Long storeId, String message) {
        try {
            log.info("가게 사장들에게 알림 전송 시작 - storeId: {}", storeId);
            
            redisNotificationUtil.publishStoreNotification(
                storeId,
                "주문 알림", // 기본 제목
                message,
                NotificationType.ORDER
            );
            
            log.info("가게 사장들에게 알림 전송 완료 - storeId: {}", storeId);
        } catch (Exception e) {
            log.error("가게 알림 API 호출 실패 - storeId: {}, operation: sendStoreNotification, details: {}", 
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
            
            redisNotificationUtil.publishCustomerNotification(
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
    public void sendNotificationToOwner(Long ownerId, String title, String message, NotificationType notificationType) {
        try {
            log.info("특정 사장에게 특정 타입 알림 전송 시작 - ownerId: {}, type: {}", ownerId, notificationType);
            
            redisNotificationUtil.publishOwnerNotification(
                ownerId,
                title,
                message,
                notificationType
            );
            
            log.info("특정 사장에게 특정 타입 알림 전송 완료 - ownerId: {}, type: {}", ownerId, notificationType);
        } catch (Exception e) {
            log.error("사장 특정 타입 알림 API 호출 실패 - ownerId: {}, type: {}, operation: sendOwnerNotificationWithType, details: {}", 
                ownerId, notificationType, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 특정 알림 타입으로 가게 사장들에게 알림 전송
     */
    public void sendNotificationToStore(Long storeId, String title, String message, NotificationType notificationType) {
        try {
            log.info("가게 사장들에게 특정 타입 알림 전송 시작 - storeId: {}, type: {}", storeId, notificationType);
            
            redisNotificationUtil.publishStoreNotification(
                storeId,
                title,
                message,
                notificationType
            );
            
            log.info("가게 사장들에게 특정 타입 알림 전송 완료 - storeId: {}, type: {}", storeId, notificationType);
        } catch (Exception e) {
            log.error("가게 특정 타입 알림 API 호출 실패 - storeId: {}, type: {}, operation: sendStoreNotificationWithType, details: {}", 
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
            
            redisNotificationUtil.publishCustomerNotificationWithAction(
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
            Long ownerId, 
            String title, 
            String message, 
            NotificationType notificationType,
            String actionUrl
    ) {
        try {
            log.info("특정 사장에게 액션 포함 알림 전송 시작 - ownerId: {}, type: {}, actionUrl: {}", 
                ownerId, notificationType, actionUrl);
            
            redisNotificationUtil.publishOwnerNotificationWithAction(
                ownerId,
                title,
                message,
                notificationType,
                actionUrl
            );
            
            log.info("특정 사장에게 액션 포함 알림 전송 완료 - ownerId: {}", ownerId);
        } catch (Exception e) {
            log.error("사장 액션 포함 알림 API 호출 실패 - ownerId: {}, operation: sendOwnerNotificationWithAction, details: {}", 
                ownerId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 액션 URL이 포함된 가게 사장들 알림 전송
     */
    public void sendNotificationToStoreWithAction(
            Long storeId, 
            String title, 
            String message, 
            NotificationType notificationType,
            String actionUrl
    ) {
        try {
            log.info("가게 사장들에게 액션 포함 알림 전송 시작 - storeId: {}, type: {}, actionUrl: {}", 
                storeId, notificationType, actionUrl);
            
            redisNotificationUtil.publishStoreNotificationWithAction(
                storeId,
                title,
                message,
                notificationType,
                actionUrl
            );
            
            log.info("가게 사장들에게 액션 포함 알림 전송 완료 - storeId: {}", storeId);
        } catch (Exception e) {
            log.error("가게 액션 포함 알림 API 호출 실패 - storeId: {}, operation: sendStoreNotificationWithAction, details: {}", 
                storeId, e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }
}