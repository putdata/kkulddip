package com.kkulddip.notification.infrastructure.persistence.jpa.mapper;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    /**
     * Domain -> Entity 변환
     */
    public NotificationEntity toEntity(Notification domain) {
        if (domain == null) {
            return null;
        }

        return NotificationEntity.builder()
            .notificationId(domain.getNotificationId())
            .title(domain.getTitle())
            .content(domain.getContent())
            .publisherId(domain.getPublisherId())
            .publisherType(domain.getPublisherType())
            .notificationType(domain.getNotificationType())
            .subscriberType(domain.getSubscriberType())
            .subscriberId(domain.getSubscriberId())
            .actionUrl(domain.getActionUrl())
            .scheduledAt(domain.getScheduledAt())
            .sentAt(domain.getSentAt())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    /**
     * Entity -> Domain 변환
     */
    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return Notification.builder()
            .notificationId(entity.getNotificationId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .publisherId(entity.getPublisherId())
            .publisherType(entity.getPublisherType())
            .notificationType(entity.getNotificationType())
            .subscriberType(entity.getSubscriberType())
            .subscriberId(entity.getSubscriberId())
            .actionUrl(entity.getActionUrl())
            .scheduledAt(entity.getScheduledAt())
            .sentAt(entity.getSentAt())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}