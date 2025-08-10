package com.kkulddip.notification.presentation.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private boolean success;
    private String message;
    private String notificationId;
    private Long queueSize;
    private LocalDateTime timestamp;
    private String title;
    private String target;
    private LocalDateTime sentAt;

    public static NotificationResponse success(String title, String target) {
        return NotificationResponse.builder()
            .success(true)
            .message("알림이 성공적으로 발송되었습니다.")
            .title(title)
            .target(target)
            .sentAt(LocalDateTime.now())
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static NotificationResponse failure(String message) {
        return NotificationResponse.builder()
            .success(false)
            .message(message)
            .sentAt(LocalDateTime.now())
            .timestamp(LocalDateTime.now())
            .build();
    }
}