package com.kkulddip.review.service;

import com.kkulddip.review.dto.request.ReviewCreateRequestDto;
import com.kkulddip.review.dto.request.ReviewUpdateRequestDto;
import com.kkulddip.review.dto.response.ReviewListResponseDto;
import com.kkulddip.review.dto.response.ReviewOneResponseDto;
import com.kkulddip.review.dto.response.ReviewResponseDto;
import com.kkulddip.review.entity.enums.ReviewSortType;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 리뷰 관련 서비스 인터페이스
 */
public interface ReviewService {

	/**
	 *
	 * @param storeId
	 * @param request
	 * @param images
	 * @param authentication
	 * @return
	 */
	ReviewResponseDto createReview(Long storeId, ReviewCreateRequestDto request, List<MultipartFile> images, Authentication authentication);

	/**
	 *
	 * @param reviewId
	 * @param request
	 * @param newImages
	 * @param authentication
	 * @return
	 */
	ReviewResponseDto updateReview(Long reviewId, ReviewUpdateRequestDto request, List<MultipartFile> newImages, Authentication authentication);

	/**
	 *
	 * @param storeId
	 * @param sortType
	 * @param cursor
	 * @param authentication
	 * @return
	 */
	ReviewListResponseDto getReviewListByStoreId(Long storeId, boolean withImage, ReviewSortType sortType, String cursor, Authentication authentication);

	/**
	 *
	 * @param cursor
	 * @param sortType
	 * @param authentication
	 * @return
	 */
	ReviewListResponseDto getMyReviewList(String cursor, ReviewSortType sortType, Authentication authentication);

	/**
	 *
	 * @param cursor
	 * @param sortType
	 * @param storeId
	 * @param authentication
	 * @return
	 */
	ReviewListResponseDto getMyReviewListByStoreId(String cursor, ReviewSortType sortType, Long storeId, Authentication authentication);

	/**
	 *
	 * @param reviewId
	 * @param authentication
	 * @return
	 */
	ReviewOneResponseDto deleteReview(Long reviewId, boolean withImage, ReviewSortType sortType, String cursor, Authentication authentication);

	/**
	 *
	 * @param reviewId
	 * @param sortType
	 * @param cursor
	 * @param authentication
	 * @return
	 */
	ReviewOneResponseDto deleteReviewOnMyReviews(Long reviewId, ReviewSortType sortType, String cursor, Authentication authentication);

	ReviewOneResponseDto deleteReviewOnMyReviewsOnStore(Long reviewId, Long storeId, ReviewSortType sortType, String cursor, Authentication authentication);

	ReviewListResponseDto getMyReplyReviewList(String cursor, Authentication authentication);
}