package com.kkulddip.notification.infrastructure.external.fcm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 단일 FCM 발송 결과
 *
 * @author Claude
 * @since 1.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmResult {
    
    private final boolean success;
    private final String message;
    private final String errorMessage;

    public static FcmResult success(String message) {
        return new FcmResult(true, message, null);
    }

    public static FcmResult failure(String errorMessage) {
        return new FcmResult(false, null, errorMessage);
    }
}