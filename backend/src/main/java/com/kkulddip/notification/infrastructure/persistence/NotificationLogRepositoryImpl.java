package com.kkulddip.notification.infrastructure.persistence;

import com.kkulddip.notification.domain.model.entity.NotificationLog;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.infrastructure.persistence.jpa.JpaNotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 로그 레포지토리 구현체
 *
 * @author Claude
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final JpaNotificationLogRepository jpaNotificationLogRepository;

    @Override
    public NotificationLog save(NotificationLog notificationLog) {
        return jpaNotificationLogRepository.save(notificationLog);
    }

    @Override
    public Optional<NotificationLog> findById(Long notificationLogId) {
        return jpaNotificationLogRepository.findById(notificationLogId);
    }

    @Override
    public List<NotificationLog> findByNotificationId(Long notificationId) {
        return jpaNotificationLogRepository.findByNotificationIdOrderByCreatedAtDesc(notificationId);
    }

    @Override
    public List<NotificationLog> findByRecipientUserIdAndRecipientType(
        Long recipientUserId, 
        RecipientType recipientType) {
        return jpaNotificationLogRepository
            .findByRecipientUserIdAndRecipientTypeOrderByCreatedAtDesc(recipientUserId, recipientType);
    }

    @Override
    public List<NotificationLog> findFailedLogsForRetry(int maxRetryCount) {
        return jpaNotificationLogRepository.findFailedLogsForRetry(maxRetryCount);
    }

    @Override
    public NotificationLogStats getStatsByNotificationId(Long notificationId) {
        return jpaNotificationLogRepository.getStatsByNotificationId(notificationId);
    }

    @Override
    public List<NotificationLog> findByCreatedAtBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate) {
        return jpaNotificationLogRepository
            .findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    @Override
    public List<NotificationLog> findByRecipientUserIdAndRecipientTypeAndCreatedAtBetween(
        Long recipientUserId,
        RecipientType recipientType,
        LocalDateTime startDate,
        LocalDateTime endDate) {
        return jpaNotificationLogRepository
            .findByRecipientUserIdAndRecipientTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                recipientUserId, recipientType, startDate, endDate);
    }
}