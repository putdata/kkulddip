package com.kkulddip.notification.infrastructure.messaging.redis.processor;

import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
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
    private final NotificationSenderService notificationSenderService;

    /**
     * 알림 요청을 처리합니다.
     *
     * @param request 알림 요청
     * @return 처리 성공 여부
     */
    public boolean processNotification(NotificationRequest request) {
        try {
            log.debug("알림 처리 시작 - id: {}, title: {}, subscriberType: {}, publisherType: {}, publisherId: {}, subscriberId: {}", 
                request.getId(), request.getTitle(), request.getSubscriberType(), 
                request.getPublisherType(), request.getPublisherId(), request.getSubscriberId());

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
                log.info("알림 처리 완료 - notificationId: {}, publisherType: {}, subscriberType: {}", 
                    notification.getNotificationId(), notification.getPublisherType(), notification.getSubscriberType());
            } else {
                log.error("알림 발송 실패 - notificationId: {}, publisherType: {}, subscriberType: {}, publisherId: {}, subscriberId: {}", 
                    notification.getNotificationId(), notification.getPublisherType(), notification.getSubscriberType(),
                    notification.getPublisherId(), notification.getSubscriberId());
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
            case OWNER -> sendToOwner(notification, request);
            case STORE -> sendToStore(notification, request);
        };
    }

    /**
     * CUSTOMER 타입 알림을 처리합니다.
     * subscriberId가 있으면 특정 고객에게, 없으면 브로드캐스트 패턴을 확인하여 처리합니다.
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
            // subscriberId가 없는 경우 - 브로드캐스트 패턴 확인
            if (notificationSenderService.isStoreFavoriteBroadcast(notification)) {
                // 가게의 즐겨찾기 고객들에게 브로드캐스트
                Long storeId = notification.getPublisherId();
                if (storeId == null) {
                    log.error("브로드캐스트 알림이지만 publisherId(storeId)가 null입니다. - notificationId: {}", 
                        notification.getNotificationId());
                    return false;
                }
                
                log.info("가게 즐겨찾기 고객 브로드캐스트 알림 발송 시작 - notificationId: {}, storeId: {}", 
                    notification.getNotificationId(), storeId);
                
                try {
                    boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);
                    if (result) {
                        log.info("가게 즐겨찾기 고객 브로드캐스트 알림 발송 성공 - notificationId: {}, storeId: {}", 
                            notification.getNotificationId(), storeId);
                    } else {
                        log.warn("가게 즐겨찾기 고객 브로드캐스트 알림 발송 실패 - notificationId: {}, storeId: {}", 
                            notification.getNotificationId(), storeId);
                    }
                    return result;
                } catch (Exception e) {
                    log.error("가게 즐겨찾기 고객 브로드캐스트 알림 발송 중 오류 발생 - notificationId: {}, storeId: {}, error: {}", 
                        notification.getNotificationId(), storeId, e.getMessage(), e);
                    return false;
                }
            } else {
                // 일반적인 전체 고객 알림 발송
                log.debug("전체 고객 알림 발송");
                return notificationSenderService.sendToAllCustomers(notification);
            }
        }
    }

    /**
     * OWNER 타입 알림을 처리합니다.
     * subscriberId가 있으면 특정 사장에게, 없으면 모든 사장에게 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendToOwner(NotificationResponse notification, NotificationRequest request) {
        if (request.getSubscriberId() != null) {
            // subscriberId가 ownerId인 경우 - 특정 사장에게 발송
            log.debug("특정 사장 알림 발송 - ownerId: {}", request.getSubscriberId());
            return notificationSenderService.sendToSpecificUser(
                notification, 
                request.getSubscriberId(), 
                RecipientType.OWNER
            );
        } else {
            // subscriberId가 없는 경우 - 모든 사장에게 발송
            log.debug("전체 사장 알림 발송");
            return notificationSenderService.sendToAllOwners(notification);
        }
    }

    /**
     * STORE 타입 알림을 처리합니다.
     * subscriberId가 있으면 특정 가게의 사장들에게, 없으면 모든 사장에게 발송합니다.
     *
     * @param notification 저장된 알림
     * @param request 원본 요청
     * @return 발송 성공 여부
     */
    private boolean sendToStore(NotificationResponse notification, NotificationRequest request) {
        if (request.getSubscriberId() != null) {
            // subscriberId가 storeId인 경우 - 특정 가게의 사장들에게 발송
            log.debug("특정 가게 알림 발송 - storeId: {}", request.getSubscriberId());
            return notificationSenderService.sendToStoreOwner(notification, request.getSubscriberId());
        } else {
            // subscriberId가 없는 경우 - 모든 사장에게 발송
            log.debug("전체 사장 알림 발송");
            return notificationSenderService.sendToAllOwners(notification);
        }
    }

}