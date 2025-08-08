package com.kkulddip.review.service;

import com.kkulddip.review.dto.response.ReviewHelpfulCreateResponseDto;
import com.kkulddip.review.entity.Review;
import org.springframework.security.core.Authentication;

public interface ReviewHelpfulService {

    boolean deleteReviewHelpful(Long reviewId, Authentication authentication);

    ReviewHelpfulCreateResponseDto createReviewHelpful(Long reviewId, Authentication authentication);

    boolean createReviewHelpfulStatusResponseDto(Review review, Authentication authentication);
}
