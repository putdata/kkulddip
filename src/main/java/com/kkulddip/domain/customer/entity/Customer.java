package com.kkulddip.domain.customer.entity;

import com.kkulddip.domain.user.entity.User;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import com.kkulddip.common.enums.OAuth2Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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

import static com.kkulddip.domain.customer.enums.CustomerLevel.SPROUT_BEE;

/**
 * 고객 엔티티
 */
@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false)
    private Long customerId;


    @Column
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column
    private CustomerLevel level = SPROUT_BEE;

    @Column(name = "total_order")
    private Integer totalOrder = 0;

    @Column(name = "total_money_saved")
    private Long totalMoneySaved = 0L;

    @Column(name = "total_co2_saved")
    private Double totalCo2Saved = 0.0;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;


    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Customer(String email, String name, String address,
                   Double latitude, Double longitude, CustomerLevel level, Integer totalOrder,
                   Long totalMoneySaved, Double totalCo2Saved, LocalDateTime lastActiveAt,
                   String profileImageUrl, OAuth2Provider oauth2Provider, String oauth2ProviderId) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.oauth2Provider = oauth2Provider;
        this.oauth2ProviderId = oauth2ProviderId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
        this.totalOrder = totalOrder;
        this.totalMoneySaved = totalMoneySaved;
        this.totalCo2Saved = totalCo2Saved;
        this.lastActiveAt = lastActiveAt;
    }

    @Override
    public Long getId() {
        return customerId;
    }
}