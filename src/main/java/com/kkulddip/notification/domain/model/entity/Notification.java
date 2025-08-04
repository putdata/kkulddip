package com.kkulddip.notification.domain.model.entity;

import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.PublisherType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Notification {

    private final Long notificationId;
    private final String title;
    private final String content;
    private final Long publisherId;
    private final PublisherType publisherType;
    private final NotificationType notificationType;
    private final SubscriberType subscriberType;
    private final Long subscriberId;
    private final String actionUrl;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime sentAt;
    private final LocalDateTime createdAt;

    /**
     * 전체 발송 여부
     */
    public boolean isBroadcast() {
        return subscriberType == SubscriberType.ALL ||
            subscriberType == SubscriberType.CUSTOMER ||
            subscriberType == SubscriberType.OWNER;
    }

    /**
     * 특정 사용자 대상 여부
     */
    public boolean isTargeted() {
        return subscriberType == SubscriberType.SPECIFIC && subscriberId != null;
    }

    /**
     * 즉시 발송 여부
     */
    public boolean isImmediate() {
        return scheduledAt == null ||
            scheduledAt.isBefore(LocalDateTime.now()) ||
            scheduledAt.isEqual(LocalDateTime.now());
    }

    /**
     * 발송 완료 여부
     */
    public boolean isSent() {
        return sentAt != null;
    }

    /**
     * 알림 유효성 검증
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() &&
            content != null && !content.trim().isEmpty() &&
            notificationType != null &&
            subscriberType != null &&
            (subscriberType != SubscriberType.SPECIFIC || subscriberId != null);
    }

    /**
     * 발송 완료 처리
     */
    public Notification markAsSent() {
        return this.toBuilder()
            .sentAt(LocalDateTime.now())
            .build();
    }
}