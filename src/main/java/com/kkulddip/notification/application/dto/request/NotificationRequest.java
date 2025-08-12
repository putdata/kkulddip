package com.kkulddip.notification.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String id;

    @NotBlank(message = "알림 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "알림 내용은 필수입니다.")
    private String content;

    private Long publisherId;

    private PublisherType publisherType;

    private Long subscriberId;

    @NotNull(message = "수신자 타입은 필수입니다.")
    private SubscriberType subscriberType;

    private String actionUrl;

    private NotificationType notificationType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;

    // Redis Consumer에서 재시도 횟수 추적용
    @Builder.Default
    private Integer redisRetryCount = 0;

    // Redis Z-Set score 계산용
    @JsonIgnore
    public long getScoreTimestamp() {
        LocalDateTime targetTime = scheduledAt != null ? scheduledAt : 
            (createdAt != null ? createdAt : LocalDateTime.now());
        return targetTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Redis 재시도 횟수를 증가시킵니다.
     */
    public void incrementRedisRetryCount() {
        this.redisRetryCount++;
    }

    /**
     * Redis 재시도 가능 여부를 확인합니다.
     */
    public boolean canRedisRetry(int maxRetryCount) {
        return redisRetryCount < maxRetryCount;
    }
}