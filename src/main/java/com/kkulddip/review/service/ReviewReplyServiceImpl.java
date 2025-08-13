package com.kkulddip.review.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.request.ReviewReplyRequestDto;
import com.kkulddip.review.dto.response.ReviewReplyResponseDto;
import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.ReviewReply;
import com.kkulddip.review.repository.ReviewReplyRepository;
import com.kkulddip.review.repository.ReviewRepository;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewReplyServiceImpl implements ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    // ================== 기본 메서드 ==================

    @Override
    @Transactional
    public ReviewReplyResponseDto createReviewReply(
        ReviewReplyRequestDto request,
        Long reviewId,
        JwtUserInfo userInfo
    ) {
        try {
            validateReplyRequest(request);

            Long currentOwnerId = Long.parseLong(userInfo.userId());

            validateStoreId(request.storeId(), currentOwnerId);

            Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."));

            validateDuplicateReply(reviewId);

            ReviewReply reviewReply = createReviewReplyEntity(request, review, currentOwnerId);
            ReviewReply savedReply = reviewReplyRepository.save(reviewReply);

            log.info("리뷰 답글 생성 성공 - replyId: {}", savedReply.getReplyId());  // ✅ 성공 로그 추가

            return createReviewReplyResponseDto(savedReply);

        } catch (BusinessException e) {
            log.error("리뷰 답글 생성 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 답글 생성 실패 - 시스템 에러. storeId: {}", request.storeId(), e);
            throw new BusinessException(ErrorCode.REVIEW_REPLY_CREATE_FAILED, "리뷰 답글 생성에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public ReviewReplyResponseDto updateReviewReply(
        Long replyId,
        ReviewReplyRequestDto request,
        JwtUserInfo userInfo
    ) {
        try {
            validateReplyId(replyId);
            validateReplyRequest(request);

            Long currentOwnerId = Long.parseLong(userInfo.userId());

            ReviewReply existingReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_REPLY_NOT_FOUND, "답글을 찾을 수 없습니다."));

            validateReplyOwnership(existingReply, currentOwnerId);
            validateReplyUpdatePermission(existingReply);

            existingReply.updateContent(request.content());

            log.info("리뷰 답글 수정 성공 - replyId: {}", replyId);  // ✅ 성공 로그 추가

            return createReviewReplyResponseDto(existingReply);

        } catch (BusinessException e) {
            log.error("리뷰 답글 수정 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 답글 수정 실패 - 시스템 에러. replyId: {}", replyId, e);
            throw new BusinessException(ErrorCode.REVIEW_REPLY_UPDATE_FAILED, "리뷰 답글 수정에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteReviewReply(Long replyId, JwtUserInfo userInfo) {
        try {
            validateReplyId(replyId);

            Long currentOwnerId = Long.parseLong(userInfo.userId());

            ReviewReply existingReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_REPLY_NOT_FOUND, "삭제할 답글을 찾을 수 없습니다."));

            validateReplyOwnership(existingReply, currentOwnerId);

            reviewReplyRepository.delete(existingReply);

            log.info("리뷰 답글 삭제 성공 - replyId: {}", replyId);  // ✅ 성공 로그 추가

        } catch (BusinessException e) {
            log.error("리뷰 답글 삭제 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 답글 삭제 실패 - 시스템 에러. replyId: {}", replyId, e);
            throw new BusinessException(ErrorCode.REVIEW_REPLY_DELETE_FAILED, "리뷰 답글 삭제에 실패했습니다.");
        }
    }

    // ================== 검증 메서드 ==================

    private void validateReplyId(Long replyId) {
        if (replyId == null || replyId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REPLY_ID, "유효하지 않은 답글 ID입니다.");
        }
    }

    private void validateStoreId(Long storeId, Long currentOwnerId) {
        if (storeId == null || storeId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_STORE_ID, "유효하지 않은 매장 ID입니다.");
        }
        if(!storeRepository.findStoreIdsByOwnerId(currentOwnerId).contains(storeId)){
            throw new BusinessException(ErrorCode.REPLY_UNAUTHORIZED_ACCESS, "본인의 매장에만 답글을 작성할 수 있습니다.");
        }
    }

    private void validateReplyRequest(ReviewReplyRequestDto request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "요청 데이터가 없습니다.");
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_REPLY_CONTENT, "답글 내용은 필수입니다.");
        }

        if (request.content().length() > 300) {
            throw new BusinessException(ErrorCode.REPLY_CONTENT_TOO_LONG, "답글 내용은 300자를 초과할 수 없습니다.");
        }
    }

    private void validateDuplicateReply(Long reviewId) {
        if (reviewReplyRepository.existsByReviewReviewId(reviewId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REPLY, "이미 해당 리뷰에 답글이 존재합니다.");
        }
    }

    private void validateReplyOwnership(ReviewReply reply, Long currentOwnerId) {
        if (!reply.getOwnerId().equals(currentOwnerId)) {
            throw new BusinessException(ErrorCode.REPLY_UNAUTHORIZED_ACCESS, "본인이 작성한 답글만 수정/삭제할 수 있습니다.");
        }
    }

    private void validateReplyUpdatePermission(ReviewReply reply) {
        if (reply.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = reply.getCreatedAt();
            LocalDateTime deadline = createdAt.plusHours(24);

            if (now.isAfter(deadline)) {
                throw new BusinessException(ErrorCode.REPLY_UPDATE_TIME_EXPIRED, "답글은 작성 후 24시간 내에만 수정 가능합니다.");
            }
        }
    }


    // ================== dto 생성 메서드 ==================

    private ReviewReply createReviewReplyEntity(
        ReviewReplyRequestDto request,
        Review review,
        Long ownerId
    ) {
        return ReviewReply.builder()
            .review(review)
            .ownerId(ownerId)
            .content(request.content())
            .build();
    }

    private ReviewReplyResponseDto createReviewReplyResponseDto(ReviewReply reply) {
        if (reply == null) {
            return null;
        }
        //userService.getUserName();
        return ReviewReplyResponseDto.builder()
            .replyId(reply.getReplyId())
            .ownerId(reply.getOwnerId())
            .content(reply.getContent())
            .createdAt(reply.getCreatedAt())
            .updatedAt(reply.getUpdatedAt())
            .build();
    }
}