package com.kkulddip.domain.userToken.service;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
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
 * FCM 토큰 관리 서비스 (단순화된 단일 서비스)
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FcmTokenService {

    private final UserTokenRepository userTokenRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    // === 쓰기 작업 ===

    @Transactional
    public FcmTokenResponse registerOrUpdateToken(Long userId, UserRole userRole, FcmTokenRequest request) {
        log.info("FCM 토큰 등록/업데이트 - userId: {}, deviceType: {}", userId, request.deviceType());

        validateUserExists(userId, userRole);

        // 기존 토큰 확인 (같은 사용자, 같은 디바이스 타입)
        Optional<UserToken> existingToken = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(userId, userRole, request.deviceType());

        UserToken userToken;
        if (existingToken.isPresent()) {
            // 기존 토큰 업데이트
            userToken = existingToken.get();
            userToken.updateToken(request.fcmToken());
        } else {
            // 새 토큰 생성
            userToken = UserToken.builder()
                .userId(userId)
                .userType(userRole)
                .fcmToken(request.fcmToken())
                .deviceType(request.deviceType())
                .build();
            userToken = userTokenRepository.save(userToken);
        }

        return toResponse(userToken);
    }

    @Transactional
    public void deactivateUserTokens(Long userId, UserRole userRole) {
        log.info("사용자 토큰 비활성화 - userId: {}", userId);

        List<UserToken> activeTokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);

        activeTokens.forEach(UserToken::deactivate);
    }

    @Transactional
    public boolean deleteToken(String fcmToken) {
        Optional<UserToken> token = userTokenRepository.findByFcmToken(fcmToken);
        if (token.isPresent()) {
            userTokenRepository.delete(token.get());
            return true;
        }
        return false;
    }

    @Transactional
    public int cleanupInactiveTokens(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<UserToken> inactiveTokens = userTokenRepository.findInactiveTokens(cutoffDate);

        if (!inactiveTokens.isEmpty()) {
            userTokenRepository.deleteAll(inactiveTokens);
        }
        return inactiveTokens.size();
    }

    // === 읽기 작업 ===

    @Transactional(readOnly = true)
    public List<FcmTokenResponse> getUserActiveTokens(Long userId, UserRole userRole) {
        List<UserToken> tokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);

        return tokens.stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserToken> getActiveTokensByUserType(UserRole userRole) {
        return userTokenRepository.findAllActiveTokensByUserType(userRole);
    }

    // === 헬퍼 메서드 ===

    private void validateUserExists(Long userId, UserRole userType) {
        boolean exists = switch (userType) {
            case CUSTOMER -> customerRepository.existsById(userId);
            case OWNER -> ownerRepository.existsById(userId);
            case ADMIN -> true;
        };

        if (!exists) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private FcmTokenResponse toResponse(UserToken token) {
        return FcmTokenResponse.builder()
            .tokenId(token.getTokenId())
            .fcmToken(token.getFcmToken())
            .deviceType(token.getDeviceType())
            .isActive(token.getIsActive())
            .registeredAt(token.getCreatedAt())
            .build();
    }
}