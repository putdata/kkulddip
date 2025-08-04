package com.kkulddip.notification.domain.repository;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 도메인의 핵심 저장소 인터페이스
 * 알림 데이터의 CRUD 및 다양한 조회 기능을 제공합니다.
 */
public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long notificationId);

    List<Notification> findScheduledNotifications();

    List<Notification> findBySubscriberType(SubscriberType subscriberType);

    List<Notification> findByNotificationType(NotificationType notificationType);

    List<Notification> findByDateRange(LocalDateTime start, LocalDateTime end);

    int deleteOldNotifications(LocalDateTime before);

    List<Notification> findUnsentNotifications();

    long countByNotificationType(NotificationType notificationType);
}