package com.kkulddip.notification.infrastructure.persistence.jpa.adapter;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.domain.repository.NotificationRepository;
import com.kkulddip.notification.application.mapper.NotificationMapper;
import com.kkulddip.notification.infrastructure.persistence.jpa.repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaRepository;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        var entity = mapper.toEntity(notification);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return jpaRepository.findById(notificationId)
            .map(mapper::toDomain);
    }

    @Override
    public List<Notification> findScheduledNotifications() {
        return jpaRepository.findScheduledNotifications(LocalDateTime.now())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findBySubscriberType(SubscriberType subscriberType) {
        return jpaRepository.findBySubscriberType(subscriberType)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByNotificationType(NotificationType notificationType) {
        return jpaRepository.findByNotificationType(notificationType)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByDateRange(start, end)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public int deleteOldNotifications(LocalDateTime before) {
        return jpaRepository.deleteOldNotificationsBatch(before);
    }

    @Override
    public List<Notification> findUnsentNotifications() {
        return jpaRepository.findUnsentNotifications(LocalDateTime.now())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByNotificationType(NotificationType notificationType) {
        return jpaRepository.countByNotificationType(notificationType);
    }
}