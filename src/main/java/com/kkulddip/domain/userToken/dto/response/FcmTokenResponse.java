package com.kkulddip.domain.userToken.dto.response;

import com.kkulddip.domain.userToken.enums.DeviceType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record FcmTokenResponse(
    Long tokenId,
    String fcmToken,
    DeviceType deviceType,
    boolean isActive,
    LocalDateTime registeredAt
) {
}