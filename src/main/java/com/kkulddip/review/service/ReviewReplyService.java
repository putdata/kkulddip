package com.kkulddip.review.service;

import com.kkulddip.review.dto.request.ReviewReplyRequestDto;
import com.kkulddip.review.dto.response.ReviewReplyResponseDto;
import org.springframework.security.core.Authentication;

public interface ReviewReplyService {

    ReviewReplyResponseDto createReviewReply(ReviewReplyRequestDto request, Long reviewId, Authentication authentication);

    ReviewReplyResponseDto updateReviewReply(Long replyId, ReviewReplyRequestDto request, Authentication authentication);

    void deleteReviewReply(Long replyId, Authentication authentication);
}
