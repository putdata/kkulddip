package com.kkulddip.notification.infrastructure.persistence;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.domain.repository.NotificationRepository;
import com.kkulddip.notification.infrastructure.persistence.jpa.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 레포지토리 구현체
 *
 * @author Claude
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public Notification save(Notification notification) {
        return jpaNotificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return jpaNotificationRepository.findById(notificationId);
    }

    @Override
    public List<Notification> findBySubscriberIdAndSubscriberType(
        Long subscriberId, 
        SubscriberType subscriberType) {
        return jpaNotificationRepository
            .findBySubscriberIdAndSubscriberTypeOrderByCreatedAtDesc(subscriberId, subscriberType);
    }

    @Override
    public List<Notification> findBySubscriberType(SubscriberType subscriberType) {
        return jpaNotificationRepository.findBySubscriberTypeOrderByCreatedAtDesc(subscriberType);
    }

    @Override
    public List<Notification> findUnsentNotifications() {
        return jpaNotificationRepository.findUnsentNotifications();
    }

    @Override
    public List<Notification> findReadyToSendNotifications(LocalDateTime currentTime) {
        return jpaNotificationRepository.findReadyToSendNotifications(currentTime);
    }

    @Override
    public List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaNotificationRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    @Override
    public List<Notification> findBySubscriberIdAndSubscriberTypeAndCreatedAtBetween(
        Long subscriberId,
        SubscriberType subscriberType,
        LocalDateTime startDate,
        LocalDateTime endDate) {
        return jpaNotificationRepository
            .findBySubscriberIdAndSubscriberTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                subscriberId, subscriberType, startDate, endDate);
    }
}