package com.kkulddip.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 리뷰 이미지들과의 1:N 관계
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private List<ReviewImage> images = new ArrayList<>();

    // 사장님 답글과의 1:1 관계
    @OneToOne(mappedBy = "review", fetch = FetchType.LAZY)
    private ReviewReply reply;


    @Column(name = "helpful_count", nullable = false)
    private int helpfulCount;

    @Builder
    public Review(Long customerId, Long storeId, String content,
                  Long orderId, Integer rating, int helpfulCount) {
        this.customerId = customerId;
        this.storeId = storeId;
        this.content = content;
        this.orderId = orderId;
        this.rating = rating;
        this.helpfulCount = helpfulCount;
    }

    public void updateContent(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }

}