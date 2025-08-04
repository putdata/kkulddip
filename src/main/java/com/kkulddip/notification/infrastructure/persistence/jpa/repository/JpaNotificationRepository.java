package com.kkulddip.notification.infrastructure.persistence.jpa.repository;

import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * 예약된 알림 조회
     */
    @Query("SELECT n FROM NotificationEntity n WHERE n.scheduledAt <= :now AND n.sentAt IS NULL")
    List<NotificationEntity> findScheduledNotifications(@Param("now") LocalDateTime now);

    /**
     * 타겟 타입별 알림 조회
     */
    List<NotificationEntity> findBySubscriberType(SubscriberType subscriberType);

    /**
     * 알림 타입별 조회
     */
    List<NotificationEntity> findByNotificationType(NotificationType notificationType);

    /**
     * 기간별 알림 조회
     */
    @Query("SELECT n FROM NotificationEntity n WHERE n.createdAt BETWEEN :start AND :end")
    List<NotificationEntity> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

    /**
     * 오래된 알림 삭제
     */
    @Modifying
    @Query(value = "DELETE FROM notification WHERE created_at < :before LIMIT 1000", nativeQuery = true)
    int deleteOldNotificationsBatch(@Param("before") LocalDateTime before);

    /**
     * 발송되지 않은 알림 조회
     */
    @Query("SELECT n FROM NotificationEntity n WHERE n.sentAt IS NULL AND (n.scheduledAt IS NULL OR n.scheduledAt <= :now)")
    List<NotificationEntity> findUnsentNotifications(@Param("now") LocalDateTime now);

    /**
     * 알림 타입별 개수 조회
     */
    long countByNotificationType(NotificationType notificationType);
}