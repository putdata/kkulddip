package com.kkulddip.domain.userToken.service;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * FCM 토큰 관리 서비스
 * 사용자의 FCM 토큰 등록, 업데이트, 비활성화 등의 비즈니스 로직을 처리합니다.
 *
 * @author 이석규
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    /**
     * FCM 토큰을 등록하거나 업데이트합니다.
     * 동일한 사용자의 동일한 디바이스 타입 토큰이 있는 경우 업데이트하고,
     * 없는 경우 새로 등록합니다.
     *
     * @param userId 사용자 ID
     * @param userRole 사용자 역할
     * @param request FCM 토큰 등록 요청
     */
    @Transactional
    public void registerOrUpdateToken(Long userId, UserRole userRole, FcmTokenRequest request) {
        log.info("FCM 토큰 등록/업데이트 요청 - userId: {}, userRole: {}, deviceType: {}",
            userId, userRole, request.deviceType());

        // 기존 토큰 조회
        Optional<UserToken> existingToken = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(userId, userRole, request.deviceType());

        if (existingToken.isPresent()) {
            // 기존 토큰 업데이트
            UserToken token = existingToken.get();
            token.updateToken(request.fcmToken(), request.deviceId());
            log.info("기존 FCM 토큰 업데이트 완료 - tokenId: {}", token.getTokenId());
        } else {
            // 새 토큰 등록
            UserToken newToken = UserToken.builder()
                .userId(userId)
                .userType(userRole)
                .fcmToken(request.fcmToken())
                .deviceType(request.deviceType())
                .isActive(true)
                .build();

            userTokenRepository.save(newToken);
            log.info("새 FCM 토큰 등록 완료 - userId: {}, deviceType: {}", userId, request.deviceType());
        }
    }

    /**
     * 특정 사용자의 모든 FCM 토큰을 비활성화합니다.
     *
     * @param userId 사용자 ID
     * @param userRole 사용자 역할
     */
    @Transactional
    public void deactivateUserTokens(Long userId, UserRole userRole) {
        log.info("사용자 FCM 토큰 비활성화 요청 - userId: {}, userRole: {}", userId, userRole);

        List<UserToken> activeTokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);

        activeTokens.forEach(UserToken::deactivate);
        log.info("사용자 FCM 토큰 비활성화 완료 - userId: {}, 비활성화된 토큰 수: {}",
            userId, activeTokens.size());
    }

    /**
     * 특정 FCM 토큰을 삭제합니다.
     *
     * @param fcmToken 삭제할 FCM 토큰
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteToken(String fcmToken) {
        log.info("FCM 토큰 삭제 요청 - fcmToken: {}", fcmToken);

        Optional<UserToken> token = userTokenRepository.findByFcmToken(fcmToken);

        if (token.isPresent()) {
            userTokenRepository.delete(token.get());
            log.info("FCM 토큰 삭제 완료 - tokenId: {}", token.get().getTokenId());
            return true;
        } else {
            log.warn("삭제할 FCM 토큰을 찾을 수 없음 - fcmToken: {}", fcmToken);
            return false;
        }
    }

    /**
     * 특정 사용자 타입의 모든 활성 토큰을 조회합니다.
     *
     * @param userRole 사용자 역할
     * @return 활성 토큰 목록
     */
    public List<UserToken> getActiveTokensByUserType(UserRole userRole) {
        return userTokenRepository.findAllActiveTokensByUserType(userRole);
    }

    /**
     * 비활성 토큰들을 정리합니다.
     * 지정된 기간보다 오래된 비활성 토큰들을 삭제합니다.
     *
     * @param cutoffDate 기준 날짜
     * @return 삭제된 토큰 수
     */
    @Transactional
    public int cleanupInactiveTokens(LocalDateTime cutoffDate) {
        log.info("비활성 토큰 정리 시작 - 기준 날짜: {}", cutoffDate);

        List<UserToken> inactiveTokens = userTokenRepository.findInactiveTokens(cutoffDate);
        int deletedCount = inactiveTokens.size();

        userTokenRepository.deleteAll(inactiveTokens);
        log.info("비활성 토큰 정리 완료 - 삭제된 토큰 수: {}", deletedCount);

        return deletedCount;
    }
}