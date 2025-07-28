package com.kkulddip.domain.customer.entity;

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
 * 고객 엔티티
 */
@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private Integer level;

    @Column(name = "total_order")
    private Integer totalOrder;

    @Column(name = "total_money_saved")
    private Long totalMoneySaved;

    @Column(name = "total_co2_saved")
    private Double totalCo2Saved;

    @Column(name = "lasted_active_at")
    private LocalDateTime lastedActiveAt;

    @Column(name = "profile_image_url")
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
    public Customer(String email, String name, String address,
                   Double latitude, Double longitude, Integer level, Integer totalOrder,
                   Long totalMoneySaved, Double totalCo2Saved, LocalDateTime lastedActiveAt,
                   String profileImageUrl, OAuth2Provider oauth2Provider, String oauth2ProviderId) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
        this.totalOrder = totalOrder;
        this.totalMoneySaved = totalMoneySaved;
        this.totalCo2Saved = totalCo2Saved;
        this.lastedActiveAt = lastedActiveAt;
        this.profileImageUrl = profileImageUrl;
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