package com.kkulddip.fcmToken.entity;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.enums.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserToken 엔티티 테스트")
class UserTokenTest {

    @Test
    @DisplayName("UserToken 생성 - Builder 패턴으로 정상 생성")
    void createUserToken_WithBuilder_Success() {
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.CUSTOMER;
        String fcmToken = "test-fcm-token-12345";
        DeviceType deviceType = DeviceType.ANDROID;

        // when
        UserToken userToken = UserToken.builder()
            .userId(userId)
            .userType(userRole)
            .fcmToken(fcmToken)
            .deviceType(deviceType)
            .build();

        // then
        assertThat(userToken.getUserId()).isEqualTo(userId);
        assertThat(userToken.getUserType()).isEqualTo(userRole);
        assertThat(userToken.getFcmToken()).isEqualTo(fcmToken);
        assertThat(userToken.getDeviceType()).isEqualTo(deviceType);
        assertThat(userToken.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("토큰 업데이트 - 토큰과 마지막 사용 시간이 업데이트됨")
    void updateToken_ShouldUpdateTokenAndLastUsedAt() {
        // given
        UserToken userToken = createTestUserToken();
        String newFcmToken = "new-fcm-token-67890";
        LocalDateTime beforeUpdate = LocalDateTime.now().minusMinutes(1);

        // when
        userToken.updateToken(newFcmToken);

        // then
        assertThat(userToken.getFcmToken()).isEqualTo(newFcmToken);
        assertThat(userToken.getIsActive()).isTrue();
        assertThat(userToken.getLastUsedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("토큰 비활성화 - isActive가 false로 변경됨")
    void deactivate_ShouldSetIsActiveFalse() {
        // given
        UserToken userToken = createTestUserToken();

        // when
        userToken.deactivate();

        // then
        assertThat(userToken.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("토큰 활성화 - isActive가 true로 변경되고 lastUsedAt이 업데이트됨")
    void activate_ShouldSetIsActiveTrueAndUpdateLastUsedAt() {
        // given
        UserToken userToken = createTestUserToken();
        userToken.deactivate();
        LocalDateTime beforeActivate = LocalDateTime.now().minusMinutes(1);

        // when
        userToken.activate();

        // then
        assertThat(userToken.getIsActive()).isTrue();
        assertThat(userToken.getLastUsedAt()).isAfter(beforeActivate);
    }

    @Test
    @DisplayName("기본값 검증 - isActive는 기본적으로 true")
    void defaultValues_IsActiveDefaultTrue() {
        // given & when
        UserToken userToken = UserToken.builder()
            .userId(1L)
            .userType(UserRole.CUSTOMER)
            .fcmToken("test-token")
            .deviceType(DeviceType.WEB)
            .build();

        // then
        assertThat(userToken.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("다양한 디바이스 타입 테스트")
    void createUserToken_WithDifferentDeviceTypes() {
        // given
        DeviceType[] deviceTypes = {DeviceType.WEB, DeviceType.ANDROID, DeviceType.IOS};

        for (DeviceType deviceType : deviceTypes) {
            // when
            UserToken userToken = UserToken.builder()
                .userId(1L)
                .userType(UserRole.OWNER)
                .fcmToken("token-for-" + deviceType.name())
                .deviceType(deviceType)
                .build();

            // then
            assertThat(userToken.getDeviceType()).isEqualTo(deviceType);
        }
    }

    @Test
    @DisplayName("다양한 사용자 역할 테스트")
    void createUserToken_WithDifferentUserRoles() {
        // given
        UserRole[] userRoles = {UserRole.CUSTOMER, UserRole.OWNER, UserRole.ADMIN};

        for (UserRole userRole : userRoles) {
            // when
            UserToken userToken = UserToken.builder()
                .userId(1L)
                .userType(userRole)
                .fcmToken("token-for-" + userRole.name())
                .deviceType(DeviceType.ANDROID)
                .build();

            // then
            assertThat(userToken.getUserType()).isEqualTo(userRole);
        }
    }

    private UserToken createTestUserToken() {
        return UserToken.builder()
            .userId(1L)
            .userType(UserRole.CUSTOMER)
            .fcmToken("test-fcm-token")
            .deviceType(DeviceType.ANDROID)
            .build();
    }
}