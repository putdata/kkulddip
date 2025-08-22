package com.kkulddip.review.service;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.response.ReviewHelpfulCreateResponseDto;
import com.kkulddip.review.entity.Review;
import org.springframework.security.core.Authentication;

public interface ReviewHelpfulService {

    boolean deleteReviewHelpful(Long reviewId, JwtUserInfo userInfo);

    ReviewHelpfulCreateResponseDto createReviewHelpful(Long reviewId, JwtUserInfo userInfo);

    boolean createReviewHelpfulStatusResponseDto(Review review, JwtUserInfo userInfo);
}
