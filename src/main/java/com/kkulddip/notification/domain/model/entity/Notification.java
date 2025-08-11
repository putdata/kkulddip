package com.kkulddip.notification.domain.model.entity;

import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 알림 정보를 관리하는 엔티티
 *
 * <p>알림의 기본 정보와 발행자/구독자 정보를 저장하며,
 * JPA Auditing을 통해 생성 시간을 자동 관리합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Entity
@Table(name = "notification", indexes = {
    @Index(name = "idx_notification_created_time_desc", columnList = "created_at DESC"),
    @Index(name = "idx_notification_subscriber_time", columnList = "subscriber_id, subscriber_type, created_at DESC"),
    @Index(name = "idx_notification_subscriber_type_time", columnList = "subscriber_type, created_at DESC")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {

    /** 알림 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    /** 알림 제목 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 알림 내용 */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 발행자 ID */
    @Column(name = "publisher_id")
    private Long publisherId;

    /** 발행자 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "publisher_type")
    private PublisherType publisherType;

    /** 구독자 ID */
    @Column(name = "subscriber_id")
    private Long subscriberId;

    /** 구독자 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "subscriber_type", nullable = false)
    private SubscriberType subscriberType;

    /** 액션 URL */
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    /** 알림 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    /** 예약 발송 시간 */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /** 실제 발송 시간 */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /** 알림 생성 시간 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 알림을 발송 완료로 표시합니다.
     */
    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
    }

    /**
     * 알림이 발송되었는지 확인합니다.
     * 
     * @return 발송 여부
     */
    public boolean isSent() {
        return sentAt != null;
    }

    /**
     * 알림이 예약된 시간에 발송 가능한지 확인합니다.
     * 
     * @return 발송 가능 여부
     */
    public boolean isReadyToSend() {
        if (isSent()) {
            return false;
        }
        
        if (scheduledAt == null) {
            return true;
        }
        
        return LocalDateTime.now().isAfter(scheduledAt) || LocalDateTime.now().isEqual(scheduledAt);
    }
}