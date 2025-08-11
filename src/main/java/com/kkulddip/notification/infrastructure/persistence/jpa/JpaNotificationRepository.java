package com.kkulddip.notification.infrastructure.persistence.jpa;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 JPA 레포지토리 인터페이스
 *
 * @author Claude
 * @since 1.0
 */
@Repository
public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 구독자별 알림 목록을 조회합니다.
     */
    List<Notification> findBySubscriberIdAndSubscriberTypeOrderByCreatedAtDesc(
        Long subscriberId, 
        SubscriberType subscriberType
    );

    /**
     * 구독자 타입별 알림 목록을 조회합니다.
     */
    List<Notification> findBySubscriberTypeOrderByCreatedAtDesc(SubscriberType subscriberType);

    /**
     * 아직 발송되지 않은 알림 목록을 조회합니다.
     */
    @Query("SELECT n FROM Notification n WHERE n.sentAt IS NULL ORDER BY n.createdAt ASC")
    List<Notification> findUnsentNotifications();

    /**
     * 예약된 시간이 되어 발송 가능한 알림들을 조회합니다.
     */
    @Query("SELECT n FROM Notification n WHERE n.sentAt IS NULL " +
           "AND (n.scheduledAt IS NULL OR n.scheduledAt <= :currentTime) " +
           "ORDER BY n.createdAt ASC")
    List<Notification> findReadyToSendNotifications(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 특정 기간 동안의 알림을 조회합니다.
     */
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    /**
     * 구독자별 특정 기간 동안의 알림을 조회합니다.
     */
    List<Notification> findBySubscriberIdAndSubscriberTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long subscriberId,
        SubscriberType subscriberType,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}