package com.kkulddip.order.infrastructure.integration.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkulddip.order.infrastructure.integration.dto.enums.NotificationType;
import com.kkulddip.order.infrastructure.integration.dto.enums.PublisherType;
import com.kkulddip.order.infrastructure.integration.dto.enums.SubscriberType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JsonProperty("publisher_id")
    private Long publisherId;

    @JsonProperty("publisher_type")
    private PublisherType publisherType;

    @JsonProperty("subscriber_id")
    private Long subscriberId;

    @JsonProperty("subscriber_type")
    @NotNull(message = "수신자 타입은 필수입니다.")
    private SubscriberType subscriberType;

    @JsonProperty("action_url")
    private String actionUrl;

    private NotificationType notificationType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Redis Z-Set score 계산용
    @JsonIgnore
    public long getScoreTimestamp() {
        return createdAt.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
    }
}