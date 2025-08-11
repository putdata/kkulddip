package com.kkulddip.notification.infrastructure.messaging.redis.processor.sender;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.infrastructure.external.fcm.FcmService;
import com.kkulddip.notification.infrastructure.external.fcm.dto.FcmResult;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 알림 발송 서비스
 *
 * <p>다양한 타입의 수신자에게 FCM 알림을 발송합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderService {

    private static final int MAX_RETRY_COUNT = 3;

    private final FcmService fcmService;
    private final UserTokenRepository userTokenRepository;
    private final NotificationService notificationService;
    private final StoreRepository storeRepository;

    /**
     * 모든 사용자에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @return 발송 성공 여부
     */
    public boolean sendToAll(NotificationResponse notification) {
        log.info("전체 사용자 알림 발송 시작 - notificationId: {}", notification.getNotificationId());
        
        boolean customerResult = sendToAllCustomers(notification);
        boolean ownerResult = sendToAllOwners(notification);
        
        return customerResult && ownerResult;
    }

    /**
     * 모든 고객에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @return 발송 성공 여부
     */
    public boolean sendToAllCustomers(NotificationResponse notification) {
        log.info("전체 고객 알림 발송 시작 - notificationId: {}", notification.getNotificationId());
        
        List<UserToken> customerTokens = userTokenRepository.findAllActiveTokensByUserType(UserRole.CUSTOMER);
        
        return sendToUserTokens(notification, customerTokens, RecipientType.CUSTOMER);
    }

    /**
     * 모든 사장에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @return 발송 성공 여부
     */
    public boolean sendToAllOwners(NotificationResponse notification) {
        log.info("전체 사장 알림 발송 시작 - notificationId: {}", notification.getNotificationId());
        
        List<UserToken> ownerTokens = userTokenRepository.findAllActiveTokensByUserType(UserRole.OWNER);
        
        return sendToUserTokens(notification, ownerTokens, RecipientType.OWNER);
    }

    /**
     * 특정 사용자에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param userId 사용자 ID
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    public boolean sendToSpecificUser(
        NotificationResponse notification, 
        Long userId, 
        RecipientType recipientType) {
        
        log.info("특정 사용자 알림 발송 시작 - notificationId: {}, userId: {}, recipientType: {}", 
            notification.getNotificationId(), userId, recipientType);

        UserRole userRole = convertToUserRole(recipientType);
        List<UserToken> userTokens = userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);

        if (userTokens.isEmpty()) {
            log.warn("사용자의 활성 FCM 토큰을 찾을 수 없습니다. - userId: {}, userRole: {}", 
                userId, userRole);
            return false;
        }

        return sendToUserTokens(notification, userTokens, recipientType);
    }

    /**
     * storeId에 해당하는 사장에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param storeId 가게 ID
     * @return 발송 성공 여부
     */
    public boolean sendToStoreOwner(NotificationResponse notification, Long storeId) {
        log.info("가게 사장 알림 발송 시작 - notificationId: {}, storeId: {}", 
            notification.getNotificationId(), storeId);

        // storeId로 ownerId 조회
        Optional<Long> ownerIdOpt = storeRepository.findOwnerIdByStoreId(storeId);
        if (ownerIdOpt.isEmpty()) {
            log.warn("storeId에 해당하는 활성 가게를 찾을 수 없습니다. - storeId: {}", storeId);
            return false;
        }

        Long ownerId = ownerIdOpt.get();
        log.debug("storeId {}에 해당하는 ownerId: {}", storeId, ownerId);

        // ownerId에 해당하는 모든 활성 FCM 토큰 조회
        List<UserToken> ownerTokens = userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(
            ownerId, UserRole.OWNER);

        if (ownerTokens.isEmpty()) {
            log.warn("사장의 활성 FCM 토큰을 찾을 수 없습니다. - ownerId: {}, storeId: {}", 
                ownerId, storeId);
            return false;
        }

        return sendToUserTokens(notification, ownerTokens, RecipientType.OWNER);
    }

    /**
     * 사용자 토큰 리스트에 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param userTokens 사용자 토큰 리스트
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    private boolean sendToUserTokens(
        NotificationResponse notification, 
        List<UserToken> userTokens, 
        RecipientType recipientType) {
        
        if (userTokens.isEmpty()) {
            log.warn("발송할 사용자 토큰이 없습니다. - notificationId: {}, recipientType: {}", 
                notification.getNotificationId(), recipientType);
            return true; // 토큰이 없는 것은 에러가 아님
        }

        int successCount = 0;
        int totalCount = userTokens.size();

        for (UserToken userToken : userTokens) {
            boolean sent = sendToSingleUser(notification, userToken, recipientType);
            if (sent) {
                successCount++;
            }
        }

        log.info("알림 발송 완료 - notificationId: {}, recipientType: {}, success: {}/{}", 
            notification.getNotificationId(), recipientType, successCount, totalCount);

        return successCount >= 1;
    }

    /**
     * 단일 사용자에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param userToken 사용자 토큰
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    private boolean sendToSingleUser(
        NotificationResponse notification, 
        UserToken userToken, 
        RecipientType recipientType) {
        
        Long logId = null;
        
        try {
            // 알림 로그 생성
            var logResponse = notificationService.createNotificationLog(
                notification.getNotificationId(),
                userToken.getUserId(),
                recipientType,
                userToken.getFcmToken()
            );
            logId = logResponse.getNotificationLogId();

            // FCM 발송
            FcmResult result = fcmService.sendNotification(
                userToken.getFcmToken(),
                notification.getTitle(),
                notification.getContent(),
                notification.getActionUrl()
            );

            if (result.isSuccess()) {
                // 발송 성공 시 로그 업데이트
                notificationService.markLogAsSent(logId);
                log.debug("FCM 발송 성공 - userId: {}, token: {}", 
                    userToken.getUserId(), maskToken(userToken.getFcmToken()));
                return true;
            } else {
                // 발송 실패 시 로그 업데이트
                notificationService.markLogAsFailed(logId, result.getErrorMessage());
                log.warn("FCM 발송 실패 - userId: {}, error: {}", 
                    userToken.getUserId(), result.getErrorMessage());
                return false;
            }

        } catch (Exception e) {
            // 예외 발생 시 로그 업데이트
            if (logId != null) {
                notificationService.markLogAsFailed(logId, e.getMessage());
            }
            log.error("알림 발송 중 오류 발생 - userId: {}, error: {}", 
                userToken.getUserId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * RecipientType을 UserRole로 변환합니다.
     *
     * @param recipientType 수신자 타입
     * @return 사용자 역할
     */
    private UserRole convertToUserRole(RecipientType recipientType) {
        return switch (recipientType) {
            case CUSTOMER -> UserRole.CUSTOMER;
            case OWNER -> UserRole.OWNER;
        };
    }

    /**
     * FCM 토큰을 마스킹합니다 (로깅용).
     *
     * @param token FCM 토큰
     * @return 마스킹된 토큰
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 5) + "***" + token.substring(token.length() - 5);
    }
}