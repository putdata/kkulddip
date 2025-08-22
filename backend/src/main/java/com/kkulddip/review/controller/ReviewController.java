package com.kkulddip.review.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.request.ReviewCreateRequestDto;
import com.kkulddip.review.dto.request.ReviewReplyRequestDto;
import com.kkulddip.review.dto.request.ReviewUpdateRequestDto;
import com.kkulddip.review.dto.response.ReviewHelpfulCreateResponseDto;
import com.kkulddip.review.dto.response.ReviewListResponseDto;
import com.kkulddip.review.dto.response.ReviewOneResponseDto;
import com.kkulddip.review.dto.response.ReviewReplyResponseDto;
import com.kkulddip.review.dto.response.ReviewResponseDto;
import com.kkulddip.review.entity.enums.ReviewSortType;
import com.kkulddip.review.service.ReviewHelpfulService;
import com.kkulddip.review.service.ReviewReplyService;
import com.kkulddip.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewReplyService reviewReplyService;
    private final ReviewHelpfulService reviewHelpfulService;

    /**
     * 리뷰 생성(소비자만)
     * @param storeId 가게 ID
     * @param request 리뷰 등록 요청(내용, 평점, 이미지)
     * @param images 업로드할 이미지 파일들
     * @param userInfo 인증된 사용자 정보
     * @return 응답 객체 : 리뷰 기본 정보, 이미지들
     */
    @PostMapping("/{storeId}")
    public ApiResponse<ReviewResponseDto> createReview(
        @PathVariable("storeId") Long storeId,
        @RequestPart("request") @Valid ReviewCreateRequestDto request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewResponseDto response = reviewService
            .createReview(storeId, request, images, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 가게 아이디로 리뷰 조회(평점,좋아요,시간)
     * @param storeId 가게 ID
     * @param withImage 이미지 포함 여부
     * @param sortType 정렬 타입
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     * @return 응답 객체 : 리뷰 기본 정보, 이미지들, 리뷰 답글, 좋아요 개수, 좋아요 눌렀는지 boolean, 페이지정보
     */
    @GetMapping("/{storeId}")
    public ApiResponse<ReviewListResponseDto> getReviewListByStoreId(
        @PathVariable("storeId") Long storeId,
        @RequestParam(value = "withImage", defaultValue = "false") boolean withImage,
        @RequestParam(value = "sortType", defaultValue = "LATEST") ReviewSortType sortType,
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewListResponseDto response = reviewService
            .getReviewListByStoreId(storeId, withImage, sortType, cursor, userInfo);
        return ApiResponse.of(response);
    }


    /**
     * 내가 쓴 리뷰 전체 조회
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     * @return 응답 객체 : 리뷰 기본 정보, 이미지들, 리뷰 답글, 좋아요 개수, 좋아요 눌렀는지 boolean, 페이지 정보
     */
    @GetMapping("/myReview")
    public ApiResponse<ReviewListResponseDto> getMyReviewList(
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewListResponseDto response = reviewService
            .getMyReviewList(cursor, ReviewSortType.LATEST, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 해당 가게에 내가 쓴 리뷰 전체 조회
     * @param cursor 커서 기반 페이징
     * @param storeId 가게 ID
     * @param userInfo 인증된 사용자 정보
     */
    @GetMapping("/myReview/{storeId}")
    public ApiResponse<ReviewListResponseDto> getMyReviewListByStoreId(
        @RequestParam(value = "cursor", required = false) String cursor,
        @PathVariable("storeId") Long storeId,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewListResponseDto response = reviewService
            .getMyReviewListByStoreId(cursor, ReviewSortType.LATEST, storeId, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 리뷰 수정(소비자 본인만)
     * @param reviewId 리뷰 ID
     * @param request 수정 요청 (request안에 삭제된 이미지 아이디 리스트도 있음)
     * @param newImages 새로 추가할 이미지 파일들
     * @param userInfo 인증된 사용자 정보
     * @return 응답 객체 : 리뷰 기본 정보, 이미지들
     */
    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponseDto> updateReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestPart("request") @Valid ReviewUpdateRequestDto request,
        @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewResponseDto response = reviewService
            .updateReview(reviewId, request, newImages, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 리뷰 삭제(관리자 혹은 소비자 본인)
     * 커서 기반으로 하나 더 가져와야 함 삭제하면 하나 땡겨지니
     * @param reviewId 리뷰 ID
     * @param withImage 이미지 포함 여부
     * @param sortType 정렬 타입
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     */
    @DeleteMapping("/{reviewId}")
    public ApiResponse<ReviewOneResponseDto> deleteReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestParam(value = "withImage", defaultValue = "false") boolean withImage,
        @RequestParam(value = "sortType", defaultValue = "LATEST") ReviewSortType sortType,
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewOneResponseDto response = reviewService
            .deleteReview(reviewId, withImage, sortType, cursor, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 특정 가게 내 리뷰 목록에서 리뷰 삭제
     * 위에 삭제 요청과 다르게 한 이유 : 전체 리뷰 리스트에서 삭제, 이건 특정 가게 내 리뷰 리스트
     * 삭제시 다음 리뷰 하나를 더 가져와야 하는데, 서로 가져오는 데 사용되는 조건이 다르기 때문
     * @param reviewId 리뷰 ID
     * @param storeId 가게 ID
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     * @return
     */
    @DeleteMapping("/myReview/{reviewId}/{storeId}")
    public ApiResponse<ReviewOneResponseDto> deleteReview(
        @PathVariable("reviewId") Long reviewId,
        @PathVariable("storeId") Long storeId,
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewOneResponseDto response = reviewService
            .deleteReviewOnMyReviewsOnStore(reviewId, storeId, ReviewSortType.LATEST, cursor, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 내 리뷰 목록에서 리뷰 삭제
     * 위에 삭제 요청과 다르게 한 이유 : 위는 특정 가게 내 리뷰 리스트에서 삭제, 이건 내 전체 리뷰 리스트
     * 삭제시 다음 리뷰 하나를 더 가져와야 하는데, 서로 가져오는 데 사용되는 조건이 다르기 때문
     * @param reviewId 리뷰 ID
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     * @return
     */
    @DeleteMapping("/myReview/{reviewId}")
    public ApiResponse<ReviewOneResponseDto> deleteReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewOneResponseDto response = reviewService
            .deleteReviewOnMyReviews(reviewId, ReviewSortType.LATEST, cursor, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 리뷰 답글 등록(점주만)
     * @param reviewId 리뷰 ID
     * @param request 리뷰 답글 등록 요청(내용, 점주 ID, 리뷰 ID)
     * @param userInfo 인증된 사용자 정보
     */
    @PostMapping("/reply/{reviewId}")
    public ApiResponse<ReviewReplyResponseDto> createReviewReply(
        @PathVariable("reviewId") Long reviewId,
        @RequestBody @Valid ReviewReplyRequestDto request,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewReplyResponseDto response = reviewReplyService
            .createReviewReply(request, reviewId, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 점주 본인이 답글 단 리뷰 리스트 조회
     * @param cursor 커서 기반 페이징
     * @param userInfo 인증된 사용자 정보
     * @return 응답 객체 : 리뷰 기본 정보, 이미지들, 리뷰 답글, 좋아요 개수
     */
    @GetMapping("/reply")
    public ApiResponse<ReviewListResponseDto> getMyReplyReviewList(
        @RequestParam(value = "cursor", required = false) String cursor,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewListResponseDto response = reviewService
            .getMyReplyReviewList(cursor, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 리뷰 답글 수정(점주만)
     * @param replyId 답글 ID
     * @param request 리뷰 답글 수정 요청
     * @param userInfo 인증된 사용자 정보
     */
    @PutMapping("/reply/{replyId}")
    public ApiResponse<ReviewReplyResponseDto> updateReviewReply(
        @PathVariable("replyId") Long replyId,
        @RequestBody @Valid ReviewReplyRequestDto request,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewReplyResponseDto response = reviewReplyService
            .updateReviewReply(replyId, request, userInfo);
        return ApiResponse.of(response);
    }

    /**
     * 리뷰 답글 삭제(점주만)
     * @param replyId 답글 ID
     * @param userInfo 인증된 사용자 정보
     * @return
     */
    @DeleteMapping("/reply/{replyId}")
    public ApiResponse<Void> deleteReviewReply(
        @PathVariable("replyId") Long replyId,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        reviewReplyService.deleteReviewReply(replyId, userInfo);
        return ApiResponse.of(200);
    }

    /**
     * 리뷰 좋아요
     * @param reviewId 리뷰 ID
     * @param userInfo 인증된 사용자 정보
     * @return 좋아요 true, 좋아요 개수++ 가져와야 함
     */
    @PostMapping("/helpful/{reviewId}")
    public ApiResponse<ReviewHelpfulCreateResponseDto> createHelpful(
        @PathVariable("reviewId") Long reviewId,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        ReviewHelpfulCreateResponseDto response = reviewHelpfulService
            .createReviewHelpful(reviewId, userInfo);
        return ApiResponse.of(response);
    }


    /**
     * 리뷰 좋아요 취소
     * @param reviewId 리뷰 ID
     * @param userInfo 인증된 사용자 정보
     * @return 좋아요 false, 좋아요 개수-- 가져와야 함
     */
    @DeleteMapping("/helpful/{reviewId}")
    public ApiResponse<Boolean> deleteReviewHelpful(
        @PathVariable("reviewId") Long reviewId,
        @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        boolean response = reviewHelpfulService
            .deleteReviewHelpful(reviewId, userInfo);
        return ApiResponse.of(response);
    }

}