package com.kkulddip.notification.infrastructure.messaging.redis.processor;

import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.domain.service.NotificationDomainService;
import com.kkulddip.notification.infrastructure.messaging.redis.processor.sender.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 알림 요청을 처리하는 프로세서
 *
 * <p>Redis에서 받은 알림 요청을 데이터베이스에 저장하고 FCM으로 발송합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final NotificationDomainService notificationDomainService;
    private final NotificationSenderService notificationSenderService;

    /**
     * 알림 요청을 처리합니다.
     *
     * @param request 알림 요청
     * @return 처리 성공 여부
     */
    public boolean processNotification(NotificationRequest request) {
        try {
            log.debug("알림 처리 시작 - id: {}, title: {}, subscriberType: {}", 
                request.getId(), request.getTitle(), request.getSubscriberType());

            // 1. 알림을 데이터베이스에 저장 (Redis에서 처리할 때)
            NotificationResponse notification = notificationService.createNotificationFromRedis(request);
            if (notification == null) {
                log.error("알림 저장 실패 - request: {}", request);
                return false;
            }

            // 2. 구독자 타입에 따라 적절한 발송 처리
            boolean sent = sendNotificationBySubscriberType(notification, request);

            if (sent) {
                // 3. 발송 성공 시 알림을 발송 완료로 표시
                notificationService.markNotificationAsSent(notification.getNotificationId());
                log.info("알림 처리 완료 - notificationId: {}", notification.getNotificationId());
            }

            return sent;

        } catch (Exception e) {
            log.error("알림 처리 중 오류 발생 - request: {}, error: {}", request, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 구독자 타입에 따라 알림을 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendNotificationBySubscriberType(
        NotificationResponse notification, 
        NotificationRequest request) {
        
        SubscriberType subscriberType = request.getSubscriberType();
        
        return switch (subscriberType) {
            case ALL -> notificationSenderService.sendToAll(notification);
            case CUSTOMER -> sendToCustomerOrAll(notification, request);
            case OWNER -> sendToOwnerOrStore(notification, request);
            case SPECIFIC -> sendToSpecificUser(notification, request);
        };
    }

    /**
     * CUSTOMER 타입 알림을 처리합니다.
     * subscriberId가 있으면 특정 고객에게, 없으면 모든 고객에게 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendToCustomerOrAll(NotificationResponse notification, NotificationRequest request) {
        if (request.getSubscriberId() != null) {
            // subscriberId가 customerId인 경우 - 특정 고객에게 발송
            log.debug("특정 고객 알림 발송 - customerId: {}", request.getSubscriberId());
            return notificationSenderService.sendToSpecificUser(
                notification, 
                request.getSubscriberId(), 
                RecipientType.CUSTOMER
            );
        } else {
            // subscriberId가 없는 경우 - 모든 고객에게 발송
            log.debug("전체 고객 알림 발송");
            return notificationSenderService.sendToAllCustomers(notification);
        }
    }

    /**
     * OWNER 타입 알림을 처리합니다.
     * subscriberId가 있으면 특정 storeId의 사장에게, 없으면 모든 사장에게 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendToOwnerOrStore(NotificationResponse notification, NotificationRequest request) {
        if (request.getSubscriberId() != null) {
            // subscriberId가 storeId인 경우 - 특정 가게의 사장에게 발송
            log.debug("특정 가게 사장 알림 발송 - storeId: {}", request.getSubscriberId());
            return notificationSenderService.sendToStoreOwner(notification, request.getSubscriberId());
        } else {
            // subscriberId가 없는 경우 - 모든 사장에게 발송
            log.debug("전체 사장 알림 발송");
            return notificationSenderService.sendToAllOwners(notification);
        }
    }

    /**
     * 특정 사용자에게 알림을 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendToSpecificUser(NotificationResponse notification, NotificationRequest request) {
        if (request.getSubscriberId() == null) {
            log.error("SPECIFIC 타입이지만 subscriberId가 null입니다. - notificationId: {}", 
                notification.getNotificationId());
            return false;
        }

        // subscriberType이 SPECIFIC인 경우, 실제 사용자 타입을 추론해야 함
        // 여기서는 간단하게 CUSTOMER로 가정하지만, 실제로는 사용자 정보를 조회해야 할 수 있음
        RecipientType recipientType = inferRecipientType(request.getSubscriberId());
        
        return notificationSenderService.sendToSpecificUser(
            notification, 
            request.getSubscriberId(), 
            recipientType
        );
    }

    /**
     * 사용자 ID로부터 수신자 타입을 추론합니다.
     * 
     * TODO: 실제 구현에서는 사용자 정보를 조회하여 정확한 타입을 결정해야 합니다.
     *
     * @param userId 사용자 ID
     * @return 추론된 수신자 타입
     */
    private RecipientType inferRecipientType(Long userId) {
        // 현재는 기본값으로 CUSTOMER를 반환
        // 실제로는 UserService나 UserRepository를 통해 사용자 정보를 조회해야 함
        return RecipientType.CUSTOMER;
    }
}