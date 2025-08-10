package com.kkulddip.notification.infrastructure.persistence.jpa.adapter;

import com.kkulddip.notification.domain.model.valueobject.NotificationRecord;
import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.infrastructure.persistence.jpa.mapper.NotificationLogMapper;
import com.kkulddip.notification.infrastructure.persistence.jpa.repository.JpaNotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final JpaNotificationLogRepository jpaRepository;
    private final NotificationLogMapper mapper;

    @Override
    public NotificationRecord save(NotificationRecord notificationRecord) {
        var entity = mapper.toEntity(notificationRecord);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<NotificationRecord> findById(Long notificationLogId) {
        return jpaRepository.findById(notificationLogId)
            .map(mapper::toDomain);
    }

    @Override
    public List<NotificationRecord> findByNotificationId(Long notificationId) {
        return jpaRepository.findByNotificationId(notificationId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countTodaySuccessfulNotifications() {
        return jpaRepository.countTodaySuccessfulNotifications();
    }

    @Override
    public long countTodayFailedNotifications() {
        return jpaRepository.countTodayFailedNotifications();
    }

    @Override
    public long countSuccessfulNotificationsByUserType(UserType userType) {
        return jpaRepository.countSuccessfulNotificationsByUserType(userType);
    }

    @Override
    public List<NotificationRecord> findFailedNotificationsForRetry(int maxRetry) {
        return jpaRepository.findFailedNotificationsForRetry(maxRetry)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteOldLogs(LocalDateTime before) {
        jpaRepository.deleteOldLogs(before);
    }

    @Override
    public long countByNotificationId(Long notificationId) {
        return jpaRepository.countByNotificationId(notificationId);
    }

    @Override
    public double getSuccessRateByDateRange(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.getSuccessRateByDateRange(start, end);
    }
}