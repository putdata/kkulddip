package com.kkulddip.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review_helpful",
        uniqueConstraints = @UniqueConstraint(columnNames = {"review_id", "customer_id"}))
public class ReviewHelpful {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helpful_id")
    private Long helpfulId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Builder
    public ReviewHelpful(Review review, Long customerId) {
        this.review = review;
        this.customerId = customerId;
    }

    // 연관관계 편의 메서드
    public void setReview(Review review) {
        this.review = review;
    }
}