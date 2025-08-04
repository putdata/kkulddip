package com.kkulddip.notification.interfaces.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String status;
    private String message;
    private String title;
    private String target;
    private LocalDateTime sentAt;

    public static NotificationResponse success(String title, String target) {
        return NotificationResponse.builder()
            .status("success")
            .message("알림이 성공적으로 발송되었습니다.")
            .title(title)
            .target(target)
            .sentAt(LocalDateTime.now())
            .build();
    }

    public static NotificationResponse failure(String message) {
        return NotificationResponse.builder()
            .status("error")
            .message(message)
            .sentAt(LocalDateTime.now())
            .build();
    }
}