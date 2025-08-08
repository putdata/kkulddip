package com.kkulddip.review.repository;

import com.kkulddip.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. 최신순 - NULL 체크로 첫 페이지와 커서 페이지 모두 처리
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        WHERE r.storeId = :storeId
           AND (:createdAt IS NULL OR 
                r.createdAt < :createdAt OR 
                (r.createdAt = :createdAt AND r.reviewId < :reviewId))
           AND (:withImage = false OR SIZE(r.images) > 0)
        ORDER BY r.createdAt DESC, r.reviewId DESC
        """)
    List<Review> findLatest(
        @Param("storeId") Long storeId,
        @Param("createdAt") LocalDateTime createdAt,  // NULL이면 첫 페이지
        @Param("reviewId") Long reviewId,              // NULL이면 첫 페이지
        @Param("withImage") boolean withImage,
        Pageable pageable
    );

    // 2. 오래된순
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        WHERE r.storeId = :storeId
           AND (:createdAt IS NULL OR 
                r.createdAt > :createdAt OR 
                (r.createdAt = :createdAt AND r.reviewId > :reviewId))
           AND (:withImage = false OR SIZE(r.images) > 0)
        ORDER BY r.createdAt ASC, r.reviewId ASC
        """)
    List<Review> findOldest(
        @Param("storeId") Long storeId,
        @Param("createdAt") LocalDateTime createdAt,
        @Param("reviewId") Long reviewId,
        @Param("withImage") boolean withImage,
        Pageable pageable
    );

    // 3. 평점 높은순
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        WHERE r.storeId = :storeId
           AND (:rating IS NULL OR 
                r.rating < :rating OR 
                (r.rating = :rating AND r.reviewId < :reviewId))
           AND (:withImage = false OR SIZE(r.images) > 0)
        ORDER BY r.rating DESC, r.reviewId DESC
        """)
    List<Review> findByRatingHigh(
        @Param("storeId") Long storeId,
        @Param("rating") Integer rating,  // NULL이면 첫 페이지
        @Param("reviewId") Long reviewId,
        @Param("withImage") boolean withImage,
        Pageable pageable
    );

    // 4. 평점 낮은순
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        WHERE r.storeId = :storeId
           AND (:rating IS NULL OR 
                r.rating > :rating OR 
                (r.rating = :rating AND r.reviewId > :reviewId))
           AND (:withImage = false OR SIZE(r.images) > 0)
        ORDER BY r.rating ASC, r.reviewId ASC
        """)
    List<Review> findByRatingLow(
        @Param("storeId") Long storeId,
        @Param("rating") Integer rating,
        @Param("reviewId") Long reviewId,
        @Param("withImage") boolean withImage,
        Pageable pageable
    );

    // 5. 좋아요 많은순
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        WHERE r.storeId = :storeId
           AND (:helpfulCount IS NULL OR 
                r.helpfulCount < :helpfulCount OR 
                (r.helpfulCount = :helpfulCount AND r.reviewId < :reviewId))
           AND (:withImage = false OR SIZE(r.images) > 0)
        ORDER BY r.helpfulCount DESC, r.reviewId DESC
        """)
    List<Review> findByHelpfulHigh(
        @Param("storeId") Long storeId,
        @Param("helpfulCount") Integer helpfulCount,  // NULL이면 첫 페이지
        @Param("reviewId") Long reviewId,
        @Param("withImage") boolean withImage,
        Pageable pageable
    );

    boolean existsByCustomerIdAndOrderId(Long customerId, Long orderId);

    @Query("""
    SELECT r FROM Review r 
    WHERE r.reviewId IN :reviewIds 
      AND (
        r.createdAt < :cursorDateTime 
        OR (r.createdAt = :cursorDateTime AND r.reviewId < :cursorReviewId)
      )
    ORDER BY r.createdAt DESC, r.reviewId DESC
    """)
    List<Review> findByReviewIdIn(
        @Param("reviewIds") List<Long> reviewIds,
        @Param("cursorDateTime") LocalDateTime cursorDateTime,
        @Param("cursorReviewId") Long cursorReviewId,
        Pageable pageable
    );

    // 고객이 작성한 리뷰 목록 - 최신순 (커서 기반)
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        LEFT JOIN FETCH r.reply rp
        WHERE r.customerId = :customerId
           AND (:createdAt IS NULL OR 
                r.createdAt < :createdAt OR 
                (r.createdAt = :createdAt AND r.reviewId < :reviewId))
        ORDER BY r.createdAt DESC, r.reviewId DESC
        """)
    List<Review> findByCustomerIdOrderByCreatedAtDesc(
        @Param("customerId") Long customerId,
        @Param("createdAt") LocalDateTime createdAt,  // NULL이면 첫 페이지
        @Param("reviewId") Long reviewId,              // NULL이면 첫 페이지
        Pageable pageable
    );

    // 특정 고객이 특정 가게에 작성한 리뷰 - 최신순 (커서 기반)
    @Query("""
        SELECT DISTINCT r FROM Review r
        LEFT JOIN FETCH r.images ri
        LEFT JOIN FETCH r.reply rp
        WHERE r.customerId = :customerId
           AND r.storeId = :storeId
           AND (:createdAt IS NULL OR 
                r.createdAt < :createdAt OR 
                (r.createdAt = :createdAt AND r.reviewId < :reviewId))
        ORDER BY r.createdAt DESC, r.reviewId DESC
        """)
    List<Review> findByCustomerIdAndStoreIdOrderByCreatedAtDesc(
        @Param("customerId") Long customerId,
        @Param("storeId") Long storeId,
        @Param("createdAt") LocalDateTime createdAt,  // NULL이면 첫 페이지
        @Param("reviewId") Long reviewId,              // NULL이면 첫 페이지
        Pageable pageable
    );


    // 원자적 증가
    @Modifying
    @Query("UPDATE Review r SET r.helpfulCount = r.helpfulCount + 1 WHERE r.reviewId = :reviewId")
    int incrementHelpfulCount(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.helpfulCount = r.helpfulCount - 1 WHERE r.reviewId = :reviewId AND r.helpfulCount > 0")
    int decrementHelpfulCount(@Param("reviewId") Long reviewId);

    @Query("SELECT rr.review.reviewId FROM ReviewReply rr WHERE rr.ownerId = :ownerId")
    List<Long> findReviewIdsByOwnerId(@Param("ownerId") Long ownerId);
}