package com.kkulddip.domain.userToken.dto.request;

import com.kkulddip.domain.userToken.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmTokenRequest(
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    String fcmToken,

    @NotNull(message = "디바이스 타입은 필수입니다.")
    DeviceType deviceType,

    String deviceId
) {
}