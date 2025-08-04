package com.kkulddip.notification.domain.model.entity;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.model.status.DeviceType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class UserToken {

    private final Long userTokenId;
    private final Long userId;
    private final UserType userType;
    private final String fcmToken;
    private final DeviceType deviceType;
    private final Boolean isActive;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * 토큰 활성 상태 확인
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * 토큰 업데이트
     */
    public UserToken updateToken(String newToken) {
        return this.toBuilder()
            .fcmToken(newToken)
            .isActive(true)
            .lastLoginAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 토큰 비활성화
     */
    public UserToken deactivate() {
        return this.toBuilder()
            .isActive(false)
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 토큰 문자열 유효성 검증
     */
    public boolean isValidToken() {
        return fcmToken != null &&
            fcmToken.length() >= 50 &&
            !fcmToken.trim().isEmpty();
    }
}