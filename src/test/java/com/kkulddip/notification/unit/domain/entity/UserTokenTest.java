package com.kkulddip.notification.unit.domain.entity;

import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.model.status.DeviceType;
import com.kkulddip.notification.domain.model.status.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

@DisplayName("UserToken 도메인 엔티티 테스트")
class UserTokenTest {

    @Nested
    @DisplayName("토큰 생성 테스트")
    class CreateTokenTest {

        @Test
        @DisplayName("정상적인 토큰 생성")
        void createValidToken() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            String fcmToken = "test-fcm-token-" + System.currentTimeMillis();

            // When
            UserToken token = UserToken.builder()
                .userId(123L)
                .userType(UserType.CUSTOMER)
                .fcmToken(fcmToken)
                .deviceType(DeviceType.WEB)
                .isActive(true)
                .createdAt(now)
                .build();

            // Then
            assertThat(token.getUserId()).isEqualTo(123L);
            assertThat(token.getUserType()).isEqualTo(UserType.CUSTOMER);
            assertThat(token.getFcmToken()).isEqualTo(fcmToken);
            assertThat(token.getDeviceType()).isEqualTo(DeviceType.WEB);
            assertThat(token.isActive()).isTrue();
            assertThat(token.getCreatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("토큰 유효성 검증 테스트")
    class TokenValidationTest {

        @Test
        @DisplayName("유효한 FCM 토큰")
        void validFcmToken() {
            // Given
            String longToken = "a".repeat(100); // 100자 토큰
            UserToken token = createToken(longToken);

            // When & Then
            assertThat(token.isValidToken()).isTrue();
        }

        @Test
        @DisplayName("짧은 FCM 토큰은 유효하지 않음")
        void invalidShortFcmToken() {
            // Given
            String shortToken = "short"; // 5자 토큰
            UserToken token = createToken(shortToken);

            // When & Then
            assertThat(token.isValidToken()).isFalse();
        }

        @Test
        @DisplayName("null FCM 토큰은 유효하지 않음")
        void invalidNullFcmToken() {
            // Given
            UserToken token = createToken(null);

            // When & Then
            assertThat(token.isValidToken()).isFalse();
        }

        @Test
        @DisplayName("빈 FCM 토큰은 유효하지 않음")
        void invalidEmptyFcmToken() {
            // Given
            UserToken token = createToken("   ");

            // When & Then
            assertThat(token.isValidToken()).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 상태 변경 테스트")
    class TokenStateTest {

        @Test
        @DisplayName("토큰 업데이트")
        void updateToken() {
            // Given
            UserToken originalToken = createToken("old-token");
            String newToken = "new-token-" + System.currentTimeMillis();

            // When
            UserToken updatedToken = originalToken.updateToken(newToken);

            // Then
            assertThat(updatedToken.getFcmToken()).isEqualTo(newToken);
            assertThat(updatedToken.isActive()).isTrue();
            assertThat(updatedToken.getLastLoginAt()).isNotNull();
            assertThat(updatedToken.getUpdatedAt()).isNotNull();

            // 원본은 변경되지 않음 (불변성)
            assertThat(originalToken.getFcmToken()).isEqualTo("old-token");
        }

        @Test
        @DisplayName("토큰 비활성화")
        void deactivateToken() {
            // Given
            UserToken activeToken = UserToken.builder()
                .userId(123L)
                .userType(UserType.CUSTOMER)
                .fcmToken("test-token")
                .deviceType(DeviceType.WEB)
                .isActive(true)
                .build();

            // When
            UserToken deactivatedToken = activeToken.deactivate();

            // Then
            assertThat(deactivatedToken.isActive()).isFalse();
            assertThat(deactivatedToken.getUpdatedAt()).isNotNull();

            // 원본은 변경되지 않음 (불변성)
            assertThat(activeToken.isActive()).isTrue();
        }

        @Test
        @DisplayName("활성 상태 확인")
        void isActive() {
            // Given
            UserToken activeToken = createTokenWithActiveStatus(true);
            UserToken inactiveToken = createTokenWithActiveStatus(false);

            // When & Then
            assertThat(activeToken.isActive()).isTrue();
            assertThat(inactiveToken.isActive()).isFalse();
        }
    }

    // Helper methods
    private UserToken createToken(String fcmToken) {
        return UserToken.builder()
            .userId(123L)
            .userType(UserType.CUSTOMER)
            .fcmToken(fcmToken)
            .deviceType(DeviceType.WEB)
            .isActive(true)
            .build();
    }

    private UserToken createTokenWithActiveStatus(Boolean isActive) {
        return UserToken.builder()
            .userId(123L)
            .userType(UserType.CUSTOMER)
            .fcmToken("test-token")
            .deviceType(DeviceType.WEB)
            .isActive(isActive)
            .build();
    }
}