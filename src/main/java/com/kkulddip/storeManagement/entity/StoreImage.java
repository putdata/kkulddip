package com.kkulddip.storeManagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * 가게 이미지 엔티티 (Review 스타일로 단순화)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "store_image")
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_img_id")
    private Long storeImgId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_order", nullable = false)
    private Integer uploadOrder;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Builder
    public StoreImage(Long storeId, String imageUrl, String originalName, Long fileSize, Integer uploadOrder) {
        this.storeId = storeId;
        this.imageUrl = imageUrl;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.uploadOrder = uploadOrder;
    }
}