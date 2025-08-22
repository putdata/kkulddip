package com.kkulddip.review.service;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.request.ReviewReplyRequestDto;
import com.kkulddip.review.dto.response.ReviewReplyResponseDto;

public interface ReviewReplyService {

    ReviewReplyResponseDto createReviewReply(ReviewReplyRequestDto request, Long reviewId, JwtUserInfo userInfo);

    ReviewReplyResponseDto updateReviewReply(Long replyId, ReviewReplyRequestDto request, JwtUserInfo userInfo);

    void deleteReviewReply(Long replyId, JwtUserInfo userInfo);
}
