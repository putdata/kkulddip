package com.kkulddip.notification.application.service;

import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationLogResponse;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.entity.NotificationLog;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.domain.repository.NotificationRepository;
import com.kkulddip.notification.domain.service.NotificationDomainService;
import com.kkulddip.common.util.RedisNotificationUtil;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 알림 애플리케이션 서비스
 *
 * <p>알림 관련 비즈니스 로직을 처리합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationDomainService notificationDomainService;
    private final RedisNotificationUtil redisNotificationUtil;
    private final StoreRepository storeRepository;

    /**
     * 새로운 알림을 Redis에 발행합니다 (DB 저장 안함).
     *
     * @param request 알림 요청 정보
     * @return 발행된 알림 응답 (임시 객체)
     */
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("알림 Redis 발행 시작 - title: {}, subscriberType: {}", 
            request.getTitle(), request.getSubscriberType());

        // Redis에 알림 요청 발행
        try {
            redisNotificationUtil.publishNotification(request);
            log.info("Redis 알림 발행 완료 - title: {}", request.getTitle());
        } catch (Exception e) {
            log.error("Redis 알림 발행 실패 - title: {}, error: {}", 
                request.getTitle(), e.getMessage(), e);
            throw new RuntimeException("알림 발행 실패", e);
        }

        // 임시 응답 객체 생성 (실제 DB 저장 전)
        return NotificationResponse.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .publisherId(request.getPublisherId())
            .publisherType(request.getPublisherType())
            .subscriberId(request.getSubscriberId())
            .subscriberType(request.getSubscriberType())
            .actionUrl(request.getActionUrl())
            .notificationType(request.getNotificationType())
            .scheduledAt(request.getScheduledAt())
            .isSent(false)
            .build();
    }

    /**
     * Redis Consumer에서 호출하는 실제 알림 생성 메서드 (DB 저장).
     *
     * @param request 알림 요청 정보
     * @return 생성된 알림 응답
     */
    @Transactional
    public NotificationResponse createNotificationFromRedis(NotificationRequest request) {
        log.info("알림 DB 저장 시작 - title: {}, subscriberType: {}", 
            request.getTitle(), request.getSubscriberType());

        Notification notification = notificationDomainService.createNotification(
            request.getTitle(),
            request.getContent(),
            request.getPublisherId(),
            request.getPublisherType(),
            request.getSubscriberId(),
            request.getSubscriberType(),
            request.getActionUrl(),
            request.getNotificationType(),
            request.getScheduledAt()
        );

        Notification savedNotification = notificationRepository.save(notification);
        log.info("알림 DB 저장 완료 - notificationId: {}", savedNotification.getNotificationId());

        return toNotificationResponse(savedNotification);
    }


    /**
     * 인증된 사용자의 알림 목록을 조회합니다.
     *
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @param authenticatedUserId 인증된 사용자 ID
     * @param authenticatedRole 인증된 사용자 역할
     * @return 알림 목록
     */
    public List<NotificationResponse> getNotificationsBySubscriberWithAuth(
            Long subscriberId, 
            SubscriberType subscriberType,
            Long authenticatedUserId,
            String authenticatedRole) {

        log.info("인증된 사용자 알림 조회 - subscriberId: {}, subscriberType: {}, authUserId: {}, role: {}",
            subscriberId, subscriberType, authenticatedUserId, authenticatedRole);

        // 권한 검증
        if (!hasPermissionToViewNotifications(subscriberId, subscriberType, authenticatedUserId, authenticatedRole)) {
            log.warn("알림 조회 권한 없음 - subscriberId: {}, subscriberType: {}, authUserId: {}",
                subscriberId, subscriberType, authenticatedUserId);
            throw new RuntimeException("알림을 조회할 권한이 없습니다.");
        }

        List<Notification> notifications = notificationRepository
            .findBySubscriberIdAndSubscriberType(subscriberId, subscriberType);

        return notifications.stream()
            .map(this::toNotificationResponse)
            .collect(Collectors.toList());
    }

    /**
     * 구독자별 알림 목록을 조회합니다. (내부용)
     *
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @return 알림 목록
     */
    public List<NotificationResponse> getNotificationsBySubscriber(
            Long subscriberId, 
            SubscriberType subscriberType) {
        List<Notification> notifications = notificationRepository
            .findBySubscriberIdAndSubscriberType(subscriberId, subscriberType);

        return notifications.stream()
            .map(this::toNotificationResponse)
            .collect(Collectors.toList());
    }

    /**
     * 사용자가 해당 알림을 조회할 권한이 있는지 확인합니다.
     *
     * @param subscriberId 구독자 ID
     * @param subscriberType 구독자 타입
     * @param authenticatedUserId 인증된 사용자 ID
     * @param authenticatedRole 인증된 사용자 역할
     * @return 권한 여부
     */
    private boolean hasPermissionToViewNotifications(
            Long subscriberId,
            SubscriberType subscriberType,
            Long authenticatedUserId,
            String authenticatedRole) {

        switch (authenticatedRole.toUpperCase()) {
            case "CUSTOMER":
                // Customer는 자신의 알림만 조회 가능
                return subscriberType == SubscriberType.CUSTOMER 
                    && subscriberId.equals(authenticatedUserId);

            case "OWNER":
                // Owner는 자신의 알림 또는 자신이 관리하는 가게의 알림 조회 가능
                if (subscriberType == SubscriberType.OWNER) {
                    // OWNER 타입: 본인의 사장 알림만 조회 가능
                    return subscriberId.equals(authenticatedUserId);
                } else if (subscriberType == SubscriberType.STORE) {
                    // STORE 타입: 자신이 관리하는 가게의 알림 조회 가능
                    List<Long> managedStoreIds = storeRepository.findStoreIdsByOwnerId(authenticatedUserId);
                    return managedStoreIds.contains(subscriberId);
                } else {
                    return false;
                }

            default:
                log.warn("알 수 없는 사용자 역할: {}", authenticatedRole);
                return false;
        }
    }

    /**
     * 알림을 발송 완료로 표시합니다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void markNotificationAsSent(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsSent();
            notificationRepository.save(notification);
            log.info("알림 발송 완료 처리 - notificationId: {}", notificationId);
        }
    }

    /**
     * 알림 로그를 생성합니다.
     *
     * @param notificationId 알림 ID
     * @param recipientUserId 수신자 사용자 ID
     * @param recipientType 수신자 타입
     * @param fcmToken FCM 토큰
     * @return 생성된 알림 로그 응답
     */
    @Transactional
    public NotificationLogResponse createNotificationLog(
            Long notificationId,
            Long recipientUserId,
            RecipientType recipientType,
            String fcmToken) {
        
        log.debug("알림 로그 생성 - notificationId: {}, recipientUserId: {}", 
            notificationId, recipientUserId);

        NotificationLog notificationLog = notificationDomainService.createNotificationLog(
            notificationId,
            recipientUserId,
            recipientType,
            fcmToken
        );

        NotificationLog savedLog = notificationLogRepository.save(notificationLog);
        return toNotificationLogResponse(savedLog);
    }

    /**
     * 알림 로그를 발송 성공으로 표시합니다.
     *
     * @param logId 알림 로그 ID
     */
    @Transactional
    public void markLogAsSent(Long logId) {
        Optional<NotificationLog> logOpt = notificationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            NotificationLog log = logOpt.get();
            log.markAsSent();
            notificationLogRepository.save(log);
        }
    }

    /**
     * 알림 로그를 발송 실패로 표시합니다.
     *
     * @param logId 알림 로그 ID
     * @param errorMessage 에러 메시지
     */
    @Transactional
    public void markLogAsFailed(Long logId, String errorMessage) {
        Optional<NotificationLog> logOpt = notificationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            NotificationLog log = logOpt.get();
            log.markAsFailed(errorMessage);
            notificationLogRepository.save(log);
        }
    }



    /**
     * Notification 엔티티를 NotificationResponse로 변환합니다.
     */
    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
            .notificationId(notification.getNotificationId())
            .title(notification.getTitle())
            .content(notification.getContent())
            .publisherId(notification.getPublisherId())
            .publisherType(notification.getPublisherType())
            .subscriberId(notification.getSubscriberId())
            .subscriberType(notification.getSubscriberType())
            .actionUrl(notification.getActionUrl())
            .notificationType(notification.getNotificationType())
            .scheduledAt(notification.getScheduledAt())
            .sentAt(notification.getSentAt())
            .createdAt(notification.getCreatedAt())
            .isSent(notification.isSent())
            .build();
    }

    /**
     * NotificationLog 엔티티를 NotificationLogResponse로 변환합니다.
     */
    private NotificationLogResponse toNotificationLogResponse(NotificationLog log) {
        return NotificationLogResponse.builder()
            .notificationLogId(log.getNotificationLogId())
            .notificationId(log.getNotificationId())
            .recipientUserId(log.getRecipientUserId())
            .recipientType(log.getRecipientType())
            .fcmToken(log.getFcmToken())
            .isSent(log.getIsSent())
            .retryCount(log.getRetryCount())
            .errorMessage(log.getErrorMessage())
            .sentAt(log.getSentAt())
            .createdAt(log.getCreatedAt())
            .build();
    }
}