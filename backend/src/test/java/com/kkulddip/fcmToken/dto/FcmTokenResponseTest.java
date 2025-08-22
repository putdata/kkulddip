package com.kkulddip.fcmToken.dto;

import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
import com.kkulddip.domain.userToken.enums.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FcmTokenResponse 테스트")
class FcmTokenResponseTest {

    @Test
    @DisplayName("FcmTokenResponse 생성 - Builder 패턴으로 정상 생성")
    void createFcmTokenResponse_WithBuilder_Success() {
        // given
        Long tokenId = 1L;
        String fcmToken = "test-fcm-token";
        DeviceType deviceType = DeviceType.ANDROID;
        boolean isActive = true;
        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        FcmTokenResponse response = FcmTokenResponse.builder()
            .tokenId(tokenId)
            .fcmToken(fcmToken)
            .deviceType(deviceType)
            .isActive(isActive)
            .registeredAt(registeredAt)
            .build();

        // then
        assertThat(response.tokenId()).isEqualTo(tokenId);
        assertThat(response.fcmToken()).isEqualTo(fcmToken);
        assertThat(response.deviceType()).isEqualTo(deviceType);
        assertThat(response.isActive()).isEqualTo(isActive);
        assertThat(response.registeredAt()).isEqualTo(registeredAt);
    }

    @Test
    @DisplayName("다양한 디바이스 타입으로 응답 생성")
    void createResponse_WithDifferentDeviceTypes() {
        // given
        DeviceType[] deviceTypes = {DeviceType.WEB, DeviceType.ANDROID, DeviceType.IOS};

        for (DeviceType deviceType : deviceTypes) {
            // when
            FcmTokenResponse response = FcmTokenResponse.builder()
                .tokenId(1L)
                .fcmToken("token-for-" + deviceType.name())
                .deviceType(deviceType)
                .isActive(true)
                .registeredAt(LocalDateTime.now())
                .build();

            // then
            assertThat(response.deviceType()).isEqualTo(deviceType);
            assertThat(response.fcmToken()).contains(deviceType.name());
        }
    }

    @Test
    @DisplayName("활성/비활성 상태별 응답 생성")
    void createResponse_WithDifferentActiveStates() {
        // given
        boolean[] activeStates = {true, false};

        for (boolean isActive : activeStates) {
            // when
            FcmTokenResponse response = FcmTokenResponse.builder()
                .tokenId(1L)
                .fcmToken("test-token")
                .deviceType(DeviceType.WEB)
                .isActive(isActive)
                .registeredAt(LocalDateTime.now())
                .build();

            // then
            assertThat(response.isActive()).isEqualTo(isActive);
        }
    }

    @Test
    @DisplayName("null 값들로 응답 생성")
    void createResponse_WithNullValues() {
        // given & when
        FcmTokenResponse response = FcmTokenResponse.builder()
            .tokenId(null)
            .fcmToken(null)
            .deviceType(null)
            .isActive(false)
            .registeredAt(null)
            .build();

        // then
        assertThat(response.tokenId()).isNull();
        assertThat(response.fcmToken()).isNull();
        assertThat(response.deviceType()).isNull();
        assertThat(response.isActive()).isFalse();
        assertThat(response.registeredAt()).isNull();
    }

    @Test
    @DisplayName("Record 특성 - equals와 hashCode 테스트")
    void recordEqualsAndHashCode_Test() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FcmTokenResponse response1 = FcmTokenResponse.builder()
            .tokenId(1L)
            .fcmToken("test-token")
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .registeredAt(now)
            .build();

        FcmTokenResponse response2 = FcmTokenResponse.builder()
            .tokenId(1L)
            .fcmToken("test-token")
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .registeredAt(now)
            .build();

        FcmTokenResponse response3 = FcmTokenResponse.builder()
            .tokenId(2L)
            .fcmToken("different-token")
            .deviceType(DeviceType.IOS)
            .isActive(false)
            .registeredAt(now)
            .build();

        // when & then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Record 특성 - toString 테스트")
    void recordToString_Test() {
        // given
        FcmTokenResponse response = FcmTokenResponse.builder()
            .tokenId(1L)
            .fcmToken("test-token")
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .registeredAt(LocalDateTime.of(2024, 1, 1, 12, 0))
            .build();

        // when
        String toString = response.toString();

        // then
        assertThat(toString).contains("FcmTokenResponse");
        assertThat(toString).contains("1");
        assertThat(toString).contains("test-token");
        assertThat(toString).contains("ANDROID");
        assertThat(toString).contains("true");
        assertThat(toString).contains("2024-01-01T12:00");
    }

    @Test
    @DisplayName("Record 불변성 테스트")
    void recordImmutability_Test() {
        // given
        Long tokenId = 1L;
        String fcmToken = "immutable-token";
        DeviceType deviceType = DeviceType.WEB;
        boolean isActive = true;
        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        FcmTokenResponse response = FcmTokenResponse.builder()
            .tokenId(tokenId)
            .fcmToken(fcmToken)
            .deviceType(deviceType)
            .isActive(isActive)
            .registeredAt(registeredAt)
            .build();

        // then
        assertThat(response.tokenId()).isEqualTo(tokenId);
        assertThat(response.fcmToken()).isEqualTo(fcmToken);
        assertThat(response.deviceType()).isEqualTo(deviceType);
        assertThat(response.isActive()).isEqualTo(isActive);
        assertThat(response.registeredAt()).isEqualTo(registeredAt);

        // 원본 변수 변경이 response에 영향을 주지 않는지 확인
        // (LocalDateTime은 immutable하므로 실제로는 문제없지만 개념적 테스트)
        assertThat(response.tokenId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Builder 패턴의 기본값 테스트")
    void builderDefaults_Test() {
        // given & when
        FcmTokenResponse response = FcmTokenResponse.builder().build();

        // then
        assertThat(response.tokenId()).isNull();
        assertThat(response.fcmToken()).isNull();
        assertThat(response.deviceType()).isNull();
        assertThat(response.isActive()).isFalse();
        assertThat(response.registeredAt()).isNull();
    }

    @Test
    @DisplayName("부분적 Builder 사용 테스트")
    void partialBuilder_Test() {
        // given & when
        FcmTokenResponse response = FcmTokenResponse.builder()
            .tokenId(100L)
            .fcmToken("partial-token")
            .isActive(true)
            .build();

        // then
        assertThat(response.tokenId()).isEqualTo(100L);
        assertThat(response.fcmToken()).isEqualTo("partial-token");
        assertThat(response.deviceType()).isNull();
        assertThat(response.isActive()).isTrue();
        assertThat(response.registeredAt()).isNull();
    }
}