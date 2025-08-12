package com.kkulddip.notification.infrastructure.messaging.redis.processor.sender;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.infrastructure.external.fcm.FcmService;
import com.kkulddip.notification.infrastructure.external.fcm.dto.FcmResult;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 알림 발송 서비스
 *
 * <p>
 * 다양한 타입의 수신자에게 FCM 알림을 발송합니다.
 * </p>
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
    private final FavoriteRepository favoriteRepository;

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
     * @param notification  발송할 알림
     * @param userId        사용자 ID
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    public boolean sendToSpecificUser(
            NotificationResponse notification,
            Long userId,
            RecipientType recipientType) {

        try {
            // 입력 값 검증
            if (notification == null) {
                log.error("특정 사용자 알림 발송 실패: 알림 객체가 null입니다. - userId: {}, recipientType: {}",
                        userId, recipientType);
                return false;
            }

            log.debug("특정 사용자 알림 발송 시작 - notificationId: {}, userId: {}, recipientType: {}",
                    notification.getNotificationId(), userId, recipientType);

            if (userId == null || userId <= 0) {
                log.error("특정 사용자 알림 발송 실패: 유효하지 않은 userId입니다. - userId: {}, recipientType: {}, notificationId: {}",
                        userId, recipientType, notification.getNotificationId());
                return false;
            }

            if (recipientType == null) {
                log.error("특정 사용자 알림 발송 실패: recipientType이 null입니다. - userId: {}, notificationId: {}",
                        userId, notification.getNotificationId());
                return false;
            }

            UserRole userRole;
            try {
                userRole = convertToUserRole(recipientType);
            } catch (Exception e) {
                log.error(
                        "특정 사용자 알림 발송 실패: recipientType 변환 오류 - userId: {}, recipientType: {}, notificationId: {}, error: {}",
                        userId, recipientType, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            List<UserToken> userTokens;
            try {
                userTokens = userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);
            } catch (Exception e) {
                log.error(
                        "특정 사용자 알림 발송 실패: 사용자 토큰 조회 중 오류 발생 - userId: {}, userRole: {}, notificationId: {}, error: {}",
                        userId, userRole, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            if (userTokens.isEmpty()) {
                log.warn("특정 사용자 알림 발송 실패: 사용자의 활성 FCM 토큰을 찾을 수 없습니다. - userId: {}, userRole: {}, notificationId: {}",
                        userId, userRole, notification.getNotificationId());
                return false;
            }

            log.debug("사용자 토큰 조회 완료 - userId: {}, tokenCount: {}, notificationId: {}",
                    userId, userTokens.size(), notification.getNotificationId());

            return sendToUserTokens(notification, userTokens, recipientType);

        } catch (Exception e) {
            log.error("특정 사용자 알림 발송 중 예상치 못한 오류 발생 - userId: {}, recipientType: {}, notificationId: {}, error: {}",
                    userId, recipientType, notification != null ? notification.getNotificationId() : "unknown",
                    e.getMessage(), e);
            return false;
        }
    }

    /**
     * storeId에 해당하는 사장에게 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param storeId      가게 ID
     * @return 발송 성공 여부
     */
    public boolean sendToStoreOwner(NotificationResponse notification, Long storeId) {
        try {
            // 입력 값 검증
            if (notification == null) {
                log.error("가게 사장 알림 발송 실패: 알림 객체가 null입니다. - storeId: {}", storeId);
                return false;
            }

            log.info("가게 사장 알림 발송 시작 - notificationId: {}, storeId: {}",
                    notification.getNotificationId(), storeId);

            if (storeId == null || storeId <= 0) {
                log.error("가게 사장 알림 발송 실패: 유효하지 않은 storeId입니다. - storeId: {}, notificationId: {}",
                        storeId, notification.getNotificationId());
                return false;
            }

            // storeId로 ownerId 조회
            Optional<Long> ownerIdOpt;
            try {
                ownerIdOpt = storeRepository.findOwnerIdByStoreId(storeId);
            } catch (Exception e) {
                log.error("가게 사장 알림 발송 실패: 가게 소유자 조회 중 오류 발생 - storeId: {}, notificationId: {}, error: {}",
                        storeId, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            if (ownerIdOpt.isEmpty()) {
                log.warn("가게 사장 알림 발송 실패: storeId에 해당하는 활성 가게를 찾을 수 없습니다. - storeId: {}, notificationId: {}",
                        storeId, notification.getNotificationId());
                return false;
            }

            Long ownerId = ownerIdOpt.get();
            log.debug("가게 소유자 조회 완료 - storeId: {}, ownerId: {}, notificationId: {}",
                    storeId, ownerId, notification.getNotificationId());

            // ownerId에 해당하는 모든 활성 FCM 토큰 조회
            List<UserToken> ownerTokens;
            try {
                ownerTokens = userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(ownerId, UserRole.OWNER);
            } catch (Exception e) {
                log.error("가게 사장 알림 발송 실패: 사장 토큰 조회 중 오류 발생 - ownerId: {}, storeId: {}, notificationId: {}, error: {}",
                        ownerId, storeId, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            if (ownerTokens.isEmpty()) {
                log.warn("가게 사장 알림 발송 실패: 사장의 활성 FCM 토큰을 찾을 수 없습니다. - ownerId: {}, storeId: {}, notificationId: {}",
                        ownerId, storeId, notification.getNotificationId());
                return false;
            }

            log.debug("사장 토큰 조회 완료 - ownerId: {}, tokenCount: {}, storeId: {}, notificationId: {}",
                    ownerId, ownerTokens.size(), storeId, notification.getNotificationId());

            return sendToUserTokens(notification, ownerTokens, RecipientType.OWNER);

        } catch (Exception e) {
            log.error("가게 사장 알림 발송 중 예상치 못한 오류 발생 - storeId: {}, notificationId: {}, error: {}",
                    storeId, notification != null ? notification.getNotificationId() : "unknown", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 가게를 즐겨찾기한 모든 고객에게 브로드캐스트 알림을 발송합니다.
     *
     * @param notification 발송할 알림
     * @param storeId      가게 ID
     * @return 발송 성공 여부
     */
    public boolean sendBroadcastToFavoriteCustomers(NotificationResponse notification, Long storeId) {
        try {
            // 1. 입력 값 검증
            if (notification == null) {
                log.error("브로드캐스트 알림 발송 실패: 알림 객체가 null입니다. - storeId: {}", storeId);
                return false;
            }

            log.info("가게 즐겨찾기 고객 브로드캐스트 알림 발송 시작 - notificationId: {}, storeId: {}",
                    notification.getNotificationId(), storeId);

            if (storeId == null || storeId <= 0) {
                log.error("브로드캐스트 알림 발송 실패: 유효하지 않은 storeId입니다. - storeId: {}, notificationId: {}",
                        storeId, notification.getNotificationId());
                return false;
            }

            // 2. 가게 존재 여부 확인
            boolean storeExists;
            try {
                storeExists = storeRepository.existsById(storeId);
            } catch (Exception e) {
                log.error("브로드캐스트 알림 발송 실패: 가게 존재 여부 확인 중 오류 발생 - storeId: {}, notificationId: {}, error: {}",
                        storeId, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            if (!storeExists) {
                log.warn("브로드캐스트 알림 발송 실패: 존재하지 않는 가게입니다. - storeId: {}, notificationId: {}",
                        storeId, notification.getNotificationId());
                return false;
            }

            // 3. 해당 가게를 즐겨찾기한 모든 고객 ID 조회
            List<Long> customerIds;
            try {
                customerIds = favoriteRepository.findCustomerIdsByStoreId(storeId);
                log.debug("즐겨찾기 고객 조회 완료 - storeId: {}, customerCount: {}", storeId, customerIds.size());
            } catch (Exception e) {
                log.error("브로드캐스트 알림 발송 실패: 즐겨찾기 고객 조회 중 오류 발생 - storeId: {}, notificationId: {}, error: {}",
                        storeId, notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            if (customerIds.isEmpty()) {
                log.info("브로드캐스트 알림 발송 완료: 가게를 즐겨찾기한 고객이 없습니다. - storeId: {}, notificationId: {}",
                        storeId, notification.getNotificationId());
                return true; // 고객이 없는 것은 에러가 아님
            }

            log.info("브로드캐스트 알림 발송 진행 - storeId: {}, notificationId: {}, targetCustomers: {}",
                    storeId, notification.getNotificationId(), customerIds.size());

            // 4. 각 고객에게 알림 발송
            int successCount = 0;
            int failureCount = 0;
            int totalCount = customerIds.size();

            for (Long customerId : customerIds) {
                try {
                    boolean sent = sendToSpecificUser(notification, customerId, RecipientType.CUSTOMER);
                    if (sent) {
                        successCount++;
                        log.debug("고객 알림 발송 성공 - customerId: {}, storeId: {}, notificationId: {}",
                                customerId, storeId, notification.getNotificationId());
                    } else {
                        failureCount++;
                        log.warn("고객 알림 발송 실패 - customerId: {}, storeId: {}, notificationId: {}",
                                customerId, storeId, notification.getNotificationId());
                    }
                } catch (Exception e) {
                    failureCount++;
                    log.error("고객 알림 발송 중 예외 발생 - customerId: {}, storeId: {}, notificationId: {}, error: {}",
                            customerId, storeId, notification.getNotificationId(), e.getMessage(), e);
                }
            }

            // 5. 발송 결과 요약 로깅
            double successRate = totalCount > 0 ? (successCount * 100.0 / totalCount) : 0.0;
            log.info("브로드캐스트 알림 발송 완료 - storeId: {}, notificationId: {}, " +
                    "총 대상: {}, 성공: {}, 실패: {}, 성공률: {:.1f}%",
                    storeId, notification.getNotificationId(), totalCount, successCount, failureCount, successRate);

            // 6. 성공 여부 판단 (1개 이상 성공하면 성공으로 간주)
            boolean overallSuccess = successCount >= 1;

            if (!overallSuccess) {
                log.error("브로드캐스트 알림 발송 전체 실패 - storeId: {}, notificationId: {}, " +
                        "모든 고객에게 발송 실패", storeId, notification.getNotificationId());
            }

            return overallSuccess;

        } catch (Exception e) {
            log.error("브로드캐스트 알림 발송 중 예상치 못한 오류 발생 - storeId: {}, notificationId: {}, error: {}",
                    storeId, notification != null ? notification.getNotificationId() : "unknown", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 가게를 즐겨찾기한 고객들에게 브로드캐스트 알림인지 감지합니다.
     * 브로드캐스트 패턴: publisherType=STORE, subscriberType=CUSTOMER, subscriberId=NULL
     *
     * @param notification 확인할 알림
     * @return 가게 즐겨찾기 고객 브로드캐스트 알림 여부
     */
    public boolean isStoreFavoriteBroadcast(NotificationResponse notification) {
        if (notification == null) {
            log.debug("알림이 null입니다. 가게 즐겨찾기 고객 브로드캐스트 패턴이 아닙니다.");
            return false;
        }

        boolean isStoreBroadcast = PublisherType.STORE.equals(notification.getPublisherType()) &&
            SubscriberType.CUSTOMER.equals(notification.getSubscriberType()) &&
            notification.getSubscriberId() == null;

        log.debug("가게 즐겨찾기 고객 브로드캐스트 패턴 감지 - notificationId: {}, publisherType: {}, subscriberType: {}, " +
            "subscriberId: {}, isStoreFavoriteBroadcast: {}",
            notification.getNotificationId(),
            notification.getPublisherType(),
            notification.getSubscriberType(),
            notification.getSubscriberId(),
            isStoreBroadcast);

        return isStoreBroadcast;
    }

    /**
     * 사용자 토큰 리스트에 알림을 발송합니다.
     *
     * @param notification  발송할 알림
     * @param userTokens    사용자 토큰 리스트
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    private boolean sendToUserTokens(
            NotificationResponse notification,
            List<UserToken> userTokens,
            RecipientType recipientType) {

        try {
            if (userTokens == null) {
                log.error("사용자 토큰 리스트가 null입니다. - notificationId: {}, recipientType: {}",
                        notification.getNotificationId(), recipientType);
                return false;
            }

            if (userTokens.isEmpty()) {
                log.info("발송할 사용자 토큰이 없습니다. - notificationId: {}, recipientType: {}",
                        notification.getNotificationId(), recipientType);
                return true; // 토큰이 없는 것은 에러가 아님
            }

            log.debug("사용자 토큰 알림 발송 시작 - notificationId: {}, recipientType: {}, tokenCount: {}",
                    notification.getNotificationId(), recipientType, userTokens.size());

            int successCount = 0;
            int failureCount = 0;
            int totalCount = userTokens.size();

            for (UserToken userToken : userTokens) {
                try {
                    if (userToken == null) {
                        log.warn("null 사용자 토큰 발견, 건너뜀 - notificationId: {}, recipientType: {}",
                                notification.getNotificationId(), recipientType);
                        failureCount++;
                        continue;
                    }

                    boolean sent = sendToSingleUser(notification, userToken, recipientType);
                    if (sent) {
                        successCount++;
                    } else {
                        failureCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    log.error("사용자 토큰 알림 발송 중 예외 발생 - notificationId: {}, recipientType: {}, " +
                            "userId: {}, error: {}",
                            notification.getNotificationId(), recipientType,
                            userToken != null ? userToken.getUserId() : "unknown", e.getMessage(), e);
                }
            }

            // 발송 결과 요약 로깅
            double successRate = totalCount > 0 ? (successCount * 100.0 / totalCount) : 0.0;
            log.info("사용자 토큰 알림 발송 완료 - notificationId: {}, recipientType: {}, " +
                    "총 토큰: {}, 성공: {}, 실패: {}, 성공률: {:.1f}%",
                    notification.getNotificationId(), recipientType, totalCount, successCount, failureCount,
                    successRate);

            return successCount >= 1;

        } catch (Exception e) {
            log.error("사용자 토큰 알림 발송 중 예상치 못한 오류 발생 - notificationId: {}, recipientType: {}, error: {}",
                    notification.getNotificationId(), recipientType, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 단일 사용자에게 알림을 발송합니다.
     *
     * @param notification  발송할 알림
     * @param userToken     사용자 토큰
     * @param recipientType 수신자 타입
     * @return 발송 성공 여부
     */
    private boolean sendToSingleUser(
            NotificationResponse notification,
            UserToken userToken,
            RecipientType recipientType) {

        Long logId = null;

        try {
            // 입력 값 검증
            if (notification == null) {
                log.error("단일 사용자 알림 발송 실패: 알림 객체가 null입니다. - userId: {}",
                        userToken != null ? userToken.getUserId() : "unknown");
                return false;
            }

            if (userToken == null) {
                log.error("단일 사용자 알림 발송 실패: 사용자 토큰이 null입니다. - notificationId: {}",
                        notification.getNotificationId());
                return false;
            }

            if (userToken.getFcmToken() == null || userToken.getFcmToken().trim().isEmpty()) {
                log.warn("단일 사용자 알림 발송 실패: FCM 토큰이 비어있습니다. - userId: {}, notificationId: {}",
                        userToken.getUserId(), notification.getNotificationId());
                return false;
            }

            log.debug("단일 사용자 알림 발송 시작 - userId: {}, notificationId: {}, token: {}",
                    userToken.getUserId(), notification.getNotificationId(), maskToken(userToken.getFcmToken()));

            // 알림 로그 생성
            try {
                var logResponse = notificationService.createNotificationLog(
                        notification.getNotificationId(),
                        userToken.getUserId(),
                        recipientType,
                        userToken.getFcmToken());
                logId = logResponse.getNotificationLogId();
                log.debug("알림 로그 생성 완료 - logId: {}, userId: {}, notificationId: {}",
                        logId, userToken.getUserId(), notification.getNotificationId());
            } catch (Exception e) {
                log.error("알림 로그 생성 실패 - userId: {}, notificationId: {}, error: {}",
                        userToken.getUserId(), notification.getNotificationId(), e.getMessage(), e);
                return false;
            }

            // FCM 발송
            FcmResult result;
            try {
                result = fcmService.sendNotification(
                        userToken.getFcmToken(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getActionUrl());
            } catch (Exception e) {
                log.error("FCM 서비스 호출 실패 - userId: {}, notificationId: {}, error: {}",
                        userToken.getUserId(), notification.getNotificationId(), e.getMessage(), e);

                // FCM 서비스 호출 실패 시 로그 업데이트
                try {
                    if (logId != null) {
                        notificationService.markLogAsFailed(logId, "FCM 서비스 호출 실패: " + e.getMessage());
                    }
                } catch (Exception logUpdateException) {
                    log.error("알림 로그 업데이트 실패 - logId: {}, error: {}",
                            logId, logUpdateException.getMessage(), logUpdateException);
                }
                return false;
            }

            if (result == null) {
                log.error("FCM 결과가 null입니다. - userId: {}, notificationId: {}",
                        userToken.getUserId(), notification.getNotificationId());

                try {
                    if (logId != null) {
                        notificationService.markLogAsFailed(logId, "FCM 결과가 null");
                    }
                } catch (Exception logUpdateException) {
                    log.error("알림 로그 업데이트 실패 - logId: {}, error: {}",
                            logId, logUpdateException.getMessage(), logUpdateException);
                }
                return false;
            }

            if (result.isSuccess()) {
                // 발송 성공 시 로그 업데이트
                try {
                    notificationService.markLogAsSent(logId);
                    log.debug("FCM 발송 성공 - userId: {}, notificationId: {}, token: {}",
                            userToken.getUserId(), notification.getNotificationId(),
                            maskToken(userToken.getFcmToken()));
                    return true;
                } catch (Exception e) {
                    log.error("성공 로그 업데이트 실패 - logId: {}, userId: {}, error: {}",
                            logId, userToken.getUserId(), e.getMessage(), e);
                    // FCM 발송은 성공했으므로 true 반환
                    return true;
                }
            } else {
                // 발송 실패 시 로그 업데이트
                String errorMessage = result.getErrorMessage() != null ? result.getErrorMessage() : "알 수 없는 FCM 오류";
                try {
                    notificationService.markLogAsFailed(logId, errorMessage);
                } catch (Exception e) {
                    log.error("실패 로그 업데이트 실패 - logId: {}, userId: {}, error: {}",
                            logId, userToken.getUserId(), e.getMessage(), e);
                }

                log.warn("FCM 발송 실패 - userId: {}, notificationId: {}, error: {}",
                        userToken.getUserId(), notification.getNotificationId(), errorMessage);
                return false;
            }

        } catch (Exception e) {
            // 예상치 못한 예외 발생 시 로그 업데이트
            try {
                if (logId != null) {
                    notificationService.markLogAsFailed(logId, "예상치 못한 오류: " + e.getMessage());
                }
            } catch (Exception logUpdateException) {
                log.error("예외 상황 로그 업데이트 실패 - logId: {}, error: {}",
                        logId, logUpdateException.getMessage(), logUpdateException);
            }

            log.error("단일 사용자 알림 발송 중 예상치 못한 오류 발생 - userId: {}, notificationId: {}, error: {}",
                    userToken != null ? userToken.getUserId() : "unknown",
                    notification != null ? notification.getNotificationId() : "unknown",
                    e.getMessage(), e);
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