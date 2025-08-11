package com.kkulddip.notification.infrastructure.persistence.jpa;

import com.kkulddip.notification.domain.model.entity.NotificationLog;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 로그 JPA 레포지토리 인터페이스
 *
 * @author Claude
 * @since 1.0
 */
@Repository
public interface JpaNotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    /**
     * 알림 ID로 관련된 모든 로그를 조회합니다.
     */
    List<NotificationLog> findByNotificationIdOrderByCreatedAtDesc(Long notificationId);

    /**
     * 수신자별 알림 로그를 조회합니다.
     */
    List<NotificationLog> findByRecipientUserIdAndRecipientTypeOrderByCreatedAtDesc(
        Long recipientUserId, 
        RecipientType recipientType
    );

    /**
     * 발송 실패한 알림 로그 중 재시도 가능한 것들을 조회합니다.
     */
    @Query("SELECT nl FROM NotificationLog nl WHERE nl.isSent = false " +
           "AND nl.retryCount < :maxRetryCount ORDER BY nl.createdAt ASC")
    List<NotificationLog> findFailedLogsForRetry(@Param("maxRetryCount") int maxRetryCount);

    /**
     * 특정 알림의 발송 통계를 조회합니다.
     */
    @Query("SELECT COUNT(nl) as totalCount, " +
           "SUM(CASE WHEN nl.isSent = true THEN 1 ELSE 0 END) as successCount " +
           "FROM NotificationLog nl WHERE nl.notificationId = :notificationId")
    NotificationLogRepository.NotificationLogStats getStatsByNotificationId(
        @Param("notificationId") Long notificationId
    );

    /**
     * 특정 기간 동안의 알림 로그를 조회합니다.
     */
    List<NotificationLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    /**
     * 특정 사용자의 특정 기간 알림 로그를 조회합니다.
     */
    List<NotificationLog> findByRecipientUserIdAndRecipientTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long recipientUserId,
        RecipientType recipientType,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}