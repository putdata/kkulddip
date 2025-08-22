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
 * 
 * 꿀딥(kkulddip) 서비스를 이용하는 고객의 정보를 관리합니다.
 * User 엔티티를 상속받아 기본 사용자 정보를 포함하며, 추가로 고객 특화 정보를 관리합니다.
 * 
 * 주요 기능:
 * - 고객 기본 정보 (주소, 좌표 정보)
 * - 고객 레벨 시스템 (SPROUT_BEE → WORKER_BEE → HONEY_BEE → QUEEN_BEE)
 * - 주문 및 환경 기여도 통계 (총 주문 수, 절약 금액, CO2 절약량)
 * - 자동 레벨업 시스템 (주문 수에 따른 레벨 승급)
 * - 마지막 활동 시간 추적
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
    
    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
    
    public void updateLocation(String address, Double latitude, Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public void updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now();
    }
    
    public void updateStats(Integer orderIncrement, Long moneySavedIncrement, Double co2SavedIncrement) {
        this.totalOrder += orderIncrement;
        this.totalMoneySaved += moneySavedIncrement;
        this.totalCo2Saved += co2SavedIncrement;
        updateCustomerLevel();
    }
    
    private void updateCustomerLevel() {
        if (this.totalOrder >= 50 && this.level != CustomerLevel.QUEEN_BEE) {
            this.level = CustomerLevel.QUEEN_BEE;
        } else if (this.totalOrder >= 30 && this.level != CustomerLevel.HONEY_BEE && this.level != CustomerLevel.QUEEN_BEE) {
            this.level = CustomerLevel.HONEY_BEE;
        } else if (this.totalOrder >= 10 && this.level == CustomerLevel.SPROUT_BEE) {
            this.level = CustomerLevel.WORKER_BEE;
        }
    }
}