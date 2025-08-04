package com.kkulddip.notification.infrastructure.persistence.jpa.entity;

import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.PublisherType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
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
@Table(name = "notification",
    indexes = {
        @Index(name = "idx_notification_created_time_desc", columnList = "created_at DESC"),
        @Index(name = "idx_notification_subscriber_time", columnList = "subscriber_id, subscriber_type, created_at DESC"),
        @Index(name = "idx_notification_subscriber_type_time", columnList = "subscriber_type, created_at DESC"),
    })
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "publisher_type")
    private PublisherType publisherType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscriber_type", nullable = false)
    private SubscriberType subscriberType;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}