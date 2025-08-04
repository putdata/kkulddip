package com.kkulddip.notification.domain.model.entity;

import com.kkulddip.notification.domain.model.status.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class NotificationLog {

    private final Long notificationLogId;
    private final Long notificationId;
    private final Long recipientUserId;
    private final UserType recipientType;
    private final String fcmToken;
    private final Boolean isSent;
    private final LocalDateTime sentAt;
    private final String errorMessage;
    private final Integer retryCount;
    private final LocalDateTime createdAt;

    /**
     * 발송 성공 처리
     */
    public NotificationLog markAsSent() {
        return this.toBuilder()
            .isSent(true)
            .sentAt(LocalDateTime.now())
            .errorMessage(null)
            .build();
    }

    /**
     * 발송 실패 처리
     */
    public NotificationLog markAsFailed(String errorMessage) {
        return this.toBuilder()
            .isSent(false)
            .sentAt(null)
            .errorMessage(errorMessage)
            .retryCount((this.retryCount != null ? this.retryCount : 0) + 1)
            .build();
    }

    /**
     * 재시도 가능 여부 확인
     */
    public boolean canRetry(int maxRetryCount) {
        return !Boolean.TRUE.equals(isSent) &&
            (retryCount == null || retryCount < maxRetryCount);
    }
}