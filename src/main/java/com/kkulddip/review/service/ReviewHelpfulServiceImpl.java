package com.kkulddip.review.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.response.ReviewHelpfulCreateResponseDto;
import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.ReviewHelpful;
import com.kkulddip.review.repository.ReviewHelpfulRepository;
import com.kkulddip.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewHelpfulServiceImpl implements ReviewHelpfulService {

    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final ReviewRepository reviewRepository;

    // ================== 기본 메서드 ==================
    
    @Override
    @Transactional
    public ReviewHelpfulCreateResponseDto createReviewHelpful(
        Long reviewId,
        Authentication authentication
    ) {
        try {
            // 입력값 검증
            validateReviewId(reviewId);

            Long currentUserId = 1L;//getCurrentUserId(authentication);

            // 리뷰 존재 확인
            Review review = findReviewById(reviewId);

            // 좋아요 생성
            ReviewHelpful reviewHelpful = createReviewHelpfulEntity(review, currentUserId);
            reviewHelpfulRepository.save(reviewHelpful);

            int updated = reviewRepository.incrementHelpfulCount(reviewId);
            if (updated == 0) {
                throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "리뷰를 찾을 수 없습니다.");
            }

            boolean isHelpful = createReviewHelpfulStatusResponseDto(review, authentication);

            log.info("리뷰 좋아요 생성 완료. reviewId: {}, customerId: {}", reviewId, currentUserId);

            return ReviewHelpfulCreateResponseDto.builder()
                .reviewId(reviewId)
                .customerId(/*currentUserId*/1L)
                .isHelpful(isHelpful)
                .build();

        } catch (BusinessException e) {
            log.error("리뷰 좋아요 생성 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 좋아요 생성 실패 - 시스템 에러. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_HELPFUL_CREATE_FAILED, "리뷰 좋아요 생성에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public boolean deleteReviewHelpful(
        Long reviewId,
        Authentication authentication
    ) {
        try {
            // 입력값 검증
            validateReviewId(reviewId);

            // 현재 사용자 ID와 요청 userId 일치 확인
            Long currentUserId = 1L;//getCurrentUserId(authentication);

            // 리뷰 존재 확인
            Review review = findReviewById(reviewId);

            // 좋아요 존재 확인
            ReviewHelpful existingHelpful = reviewHelpfulRepository
                .findByReviewReviewIdAndCustomerId(reviewId, currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_HELPFUL_NOT_FOUND, "좋아요를 찾을 수 없습니다."));

            // 좋아요 삭제
            reviewHelpfulRepository.delete(existingHelpful);
            int updated = reviewRepository.decrementHelpfulCount(reviewId);
            if (updated == 0) {
                throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "리뷰를 찾을 수 없습니다.");
            }

            log.info("리뷰 좋아요 삭제 완료. reviewId: {}, customerId: {}", reviewId, currentUserId);
            return createReviewHelpfulStatusResponseDto(review, authentication);

        } catch (BusinessException e) {
            log.error("리뷰 좋아요 삭제 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 좋아요 삭제 실패 - 시스템 에러. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_HELPFUL_DELETE_FAILED, "리뷰 좋아요 삭제에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean createReviewHelpfulStatusResponseDto(
        Review review,
        Authentication authentication
    ) {
        if (review == null) {
            return false;
        }

        boolean isHelpful = isHelpfuldByCustomer(review.getReviewId(), authentication);

        return isHelpful;
    }

    public boolean isHelpfuldByCustomer(Long reviewId, Authentication authentication) {
        Long currentUserId = 1L;//getCurrentUserId(authentication);
        return reviewHelpfulRepository.existsByReviewReviewIdAndCustomerId(reviewId, currentUserId);
        /*try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return false; // 인증되지 않은 사용자는 좋아요 상태가 false
            }


        } catch (Exception e) {
            log.warn("좋아요 상태 확인 실패. reviewId: {}", reviewId, e);
            return false; // 에러 시 false 반환
        }*/
    }

    // ================== 검증 메서드 ==================

    private void validateReviewId(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REVIEW_ID, "유효하지 않은 리뷰 ID입니다.");
        }
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "리뷰를 찾을 수 없습니다."));
    }

    // ================== 유틸리티 메서드 ==================

    private Long getCurrentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtUserInfo userInfo) {
            //return userInfo.userId();
        }

        throw new BusinessException(ErrorCode.INVALID_AUTHENTICATION, "인증 정보를 찾을 수 없습니다.");
    }

    // ================== dto 생성 메서드 ==================

    private ReviewHelpful createReviewHelpfulEntity(Review review, Long customerId) {
        return ReviewHelpful.builder()
            .review(review)
            .customerId(customerId)
            .build();
    }
}