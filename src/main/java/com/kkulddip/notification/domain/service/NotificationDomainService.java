package com.kkulddip.notification.domain.service;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.model.valueobject.NotificationRecord;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.infrastructure.external.firebase.FirebaseMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class NotificationDomainService {

    private final NotificationLogRepository notificationLogRepository;
    private final FirebaseMessagingService firebaseMessagingService;

    @Value("${app.notification.mock-mode:false}")
    private boolean mockMode;

    /**
     * 생성자 - FirebaseMessagingService 인터페이스로만 주입
     * Mock/Real 구현체는 @ConditionalOnProperty로 자동 선택됨
     */
    public NotificationDomainService(
        NotificationLogRepository notificationLogRepository,
        FirebaseMessagingService firebaseMessagingService) {

        this.notificationLogRepository = notificationLogRepository;
        this.firebaseMessagingService = firebaseMessagingService;

        log.info("NotificationDomainService 초기화 - Firebase 서비스: {} (Mock 모드: {})",
            firebaseMessagingService.getClass().getSimpleName(), mockMode);
    }

    /**
     * 단일 사용자에게 알림 발송
     */
    public void sendToSingleUser(Notification notification, UserToken userToken) {
        log.info("단일 사용자 알림 발송: 사용자={}:{}", userToken.getUserType(), userToken.getUserId());

        try {
            boolean success = firebaseMessagingService.sendMessage(userToken.getFcmToken(), notification);
            saveNotificationRecord(notification, userToken, success, null);

            if (!success) {
                log.warn("사용자 {}:{} 알림 발송 실패", userToken.getUserType(), userToken.getUserId());
            }
        } catch (Exception e) {
            log.error("사용자 {}:{} 알림 발송 중 오류: {}",
                userToken.getUserType(), userToken.getUserId(), e.getMessage());
            saveNotificationRecord(notification, userToken, false, e.getMessage());
            throw e;
        }
    }

    /**
     * 다중 사용자에게 알림 발송
     */
    public void sendToMultipleUsers(Notification notification, List<UserToken> userTokens) {
        if (userTokens == null || userTokens.isEmpty()) {
            log.warn("발송할 사용자 토큰이 없습니다.");
            return;
        }
        log.info("다중 사용자 알림 발송: {}명", userTokens.size());

        int successCount = 0;
        int failureCount = 0;

        for (UserToken userToken : userTokens) {
            try {
                sendToSingleUser(notification, userToken);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("사용자 {}:{} 알림 발송 실패, 계속 진행: {}",
                    userToken.getUserType(), userToken.getUserId(), e.getMessage());
            }
        }

        log.info("다중 사용자 알림 발송 완료: 성공={}명, 실패={}명", successCount, failureCount);
    }

    /**
     * 알림 기록 저장
     */
    private void saveNotificationRecord(Notification notification, UserToken userToken, boolean success, String errorMessage) {
        try {
            NotificationRecord record = NotificationRecord.builder()
                .notificationId(notification.getNotificationId())
                .recipientUserId(userToken.getUserId())
                .recipientType(userToken.getUserType())
                .fcmToken(userToken.getFcmToken())
                .isSent(success)
                .sentAt(success ? LocalDateTime.now() : null)
                .errorMessage(errorMessage)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

            notificationLogRepository.save(record);
            log.debug("알림 기록 저장 완료: 사용자={}:{}, 성공={}",
                userToken.getUserType(), userToken.getUserId(), success);

        } catch (Exception e) {
            log.error("알림 기록 저장 실패: {}", e.getMessage(), e);
        }
    }
}