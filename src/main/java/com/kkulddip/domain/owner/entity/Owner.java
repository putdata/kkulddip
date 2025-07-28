package com.kkulddip.domain.owner.entity;

import com.kkulddip.common.entity.User;
import com.kkulddip.common.enums.OAuth2Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사장 엔티티
 */
@Entity
@Table(name = "owners")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String profileImageUrl;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider")
    private OAuth2Provider oauth2Provider;

    @Column(name = "oauth2_provider_id")
    private String oauth2ProviderId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Owner(String email, String name, String profileImageUrl, LocalDateTime lastActiveAt,
                OAuth2Provider oauth2Provider, String oauth2ProviderId) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.lastActiveAt = lastActiveAt;
        this.oauth2Provider = oauth2Provider;
        this.oauth2ProviderId = oauth2ProviderId;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}