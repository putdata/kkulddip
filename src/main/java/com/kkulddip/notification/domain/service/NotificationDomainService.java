package com.kkulddip.notification.domain.service;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.entity.NotificationLog;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 알림 도메인 서비스
 *
 * <p>알림 관련 도메인 로직을 처리합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Service
public class NotificationDomainService {

    /**
     * 새로운 알림을 생성합니다.
     *
     * @param title 알림 제목
     * @param content 알림 내용
     * @param publisherId 발행자 ID
     * @param publisherType 발행자 타입
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @param actionUrl 액션 URL
     * @param notificationType 알림 타입
     * @param scheduledAt 예약 발송 시간
     * @return 생성된 알림
     */
    public Notification createNotification(
            String title,
            String content,
            Long publisherId,
            PublisherType publisherType,
            Long subscriberId,
            SubscriberType subscriberType,
            String actionUrl,
            NotificationType notificationType,
            LocalDateTime scheduledAt
    ) {
        log.debug("알림 생성 - title: {}, subscriberType: {}, subscriberId: {}", 
                title, subscriberType, subscriberId);

        return Notification.builder()
                .title(title)
                .content(content)
                .publisherId(publisherId)
                .publisherType(publisherType)
                .subscriberId(subscriberId)
                .subscriberType(subscriberType)
                .actionUrl(actionUrl)
                .notificationType(notificationType)
                .scheduledAt(scheduledAt)
                .build();
    }

    /**
     * 즉시 발송할 알림을 생성합니다.
     *
     * @param title 알림 제목
     * @param content 알림 내용
     * @param publisherId 발행자 ID
     * @param publisherType 발행자 타입
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @param actionUrl 액션 URL
     * @param notificationType 알림 타입
     * @return 생성된 알림
     */
    public Notification createImmediateNotification(
            String title,
            String content,
            Long publisherId,
            PublisherType publisherType,
            Long subscriberId,
            SubscriberType subscriberType,
            String actionUrl,
            NotificationType notificationType
    ) {
        return createNotification(
                title, content, publisherId, publisherType, 
                subscriberId, subscriberType, actionUrl, 
                notificationType, null
        );
    }

    /**
     * 개별 수신자에 대한 알림 로그를 생성합니다.
     *
     * @param notificationId 알림 ID
     * @param recipientUserId 수신자 사용자 ID
     * @param recipientType 수신자 타입
     * @param fcmToken FCM 토큰
     * @return 생성된 알림 로그
     */
    public NotificationLog createNotificationLog(
            Long notificationId,
            Long recipientUserId,
            RecipientType recipientType,
            String fcmToken
    ) {
        log.debug("알림 로그 생성 - notificationId: {}, recipientUserId: {}, recipientType: {}", 
                notificationId, recipientUserId, recipientType);

        return NotificationLog.builder()
                .notificationId(notificationId)
                .recipientUserId(recipientUserId)
                .recipientType(recipientType)
                .fcmToken(fcmToken)
                .build();
    }

    /**
     * 구독자 타입을 수신자 타입으로 변환합니다.
     *
     * @param subscriberType 구독자 타입
     * @return 수신자 타입
     * @throws IllegalArgumentException 변환할 수 없는 구독자 타입인 경우
     */
    public RecipientType convertToRecipientType(SubscriberType subscriberType) {
        return switch (subscriberType) {
            case CUSTOMER -> RecipientType.CUSTOMER;
            case OWNER -> RecipientType.OWNER;
            case ALL, SPECIFIC -> throw new IllegalArgumentException(
                    "구독자 타입 " + subscriberType + "은 개별 수신자 타입으로 변환할 수 없습니다."
            );
        };
    }

    /**
     * 알림이 전체 발송 대상인지 확인합니다.
     *
     * @param subscriberType 구독자 타입
     * @return 전체 발송 여부
     */
    public boolean isBroadcastNotification(SubscriberType subscriberType) {
        return subscriberType == SubscriberType.ALL;
    }

    /**
     * 알림이 특정 사용자 대상인지 확인합니다.
     *
     * @param subscriberType 구독자 타입
     * @return 특정 사용자 대상 여부
     */
    public boolean isTargetedNotification(SubscriberType subscriberType) {
        return subscriberType == SubscriberType.SPECIFIC || 
               subscriberType == SubscriberType.CUSTOMER || 
               subscriberType == SubscriberType.OWNER;
    }

    /**
     * 알림 발송 가능 여부를 검증합니다.
     *
     * @param notification 검증할 알림
     * @return 발송 가능 여부
     */
    public boolean validateNotificationForSending(Notification notification) {
        if (notification == null) {
            log.warn("알림이 null입니다.");
            return false;
        }

        if (notification.isSent()) {
            log.warn("이미 발송된 알림입니다. notificationId: {}", notification.getNotificationId());
            return false;
        }

        if (!notification.isReadyToSend()) {
            log.debug("아직 발송 시간이 되지 않은 알림입니다. notificationId: {}, scheduledAt: {}", 
                    notification.getNotificationId(), notification.getScheduledAt());
            return false;
        }

        return true;
    }

    /**
     * 재시도 가능한 로그인지 검증합니다.
     *
     * @param notificationLog 검증할 알림 로그
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 가능 여부
     */
    public boolean validateLogForRetry(NotificationLog notificationLog, int maxRetryCount) {
        if (notificationLog == null) {
            log.warn("알림 로그가 null입니다.");
            return false;
        }

        if (notificationLog.getIsSent()) {
            log.debug("이미 발송 성공한 로그입니다. logId: {}", notificationLog.getNotificationLogId());
            return false;
        }

        if (!notificationLog.canRetry(maxRetryCount)) {
            log.warn("재시도 한도를 초과한 로그입니다. logId: {}, retryCount: {}, maxRetryCount: {}", 
                    notificationLog.getNotificationLogId(), notificationLog.getRetryCount(), maxRetryCount);
            return false;
        }

        return true;
    }
}