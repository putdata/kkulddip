package com.kkulddip.notification.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.PublisherType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    // Redis Z-Set score 계산용
    @JsonIgnore
    public long getScoreTimestamp() {
        if (createdAt == null) {
            return System.currentTimeMillis();
        }
        return createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}