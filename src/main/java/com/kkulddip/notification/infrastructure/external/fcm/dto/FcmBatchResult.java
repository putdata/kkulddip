package com.kkulddip.notification.infrastructure.external.fcm.dto;

import com.google.firebase.messaging.BatchResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 배치 FCM 발송 결과
 *
 * @author Claude
 * @since 1.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmBatchResult {
    
    private final int successCount;
    private final int failureCount;
    private final List<FcmTokenResult> tokenResults;
    private final String errorMessage;

    public static FcmBatchResult fromBatchResponse(BatchResponse response, List<String> tokens) {
        List<FcmTokenResult> tokenResults = response.getResponses().stream()
            .map(sendResponse -> sendResponse.isSuccessful() ?
                FcmTokenResult.success(sendResponse.getMessageId()) :
                FcmTokenResult.failure(sendResponse.getException().getMessage()))
            .toList();

        return new FcmBatchResult(
            response.getSuccessCount(), 
            response.getFailureCount(), 
            tokenResults, 
            null
        );
    }

    public static FcmBatchResult failure(String errorMessage) {
        return new FcmBatchResult(0, 0, null, errorMessage);
    }

    public static FcmBatchResult empty() {
        return new FcmBatchResult(0, 0, null, "토큰 리스트가 비어있습니다.");
    }

    public boolean hasError() {
        return errorMessage != null;
    }
}