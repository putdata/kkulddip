package com.kkulddip.notification.infrastructure.persistence.jpa.repository;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface JpaNotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {

    /**
     * 알림별 기록 조회
     */
    List<NotificationLogEntity> findByNotificationId(Long notificationId);

    /**
     * 오늘 성공한 알림 수
     */
    @Query("SELECT COUNT(nl) FROM NotificationLogEntity nl WHERE DATE(nl.createdAt) = CURRENT_DATE AND nl.isSent = true")
    long countTodaySuccessfulNotifications();

    /**
     * 오늘 실패한 알림 수
     */
    @Query("SELECT COUNT(nl) FROM NotificationLogEntity nl WHERE DATE(nl.createdAt) = CURRENT_DATE AND nl.isSent = false")
    long countTodayFailedNotifications();

    /**
     * 사용자 타입별 성공한 알림 수
     */
    @Query("SELECT COUNT(nl) FROM NotificationLogEntity nl WHERE nl.recipientType = :userType AND nl.isSent = true")
    long countSuccessfulNotificationsByUserType(@Param("userType") UserType userType);

    /**
     * 재시도 가능한 실패 알림 조회
     */
    @Query("SELECT nl FROM NotificationLogEntity nl WHERE nl.isSent = false AND nl.retryCount < :maxRetry")
    List<NotificationLogEntity> findFailedNotificationsForRetry(@Param("maxRetry") int maxRetry);

    /**
     * 오래된 로그 삭제
     */
    @Modifying
    @Query("DELETE FROM NotificationLogEntity nl WHERE nl.createdAt < :before")
    void deleteOldLogs(@Param("before") LocalDateTime before);

    /**
     * 알림별 기록 수 조회
     */
    long countByNotificationId(Long notificationId);

    /**
     * 기간별 성공률 조회
     */
    @Query("""
        SELECT CASE 
            WHEN COUNT(nl) = 0 THEN 0.0 
            ELSE (CAST(SUM(CASE WHEN nl.isSent = true THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(nl)) * 100 
        END
        FROM NotificationLogEntity nl 
        WHERE nl.createdAt BETWEEN :start AND :end
        """)
    double getSuccessRateByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}