package com.kkulddip.notification.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogResponse {

    private Long notificationLogId;

    private Long notificationId;

    private Long recipientUserId;

    private RecipientType recipientType;

    private String fcmToken;

    private Boolean isSent;

    private Integer retryCount;

    private String errorMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}