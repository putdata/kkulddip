package com.kkulddip.fcmToken.entity;

import com.kkulddip.domain.userToken.enums.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DeviceType 열거형 테스트")
class DeviceTypeTest {

    @Test
    @DisplayName("WEB 디바이스 타입 검증")
    void webDeviceType_ShouldHaveCorrectValues() {
        // given & when
        DeviceType deviceType = DeviceType.WEB;

        // then
        assertThat(deviceType.getDisplayName()).isEqualTo("웹");
        assertThat(deviceType.getPlatformCode()).isEqualTo("web");
    }

    @Test
    @DisplayName("ANDROID 디바이스 타입 검증")
    void androidDeviceType_ShouldHaveCorrectValues() {
        // given & when
        DeviceType deviceType = DeviceType.ANDROID;

        // then
        assertThat(deviceType.getDisplayName()).isEqualTo("안드로이드");
        assertThat(deviceType.getPlatformCode()).isEqualTo("android");
    }

    @Test
    @DisplayName("IOS 디바이스 타입 검증")
    void iosDeviceType_ShouldHaveCorrectValues() {
        // given & when
        DeviceType deviceType = DeviceType.IOS;

        // then
        assertThat(deviceType.getDisplayName()).isEqualTo("iOS");
        assertThat(deviceType.getPlatformCode()).isEqualTo("ios");
    }

    @Test
    @DisplayName("모든 디바이스 타입 값 검증")
    void allDeviceTypes_ShouldHaveValidValues() {
        // given
        DeviceType[] deviceTypes = DeviceType.values();

        // then
        assertThat(deviceTypes).hasSize(3);
        
        for (DeviceType deviceType : deviceTypes) {
            assertThat(deviceType.getDisplayName()).isNotBlank();
            assertThat(deviceType.getPlatformCode()).isNotBlank();
        }
    }

    @Test
    @DisplayName("디바이스 타입 순서 검증")
    void deviceTypesOrder_ShouldBeCorrect() {
        // given
        DeviceType[] deviceTypes = DeviceType.values();

        // then
        assertThat(deviceTypes[0]).isEqualTo(DeviceType.WEB);
        assertThat(deviceTypes[1]).isEqualTo(DeviceType.ANDROID);
        assertThat(deviceTypes[2]).isEqualTo(DeviceType.IOS);
    }

    @Test
    @DisplayName("valueOf로 디바이스 타입 조회")
    void valueOf_ShouldReturnCorrectDeviceType() {
        // given & when & then
        assertThat(DeviceType.valueOf("WEB")).isEqualTo(DeviceType.WEB);
        assertThat(DeviceType.valueOf("ANDROID")).isEqualTo(DeviceType.ANDROID);
        assertThat(DeviceType.valueOf("IOS")).isEqualTo(DeviceType.IOS);
    }
}