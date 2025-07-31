package com.kkulddip.domain.customer.entity;

import com.kkulddip.domain.user.entity.User;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.kkulddip.domain.customer.enums.CustomerLevel.SPROUT_BEE;

/**
 * 고객 엔티티
 */
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
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

    @Builder.Default
    @ColumnDefault("'SPROUT_BEE'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerLevel level = SPROUT_BEE;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "total_order", nullable = false)
    private Integer totalOrder = 0;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "total_money_saved", nullable = false)
    private Long totalMoneySaved = 0L;

    @Builder.Default
    @ColumnDefault("0.0")
    @Column(name = "total_co2_saved", nullable = false)
    private Double totalCo2Saved = 0.0;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public Long getId() {
        return customerId;
    }
}