package com.kkulddip.domain.userToken.service;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.enums.DeviceType;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmTokenService {

    private final UserTokenRepository userTokenRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    /**
     * FCM 토큰 등록 또는 업데이트
     */
    @Transactional
    public FcmTokenResponse registerOrUpdateToken(Long userId, UserRole userType, FcmTokenRequest request) {
        log.info("FCM 토큰 등록/업데이트 요청 - UserId: {}, UserType: {}, DeviceType: {}",
            userId, userType, request.deviceType());

        // 사용자 존재 여부 확인
        validateUserExists(userId, userType);

        // 기존 토큰 확인 (같은 사용자, 같은 디바이스 타입)
        Optional<UserToken> existingToken = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(userId, userType, request.deviceType());

        UserToken userToken;

        if (existingToken.isPresent()) {
            // 기존 토큰 업데이트
            userToken = existingToken.get();
            userToken.updateToken(request.fcmToken(), request.deviceId());
            log.info("기존 FCM 토큰 업데이트 완료 - TokenId: {}", userToken.getTokenId());
        } else {
            // 새 토큰 생성
            userToken = UserToken.builder()
                .userId(userId)
                .userType(userType)
                .fcmToken(request.fcmToken())
                .deviceType(request.deviceType())
                .lastUsedAt(LocalDateTime.now())
                .build();

            userToken = userTokenRepository.save(userToken);
            log.info("새 FCM 토큰 등록 완료 - TokenId: {}", userToken.getTokenId());
        }

        return FcmTokenResponse.builder()
            .tokenId(userToken.getTokenId())
            .fcmToken(userToken.getFcmToken())
            .deviceType(userToken.getDeviceType())
            .isActive(userToken.getIsActive())
            .registeredAt(userToken.getCreatedAt())
            .build();
    }

    /**
     * FCM 토큰 삭제 (비활성화)
     */
    @Transactional
    public void deactivateToken(Long userId, UserRole userType, DeviceType deviceType) {
        log.info("FCM 토큰 비활성화 요청 - UserId: {}, UserType: {}, DeviceType: {}",
            userId, userType, deviceType);

        UserToken userToken = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(userId, userType, deviceType)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND, "토큰을 찾을 수 없습니다."));

        userToken.deactivate();
        log.info("FCM 토큰 비활성화 완료 - TokenId: {}", userToken.getTokenId());
    }

    /**
     * 사용자의 모든 활성 토큰 조회
     */
    public List<FcmTokenResponse> getUserActiveTokens(Long userId, UserRole userType) {
        log.debug("사용자 활성 토큰 조회 - UserId: {}, UserType: {}", userId, userType);

        List<UserToken> tokens = userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userType);

        return tokens.stream()
            .map(token -> FcmTokenResponse.builder()
                .tokenId(token.getTokenId())
                .fcmToken(token.getFcmToken())
                .deviceType(token.getDeviceType())
                .isActive(token.getIsActive())
                .registeredAt(token.getCreatedAt())
                .build())
            .toList();
    }

    /**
     * 사용자 존재 여부 확인
     */
    private void validateUserExists(Long userId, UserRole userType) {
        boolean exists = switch (userType) {
            case CUSTOMER -> customerRepository.existsById(userId);
            case OWNER -> ownerRepository.existsById(userId);
            case ADMIN -> true; // Admin은 별도 검증 로직 필요 시 추가
        };

        if (!exists) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 비활성 토큰 정리 (스케줄러에서 사용)
     */
    @Transactional
    public void cleanupInactiveTokens(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<UserToken> inactiveTokens = userTokenRepository.findInactiveTokens(cutoffDate);

        if (!inactiveTokens.isEmpty()) {
            userTokenRepository.deleteAll(inactiveTokens);
            log.info("비활성 토큰 {}개 정리 완료", inactiveTokens.size());
        }
    }
}