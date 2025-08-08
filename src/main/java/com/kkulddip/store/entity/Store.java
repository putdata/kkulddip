package com.kkulddip.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "store_name", length = 255, nullable = false)
    private String storeName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "operating_hours", length = 100)
    private String operatingHours;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "rating_average")
    private Double ratingAverage;

    @Column(name = "review_count")
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(name = "business_number", length = 100)
    private String businessNumber;

    @Column(name = "store_address", length = 100)
    private String storeAddress;

    @Column(name = "store_profile_image", length = 100)
    private String storeProfileImage;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Cursor 페이지네이션을 위한 생성일시와 수정일시 추가
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}