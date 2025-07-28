package com.kkulddip.domain.owner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDateTime;

/**
 * 사장 엔티티
 */
@Entity
@Table(name = "owners")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String profileImageUrl;

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
    public Owner(String email, String name, String profileImageUrl, 
                OAuth2Provider oauth2Provider, String oauth2ProviderId) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.oauth2Provider = oauth2Provider;
        this.oauth2ProviderId = oauth2ProviderId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public enum OAuth2Provider {
        GOOGLE("google"),
        KAKAO("kakao"),
        NAVER("naver");

        private final String registrationId;

        OAuth2Provider(String registrationId) {
            this.registrationId = registrationId;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public static OAuth2Provider fromRegistrationId(String registrationId) {
            for (OAuth2Provider provider : values()) {
                if (provider.getRegistrationId().equals(registrationId)) {
                    return provider;
                }
            }
            throw new IllegalArgumentException("Unknown OAuth2 provider: " + registrationId);
        }
    }
}