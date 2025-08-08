package com.kkulddip.review.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.common.CursorUtil;
import com.kkulddip.review.dto.request.ReviewCreateRequestDto;
import com.kkulddip.review.dto.request.ReviewUpdateRequestDto;
import com.kkulddip.review.dto.response.ReviewImageResponseDto;
import com.kkulddip.review.dto.response.ReviewListResponseDto;
import com.kkulddip.review.dto.response.ReviewOneResponseDto;
import com.kkulddip.review.dto.response.ReviewReplyResponseDto;
import com.kkulddip.review.dto.response.ReviewResponseDto;
import com.kkulddip.review.dto.response.ReviewWithHelpfulStatusResponseDto;
import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.ReviewReply;
import com.kkulddip.review.entity.enums.ReviewSortType;
import com.kkulddip.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewImageService reviewImageService;
    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulService reviewHelpfulService;
    private final ReviewReplyService reviewReplyService;
    private final CursorUtil cursorUtil;
    private final int size10 = 10;
    private final int size1 = 1;
    private final Pageable pageable10 = PageRequest.of(0, size10+1);
    private final Pageable pageable1 = PageRequest.of(0, size1+1);

    // ================== 리뷰 기본 메서드 ==================

    @Override
    @Transactional
    public ReviewResponseDto createReview(
        Long storeId,
        ReviewCreateRequestDto request,
        List<MultipartFile> images,
        Authentication authentication) {

        try {
            // 입력값 검증
            validateStoreId(storeId);
            validateRequest(request);
            validateContentAndRating(request.content(), request.rating());

            // 중복 리뷰 검증 (같은 주문에 대한 리뷰)
            validateDuplicateReview(request.customerId(), request.orderId());

            Review review = createReviewEntity(storeId, request);
            Review savedReview = reviewRepository.save(review);

            // 이미지 저장
            if (images != null && !images.isEmpty()) {
                reviewImageService.addImage(savedReview.getReviewId(), images, 0);
            }

            // 다시 조회해서 이미지 정보 포함
            Review reviewWithImages = reviewRepository.findById(savedReview.getReviewId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "저장된 리뷰를 찾을 수 없습니다."));

            List<ReviewImageResponseDto> imageList = reviewImageService.getImageDtoList(reviewWithImages.getImages());
            ReviewReplyResponseDto replyDto = getReplyDto(reviewWithImages.getReply());

            log.info("리뷰 생성 완료. reviewId: {}, customerId: {}", savedReview.getReviewId(), request.customerId());
            return createReviewResponseDto(reviewWithImages, imageList, replyDto, authentication);

        } catch (BusinessException e) {
            log.error("리뷰 생성 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 생성 실패 - 시스템 에러", e);
            throw new BusinessException(ErrorCode.REVIEW_CREATE_FAILED, "리뷰 생성에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(
        Long reviewId,
        ReviewUpdateRequestDto request,
        List<MultipartFile> newImages,
        Authentication authentication) {

        try {
            // 입력값 검증
            validateReviewId(reviewId);
            validateRequest(request);
            validateContentAndRating(request.content(), request.rating());

            // 기존 리뷰 조회
            Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "리뷰를 찾을 수 없습니다."));

            // 권한 검증
            validateReviewWriter(existingReview, authentication);
            validateUpdatePermission(existingReview);

            // 리뷰 내용 업데이트
            existingReview.updateContent(request.content(), request.rating());

            // 새 이미지 추가
            if (newImages != null && !newImages.isEmpty()) {
                reviewImageService.addImage(reviewId, newImages, request.deleteImageIds().size());
            }

            // 기존 이미지 삭제
            if (request.deleteImageIds() != null && !request.deleteImageIds().isEmpty()) {
                reviewImageService.deleteImages(reviewId, request.deleteImageIds());
            }

            // 업데이트된 리뷰 조회
            Review updatedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "업데이트된 리뷰를 찾을 수 없습니다."));

            List<ReviewImageResponseDto> imageList = reviewImageService.getImageDtoList(updatedReview.getImages());
            ReviewReplyResponseDto replyDto = getReplyDto(updatedReview.getReply());

            log.info("리뷰 수정 완료. reviewId: {}", reviewId);
            return createReviewResponseDto(updatedReview, imageList, replyDto, authentication);

        } catch (BusinessException e) {
            log.error("리뷰 수정 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 수정 실패 - 시스템 에러. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_UPDATE_FAILED, "리뷰 수정에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewListResponseDto getReviewListByStoreId(
        Long storeId,
        boolean withImage,
        ReviewSortType sortType,
        String cursor,
        Authentication authentication
    ) {
        try {
            validateStoreId(storeId);
            validateSortType(sortType);

            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, sortType);

            //리뷰 리스트 받아오기
            List<Review> reviews = fetchReviews(cursorData, sortType, storeId, size10+1, withImage);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size10;
            if (hasNext) {
                reviews = reviews.subList(0, size10);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, sortType);
            }

            log.info("매장 리뷰 목록 조회 완료. storeId: {}, sortType: {}, cursor: {}", storeId, sortType, nextCursor);
            return createReviewListResponseDto(reviews, nextCursor, hasNext, authentication);

        } catch (BusinessException e) {
            log.error("매장 리뷰 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("매장 리뷰 목록 조회 실패. storeId: {}", storeId, e);
            throw new BusinessException(ErrorCode.REVIEW_LIST_FETCH_FAILED, "리뷰 목록 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewListResponseDto getMyReviewList(
        String cursor,
        ReviewSortType sortType,
        Authentication authentication
    ) {
        try {
            Long currentUserId = 1L;//getCurrentUserId(authentication);
            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, sortType);
            // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
            CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);

            List<Review> reviews = reviewRepository
                .findByCustomerIdOrderByCreatedAtDesc(currentUserId, params.getDateTime(), params.getReviewId(), pageable10);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size10;
            if (hasNext) {
                reviews = reviews.subList(0, size10);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, ReviewSortType.LATEST);
            }
            log.info("내 리뷰 목록 조회 완료. customerId: {}, cursor: {}", currentUserId, nextCursor);
            return createReviewListResponseDto(reviews, nextCursor, hasNext, authentication);

        } catch (BusinessException e) {
            log.error("내 리뷰 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("내 리뷰 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.REVIEW_LIST_FETCH_FAILED, "내 리뷰 목록 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewListResponseDto getMyReviewListByStoreId(
        String cursor,
        ReviewSortType sortType,
        Long storeId,
        Authentication authentication
    ) {
        try {
            validateStoreId(storeId);
            Long currentUserId = 1L;//getCurrentUserId(authentication);

            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, sortType);
            // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
            CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);

            List<Review> reviews = reviewRepository
                .findByCustomerIdAndStoreIdOrderByCreatedAtDesc(currentUserId, storeId, params.getDateTime(), params.getReviewId(), pageable10);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size10;
            if (hasNext) {
                reviews = reviews.subList(0, size10);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, sortType);
            }

            log.info("매장별 내 리뷰 목록 조회 완료. customerId: {}, storeId: {}, cursor: {}", currentUserId, storeId, nextCursor);
            return createReviewListResponseDto(reviews, nextCursor, hasNext, authentication);

        } catch (BusinessException e) {
            log.error("매장별 내 리뷰 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("매장별 내 리뷰 목록 조회 실패. storeId: {}", storeId, e);
            throw new BusinessException(ErrorCode.REVIEW_LIST_FETCH_FAILED, "매장별 내 리뷰 목록 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewListResponseDto getMyReplyReviewList(String cursor, Authentication authentication) {
        try {
            Long currentOwnerId = 1L;

            List<Long> reviewIdsWithMyReplies = reviewRepository.findReviewIdsByOwnerId(currentOwnerId);
            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, ReviewSortType.LATEST);
            // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
            CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);
            List<Review> reviewsWithMyReplies = reviewRepository
                .findByReviewIdIn(reviewIdsWithMyReplies,params.getDateTime(), params.getReviewId(), pageable10);

            log.info("내 답글이 있는 리뷰 목록 조회 성공 - 리뷰 수: {}", reviewsWithMyReplies.size());  // ✅ 성공 로그 추가

            // 다음 페이지 체크
            boolean hasNext = reviewsWithMyReplies.size() > size10;
            if (hasNext) {
                reviewsWithMyReplies = reviewsWithMyReplies.subList(0, size10);
            }

            return createReviewListResponseDto(reviewsWithMyReplies, cursor, hasNext, authentication);

        } catch (BusinessException e) {
            log.error("내 답글 리뷰 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("내 답글 리뷰 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.REVIEW_REPLY_LIST_FETCH_FAILED, "내 답글 리뷰 목록 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public ReviewOneResponseDto deleteReview(
        Long reviewId,
        boolean withImage,
        ReviewSortType sortType,
        String cursor,
        Authentication authentication
    ) {
        try {
            validateReviewId(reviewId);

            Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "삭제할 리뷰를 찾을 수 없습니다."));

            // 권한 검증
            validateReviewWriter(deletedReview, authentication);

            // 이미지 삭제
            reviewImageService.deleteImagesBeforeDeleteReview(deletedReview);

            // 답글 있을 때, 답글 삭제
            if(deletedReview.getReply()!=null){
                reviewReplyService.deleteReviewReply(deletedReview.getReply().getReplyId(), authentication);
            }

            Long storeId = deletedReview.getStoreId();
            // 리뷰 삭제
            reviewRepository.delete(deletedReview);

            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, ReviewSortType.LATEST);

            //리뷰 리스트 받아오기
            List<Review> reviews = fetchReviews(cursorData, sortType, storeId, size1+1, withImage);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size1;
            if (hasNext) {
                reviews = reviews.subList(0, size1);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, sortType);
            }

            if (reviews.isEmpty()) {
                log.info("리뷰 삭제 후 다음 리뷰 없음. reviewId: {}", reviewId);
                return ReviewOneResponseDto.builder()
                    .review(null)
                    .cursor(null)
                    .hasNext(false)
                    .build();
            }

            Review nextReview = reviews.getFirst();

            log.info("리뷰 삭제 완료. reviewId: {}, storeId: {}, customerId: {}", reviewId, storeId, deletedReview.getCustomerId());
            return createReviewOneResponseDto(nextReview, hasNext, nextCursor, authentication);

        } catch (BusinessException e) {
            log.error("리뷰 삭제 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("리뷰 삭제 실패. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_DELETE_FAILED, "리뷰 삭제에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public ReviewOneResponseDto deleteReviewOnMyReviews(
        Long reviewId,
        ReviewSortType sortType, String cursor,
        Authentication authentication
    ) {
        try {
            validateReviewId(reviewId);

            Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "삭제할 리뷰를 찾을 수 없습니다."));

            // 권한 검증
            validateReviewWriter(deletedReview, authentication);

            Long customerId = deletedReview.getCustomerId();

            // 이미지 삭제
            reviewImageService.deleteImagesBeforeDeleteReview(deletedReview);

            // 답글 있을 때, 답글 삭제
            if(deletedReview.getReply()!=null){
                reviewReplyService.deleteReviewReply(deletedReview.getReply().getReplyId(), authentication);
            }

            // 리뷰 삭제
            reviewRepository.delete(deletedReview);

            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, sortType);
            // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
            CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);

            List<Review> reviews = reviewRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId, params.getDateTime(), params.getReviewId(), pageable1);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size1;
            if (hasNext) {
                reviews = reviews.subList(0, size1);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, sortType);
            }

            Review nextReview = reviews.getFirst();

            log.info("내 리뷰에서 리뷰 삭제 완료. reviewId: {}, customerId: {}", reviewId);
            return createReviewOneResponseDto(nextReview, hasNext, nextCursor, authentication);

        } catch (BusinessException e) {
            log.error("내 리뷰에서 리뷰 삭제 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("내 리뷰에서 리뷰 삭제 실패. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_DELETE_FAILED, "리뷰 삭제에 실패했습니다.");
        }
    }

    @Override
    public ReviewOneResponseDto deleteReviewOnMyReviewsOnStore(
        Long reviewId,
        Long storeId,
        ReviewSortType sortType,
        String cursor,
        Authentication authentication
    ) {
        try {
            validateReviewId(reviewId);

            Review deletedReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "삭제할 리뷰를 찾을 수 없습니다."));

            // 권한 검증
            validateReviewWriter(deletedReview, authentication);

            Long customerId = deletedReview.getCustomerId();

            // 이미지 삭제
            reviewImageService.deleteImagesBeforeDeleteReview(deletedReview);

            // 답글 있을 때, 답글 삭제
            if(deletedReview.getReply()!=null){
                reviewReplyService.deleteReviewReply(deletedReview.getReply().getReplyId(), authentication);
            }

            // 리뷰 삭제
            reviewRepository.delete(deletedReview);

            // Base64 디코딩 및 파싱
            CursorUtil.CursorData cursorData = cursorUtil.parseCursor(cursor, sortType);
            // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
            CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);

            List<Review> reviews = reviewRepository
                .findByCustomerIdAndStoreIdOrderByCreatedAtDesc(customerId, storeId, params.getDateTime(), params.getReviewId(), pageable1);

            // 다음 페이지 체크
            boolean hasNext = reviews.size() > size1;
            if (hasNext) {
                reviews = reviews.subList(0, size1);
            }

            // 다음 커서 생성 (Base64 인코딩)
            String nextCursor = null;
            if (hasNext && !reviews.isEmpty()) {
                Review lastReview = reviews.get(reviews.size() - 1);
                nextCursor = cursorUtil.createCursor(lastReview, sortType);
            }

            Review nextReview = reviews.getFirst();

            log.info("해당가게 내 리뷰리스트에서 리뷰 삭제 완료. reviewId: {}, customerId: {}", reviewId);
            return createReviewOneResponseDto(nextReview, hasNext, nextCursor, authentication);

        } catch (BusinessException e) {
            log.error("해당가게 내 리뷰리스트에서 리뷰 삭제 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("해당가게 내 리뷰리스트에서 리뷰 삭제 실패. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_DELETE_FAILED, "리뷰 삭제에 실패했습니다.");
        }
    }

    private ReviewOneResponseDto createReviewOneResponseDto(Review nextReview, boolean hasNext, String nextCursor, Authentication authentication) {
        ReviewWithHelpfulStatusResponseDto dto = createReviewDetailResponseDto(nextReview, authentication);
        return ReviewOneResponseDto.builder()
            .review(dto)
            .cursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

    // ================== 검증 메서드 ==================

    private void validateStoreId(Long storeId) {
        if (storeId == null || storeId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_STORE_ID, "유효하지 않은 매장 ID입니다.");
        }
    }

    private void validateReviewId(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REVIEW_ID, "유효하지 않은 리뷰 ID입니다.");
        }
    }

    private void validateRequest(Object request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "요청 데이터가 없습니다.");
        }
    }

    private void validateSortType(ReviewSortType sortType) {
        if (sortType == null) {
            throw new BusinessException(ErrorCode.INVALID_SORT_TYPE, "정렬 타입이 필요합니다.");
        }
    }

    private void validateDuplicateReview(Long customerId, Long orderId) {
        if (orderId != null && reviewRepository.existsByCustomerIdAndOrderId(customerId, orderId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_REVIEW, "이미 해당 주문에 대한 리뷰가 존재합니다.");
        }
    }

    private void validateReviewWriter(Review review, Authentication authentication) {
        Long currentUserId = 1L;//getCurrentUserId(authentication);
        if (!review.getCustomerId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.REVIEW_UNAUTHORIZED_ACCESS, "본인이 작성한 리뷰만 수정/삭제할 수 있습니다.");
        }
    }

    private void validateUpdatePermission(Review review) {
        if (review.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = review.getCreatedAt();
            LocalDateTime deadline = createdAt.plusHours(24);

            if (now.isAfter(deadline)) {
                throw new BusinessException(ErrorCode.UPDATE_TIME_EXPIRED, "리뷰는 작성 후 24시간 내에만 수정 가능합니다.");
            }
        }
    }

    private void validateContentAndRating(String content, Integer rating) {
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_REVIEW_CONTENT, "리뷰 내용은 필수입니다.");
        }
        if (content.length() > 300) {
            throw new BusinessException(ErrorCode.REVIEW_CONTENT_TOO_LONG, "리뷰 내용은 300자를 초과할 수 없습니다.");
        }
        if (rating == null) {
            throw new BusinessException(ErrorCode.MISSING_RATING, "평점은 필수입니다.");
        }
        if (rating < 1 || rating > 5) {
            throw new BusinessException(ErrorCode.INVALID_RATING, "평점은 1-5 사이의 값이어야 합니다.");
        }
    }

    // ================== 유틸리티 메서드 ==================

    private Long getCurrentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtUserInfo userInfo) {
            //return userInfo.userId();
        }

        throw new BusinessException(ErrorCode.INVALID_AUTHENTICATION, "인증 정보를 찾을 수 없습니다.");
    }

    /**
     *
     * 이미지/정렬 타입에 따른 리뷰 리스트 추출
     */
    private List<Review> fetchReviews(
        CursorUtil.CursorData cursorData,
        ReviewSortType sortType,
        Long storeId,
        int limit,
        boolean withImage
    ) {
        PageRequest pageRequest = PageRequest.of(0, limit);

        // 커서에서 날짜 타입과 숫자 타입 분리하여 값 추출
        CursorUtil.CursorParams params = cursorUtil.extractParams(cursorData);

        // 정렬 타입에 따라 적절한 메서드 호출
        return switch (sortType) {
            case LATEST -> reviewRepository.findLatest(
                storeId, params.getDateTime(), params.getReviewId(), withImage, pageRequest
            );
            case OLDEST -> reviewRepository.findOldest(
                storeId, params.getDateTime(), params.getReviewId(), withImage, pageRequest
            );
            case RATING_HIGH -> reviewRepository.findByRatingHigh(
                storeId, params.getNumericValue(), params.getReviewId(), withImage, pageRequest
            );
            case RATING_LOW -> reviewRepository.findByRatingLow(
                storeId, params.getNumericValue(), params.getReviewId(), withImage, pageRequest
            );
            case HELPFUL_HIGH -> reviewRepository.findByHelpfulHigh(
                storeId, params.getNumericValue(), params.getReviewId(), withImage, pageRequest
            );
        };
    }

    // ================== dto 생성 메서드 ==================

    private ReviewReplyResponseDto getReplyDto(ReviewReply reply) {
        if (reply == null) {
            return null;
        }

        return ReviewReplyResponseDto.builder()
            .replyId(reply.getReplyId())
            .ownerId(reply.getOwnerId())
            .content(reply.getContent())
            .createdAt(reply.getCreatedAt())
            .updatedAt(reply.getUpdatedAt())
            .build();
    }

    private ReviewResponseDto createReviewResponseDto(
        Review review,
        List<ReviewImageResponseDto> imageList,
        ReviewReplyResponseDto replyDto,
        Authentication authentication) {
        return ReviewResponseDto.builder()
            .reviewId(review.getReviewId())
            .customerId(review.getCustomerId())
            .storeId(review.getStoreId())
            .orderId(review.getOrderId())
            .userName("유저"/*userService.getUserName()*/)
            .profileImage("imgUrl"/*userService.getProfileImage()*/)
            .content(review.getContent())
            .rating(review.getRating())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .helpfulCount(review.getHelpfulCount())
            .images(imageList)
            .reply(replyDto)
            .build();
    }

    private ReviewWithHelpfulStatusResponseDto createReviewDetailResponseDto(
        Review review,
        Authentication authentication
    ) {
        if (review == null) {
            return null;
        }

        List<ReviewImageResponseDto> imageList = reviewImageService.getImageDtoList(review.getImages());
        ReviewReplyResponseDto replyDto = getReplyDto(review.getReply());

        boolean isHelpful = reviewHelpfulService.createReviewHelpfulStatusResponseDto(review, authentication);

        return ReviewWithHelpfulStatusResponseDto.builder()
            .reviewId(review.getReviewId())
            .customerId(review.getCustomerId())
            .storeId(review.getStoreId())
            .orderId(review.getOrderId())
            .userName("유저"/*userService.getUserName()*/)
            .profileImage("imgUrl"/*userService.getProfileImage()*/)
            .content(review.getContent())
            .rating(review.getRating())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .images(imageList)
            .reply(replyDto)
            .isHelpful(isHelpful)
            .build();
    }

    protected List<ReviewWithHelpfulStatusResponseDto> createReviewWithHelpfulStatusResponseDtoList(
        List<Review> reviews,
        Authentication authentication
    ) {
        return reviews.stream()
            .map(review -> createReviewDetailResponseDto(review, authentication))
            .toList();
    }

    private Review createReviewEntity(Long storeId, ReviewCreateRequestDto request) {
        return Review.builder()
            .customerId(request.customerId())
            .storeId(storeId)
            .content(request.content())
            .orderId(request.orderId())
            .rating(request.rating())
            .helpfulCount(0)
            .build();
    }

    protected ReviewListResponseDto createReviewListResponseDto(
        List<Review> reviews,
        String cursor,
        boolean hasNext,
        Authentication authentication
    ) {

        return ReviewListResponseDto.builder()
            .reviewList(createReviewWithHelpfulStatusResponseDtoList(reviews, authentication))
            .cursor(cursor)
            .hasNext(hasNext)
            .build();

    }
}