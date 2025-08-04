package com.kkulddip.notification.infrastructure.persistence.jpa.entity;

import com.kkulddip.notification.domain.model.status.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_log",
    indexes = {
        @Index(name = "idx_notification_log_notification_id", columnList = "notification_id"),
        @Index(name = "idx_notification_log_recipient", columnList = "recipient_user_id, recipient_type"),
        @Index(name = "idx_notification_log_created_date", columnList = "created_at")
    })
public class NotificationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_log_id")
    private Long notificationLogId;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false)
    private UserType recipientType;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "is_sent", nullable = false)
    private Boolean isSent;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (retryCount == null) {
            retryCount = 0;
        }
    }
}