package com.kkulddip.domain.userToken.entity;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.enums.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.EntityListeners;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * 사용자 FCM 토큰 정보를 관리하는 엔티티
 *
 * <p>사용자별 푸시 알림 토큰과 디바이스 정보를 저장하며,
 * JPA Auditing을 통해 생성/수정 시간을 자동 관리합니다.</p>
 *
 * access = AccessLevel 설정 이유 : builder() 사용 유도 및 무분별한 외부 참조 방지
 *
 * @author 이석규
 * @since 1.0
 */

@Entity
@Table(name = "user_token",
    uniqueConstraints = @UniqueConstraint(columnNames = {"fcm_token"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserToken {

    /** 토큰 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_token_id")
    private Long tokenId;

    /** 토큰 소유자 사용자 ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 사용자 역할 (CUSTOMER, OWNER) */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserRole userType;

    /** Firebase Cloud Messaging 토큰 */
    @Column(name = "fcm_token", nullable = false, length = 1000)
    private String fcmToken;

    /** 디바이스 타입 (WEB, ANDROID, IOS) */
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    /** 토큰 활성화 상태 */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /** 토큰 생성 시간 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 토큰 정보 마지막 수정 시간 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 토큰 마지막 사용 시간 */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /**
     * FCM 토큰과 디바이스 정보를 업데이트하고 토큰을 활성화합니다.
     *
     * @param fcmToken 새로운 FCM 토큰
     */
    public void updateToken(String fcmToken) {
        this.fcmToken = fcmToken;
        this.lastUsedAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * 토큰을 비활성화합니다.
     * 비활성화된 토큰은 푸시 알림 발송 대상에서 제외됩니다.
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 토큰을 활성화하고 마지막 사용 시간을 업데이트합니다.
     */
    public void activate() {
        this.isActive = true;
        this.lastUsedAt = LocalDateTime.now();
    }
}