package com.kkulddip.notification.domain.model.entity;

import com.kkulddip.notification.domain.model.enums.RecipientType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 개별 사용자에 대한 알림 발송 로그를 관리하는 엔티티
 *
 * <p>각 사용자별 알림 발송 시도, 성공/실패, 재시도 정보를 저장하며,
 * JPA Auditing을 통해 생성 시간을 자동 관리합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Entity
@Table(name = "notification_log", indexes = {
    @Index(name = "idx_notification_log_notification_id", columnList = "notification_id"),
    @Index(name = "idx_notification_log_recipient", columnList = "recipient_user_id, recipient_type"),
    @Index(name = "idx_notification_log_created_date", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationLog {

    /** 알림 로그 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_log_id")
    private Long notificationLogId;

    /** 연관된 알림 ID */
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    /** 수신자 사용자 ID */
    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    /** 수신자 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false)
    private RecipientType recipientType;

    /** FCM 토큰 */
    @Column(name = "fcm_token")
    private String fcmToken;

    /** 발송 성공 여부 */
    @Builder.Default
    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;

    /** 재시도 횟수 */
    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /** 에러 메시지 */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** 실제 발송 시간 */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /** 로그 생성 시간 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 알림 발송 성공을 기록합니다.
     */
    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * 알림 발송 실패를 기록합니다.
     * 
     * @param errorMessage 에러 메시지
     */
    public void markAsFailed(String errorMessage) {
        this.isSent = false;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }

    /**
     * 재시도 가능 여부를 확인합니다.
     * 
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 가능 여부
     */
    public boolean canRetry(int maxRetryCount) {
        return !isSent && retryCount < maxRetryCount;
    }

    /**
     * FCM 토큰을 업데이트합니다.
     * 
     * @param fcmToken 새로운 FCM 토큰
     */
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}