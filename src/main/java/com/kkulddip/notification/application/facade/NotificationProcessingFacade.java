package com.kkulddip.notification.application.facade;

import com.kkulddip.notification.interfaces.dto.request.NotificationRequest;
import com.kkulddip.notification.interfaces.dto.response.NotificationResponse;
import com.kkulddip.notification.interfaces.dto.response.StatisticsDto;
import com.kkulddip.notification.application.mapper.NotificationMapper;
import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.domain.repository.NotificationRepository;
import com.kkulddip.notification.domain.repository.UserTokenRepository;
import com.kkulddip.notification.domain.service.NotificationDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationProcessingFacade {

    private final NotificationRepository notificationRepository;
    private final UserTokenRepository userTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationDomainService notificationDomainService;
    private final NotificationMapper notificationMapper;

    /**
     * 알림 요청 처리 - Redis ZSet에서 가져온 요청 처리
     */
    public NotificationResponse processNotificationRequest(NotificationRequest request) {
        log.info("=== 알림 요청 처리 시작 ===");
        log.info("제목: {}, 대상: {}", request.getTitle(), request.getSubscriberType());

        try {
            // 1. 도메인 객체 생성
            Notification notification = createNotificationFromRequest(request);

            // 2. 알림 저장
            Notification savedNotification = notificationRepository.save(notification);

            // 3. 대상 사용자 토큰 조회
            List<UserToken> targetTokens = getTargetTokens(savedNotification);

            if (targetTokens.isEmpty()) {
                log.warn("대상 토큰이 없습니다: 알림ID={}", savedNotification.getNotificationId());
            }

            // 4. FCM 발송
            notificationDomainService.sendToMultipleUsers(savedNotification, targetTokens);

            // 5. 발송 완료 처리
            Notification sentNotification = savedNotification.markAsSent();
            notificationRepository.save(sentNotification);

            // 6. 응답 생성
            String target = getTargetDescription(request);
            return NotificationResponse.success(request.getTitle(), target);

        } catch (Exception e) {
            log.error("알림 요청 처리 실패: {}", e.getMessage(), e);
            return NotificationResponse.failure("알림 발송에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 통계 정보 조회
     */
    @Transactional(readOnly = true)
    public StatisticsDto getStatistics() {
        log.debug("통계 정보 조회");

        long totalCustomers = userTokenRepository.countActiveTokensByUserType(UserType.CUSTOMER);
        long totalOwners = userTokenRepository.countActiveTokensByUserType(UserType.OWNER);
        long todaySuccess = notificationLogRepository.countTodaySuccessfulNotifications();
        long todayFailed = notificationLogRepository.countTodayFailedNotifications();

        return StatisticsDto.of(totalCustomers, totalOwners, todaySuccess, todayFailed);
    }

    /**
     * 요청으로부터 알림 도메인 객체 생성
     */
    private Notification createNotificationFromRequest(NotificationRequest request) {
        return Notification.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .publisherId(request.getPublisherId())
            .publisherType(request.getPublisherType())
            .notificationType(request.getNotificationType() != null ?
                request.getNotificationType() : NotificationType.SYSTEM)
            .subscriberType(request.getSubscriberType())
            .subscriberId(request.getSubscriberId())
            .actionUrl(request.getActionUrl())
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * 대상 토큰 조회
     */
    private List<UserToken> getTargetTokens(Notification notification) {
        SubscriberType subscriberType = notification.getSubscriberType();

        return switch (subscriberType) {
            case ALL -> userTokenRepository.findAllActiveTokens();
            case CUSTOMER -> userTokenRepository.findAllActiveTokensByUserType(UserType.CUSTOMER);
            case OWNER -> userTokenRepository.findAllActiveTokensByUserType(UserType.OWNER);
            case SPECIFIC -> {
                if (notification.getSubscriberId() != null) {
                    // 먼저 CUSTOMER로 시도, 없으면 OWNER로 시도
                    var customerToken = userTokenRepository.findByUserIdAndUserType(
                        notification.getSubscriberId(), UserType.CUSTOMER);
                    if (customerToken.isPresent()) {
                        yield List.of(customerToken.get());
                    }

                    var ownerToken = userTokenRepository.findByUserIdAndUserType(
                        notification.getSubscriberId(), UserType.OWNER);
                    yield ownerToken.map(List::of).orElse(List.of());
                }
                yield List.of();
            }
        };
    }

    /**
     * 대상 설명 생성
     */
    private String getTargetDescription(NotificationRequest request) {
        SubscriberType subscriberType = request.getSubscriberType();
        return switch (subscriberType) {
            case SPECIFIC -> String.format("특정 사용자 (ID: %d)", request.getSubscriberId());
            default -> subscriberType.getDescription();
        };
    }
}