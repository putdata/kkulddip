package com.kkulddip.notification.domain.repository;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.model.valueobject.NotificationRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository {

    NotificationRecord save(NotificationRecord notificationRecord);

    Optional<NotificationRecord> findById(Long notificationLogId);

    List<NotificationRecord> findByNotificationId(Long notificationId);

    long countTodaySuccessfulNotifications();

    long countTodayFailedNotifications();

    long countSuccessfulNotificationsByUserType(UserType userType);

    List<NotificationRecord> findFailedNotificationsForRetry(int maxRetry);

    void deleteOldLogs(LocalDateTime before);

    long countByNotificationId(Long notificationId);

    double getSuccessRateByDateRange(LocalDateTime start, LocalDateTime end);
}