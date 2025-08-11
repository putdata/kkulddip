package com.kkulddip.notification.domain.repository;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.enums.SubscriberType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 도메인 레포지토리 인터페이스
 *
 * @author Claude
 * @since 1.0
 */
public interface NotificationRepository {

    /**
     * 알림을 저장합니다.
     *
     * @param notification 저장할 알림
     * @return 저장된 알림
     */
    Notification save(Notification notification);

    /**
     * ID로 알림을 조회합니다.
     *
     * @param notificationId 알림 ID
     * @return 알림 정보
     */
    Optional<Notification> findById(Long notificationId);

    /**
     * 구독자별 알림 목록을 조회합니다.
     *
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @return 알림 목록
     */
    List<Notification> findBySubscriberIdAndSubscriberType(Long subscriberId, SubscriberType subscriberType);

    /**
     * 구독자 타입별 알림 목록을 조회합니다.
     *
     * @param subscriberType 구독자 타입
     * @return 알림 목록
     */
    List<Notification> findBySubscriberType(SubscriberType subscriberType);

    /**
     * 아직 발송되지 않은 알림 목록을 조회합니다.
     *
     * @return 미발송 알림 목록
     */
    List<Notification> findUnsentNotifications();

    /**
     * 예약된 시간이 되어 발송 가능한 알림들을 조회합니다.
     *
     * @param currentTime 현재 시간
     * @return 발송 가능한 알림 목록
     */
    List<Notification> findReadyToSendNotifications(LocalDateTime currentTime);

    /**
     * 특정 기간 동안의 알림을 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 알림 목록
     */
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 구독자별 특정 기간 동안의 알림을 조회합니다.
     *
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 구독자의 특정 기간 알림 목록
     */
    List<Notification> findBySubscriberIdAndSubscriberTypeAndCreatedAtBetween(
            Long subscriberId, 
            SubscriberType subscriberType, 
            LocalDateTime startDate, 
            LocalDateTime endDate
    );
}