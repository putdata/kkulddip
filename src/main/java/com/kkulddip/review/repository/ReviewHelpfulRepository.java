package com.kkulddip.review.repository;

import com.kkulddip.review.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {
    Optional<ReviewHelpful> findByReviewReviewIdAndCustomerId(Long reviewId, Long currentUserId);

    boolean existsByReviewReviewIdAndCustomerId(Long reviewId, Long currentUserId);
}
