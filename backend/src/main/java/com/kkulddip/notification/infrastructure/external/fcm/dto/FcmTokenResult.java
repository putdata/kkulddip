package com.kkulddip.notification.infrastructure.external.fcm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 개별 토큰 FCM 발송 결과
 *
 * @author Claude
 * @since 1.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmTokenResult {
    
    private final boolean success;
    private final String message;
    private final String errorMessage;

    public static FcmTokenResult success(String message) {
        return new FcmTokenResult(true, message, null);
    }

    public static FcmTokenResult failure(String errorMessage) {
        return new FcmTokenResult(false, null, errorMessage);
    }
}