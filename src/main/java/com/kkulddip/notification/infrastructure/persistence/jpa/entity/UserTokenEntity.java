package com.kkulddip.notification.infrastructure.persistence.jpa.entity;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.model.status.DeviceType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_token",
    indexes = {
        @Index(name = "idx_user_token_user_id", columnList = "user_id, user_type"),
        @Index(name = "idx_user_token_active", columnList = "is_active, user_type"),
        @Index(name = "idx_user_token_fcm", columnList = "fcm_token")
    })
public class UserTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_token_id")
    private Long userTokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "fcm_token", length = 255, nullable = false)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}