package com.kkulddip.notification.application.mapper;

import com.kkulddip.notification.domain.model.valueobject.NotificationRecord;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.NotificationLogEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationLogMapper {

    /**
     * Domain -> Entity 변환
     */
    public NotificationLogEntity toEntity(NotificationRecord domain) {
        if (domain == null) {
            return null;
        }

        return NotificationLogEntity.builder()
            .notificationLogId(domain.getNotificationLogId())
            .notificationId(domain.getNotificationId())
            .recipientUserId(domain.getRecipientUserId())
            .recipientType(domain.getRecipientType())
            .fcmToken(domain.getFcmToken())
            .isSent(domain.getIsSent())
            .sentAt(domain.getSentAt())
            .errorMessage(domain.getErrorMessage())
            .retryCount(domain.getRetryCount())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    /**
     * Entity -> Domain 변환
     */
    public NotificationRecord toDomain(NotificationLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return NotificationRecord.builder()
            .notificationLogId(entity.getNotificationLogId())
            .notificationId(entity.getNotificationId())
            .recipientUserId(entity.getRecipientUserId())
            .recipientType(entity.getRecipientType())
            .fcmToken(entity.getFcmToken())
            .isSent(entity.getIsSent())
            .sentAt(entity.getSentAt())
            .errorMessage(entity.getErrorMessage())
            .retryCount(entity.getRetryCount())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}