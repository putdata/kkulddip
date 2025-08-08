package com.kkulddip.review.repository;

import com.kkulddip.review.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    boolean existsByReviewReviewId(Long reviewId);
}