package com.kkulddip.notification.domain.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.notification.domain.model.entity.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserTokenDomainService {

    /**
     * 토큰 유효성 검증
     */
    public void validateToken(UserToken userToken) {
        if (userToken == null) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "토큰 정보가 없습니다.");
        }

        if (userToken.getUserId() == null) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "사용자 ID는 필수입니다.");
        }

        if (!StringUtils.hasText(userToken.getFcmToken())) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "FCM 토큰은 필수입니다.");
        }

        if (!userToken.isValidToken()) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "유효하지 않은 FCM 토큰입니다.");
        }

        if (userToken.getDeviceType() == null) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "디바이스 타입은 필수입니다.");
        }

        if (userToken.getUserType() == null) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "사용자 타입은 필수입니다.");
        }
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(UserToken userToken) {
        return userToken.getLastLoginAt() != null &&
            userToken.getLastLoginAt().isBefore(LocalDateTime.now().minusMonths(6));
    }

    /**
     * 토큰 갱신 필요 여부 확인
     */
    public boolean needsTokenRefresh(UserToken userToken, String newToken) {
        return !userToken.getFcmToken().equals(newToken);
    }
}