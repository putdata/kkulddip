package com.kkulddip.review.service;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.review.dto.request.ReviewCreateRequestDto;
import com.kkulddip.review.dto.request.ReviewUpdateRequestDto;
import com.kkulddip.review.dto.response.ReviewListResponseDto;
import com.kkulddip.review.dto.response.ReviewOneResponseDto;
import com.kkulddip.review.dto.response.ReviewResponseDto;
import com.kkulddip.review.entity.enums.ReviewSortType;
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
	 * @param userInfo
	 * @return
	 */
	ReviewResponseDto createReview(Long storeId, ReviewCreateRequestDto request, List<MultipartFile> images, JwtUserInfo userInfo);

	/**
	 *
	 * @param reviewId
	 * @param request
	 * @param newImages
	 * @param userInfo
	 * @return
	 */
	ReviewResponseDto updateReview(Long reviewId, ReviewUpdateRequestDto request, List<MultipartFile> newImages, JwtUserInfo userInfo);

	/**
	 *
	 * @param storeId
	 * @param sortType
	 * @param cursor
	 * @param userInfo
	 * @return
	 */
	ReviewListResponseDto getReviewListByStoreId(Long storeId, boolean withImage, ReviewSortType sortType, String cursor, JwtUserInfo userInfo);

	/**
	 *
	 * @param cursor
	 * @param sortType
	 * @param userInfo
	 * @return
	 */
	ReviewListResponseDto getMyReviewList(String cursor, ReviewSortType sortType, JwtUserInfo userInfo);

	/**
	 *
	 * @param cursor
	 * @param sortType
	 * @param storeId
	 * @param userInfo
	 * @return
	 */
	ReviewListResponseDto getMyReviewListByStoreId(String cursor, ReviewSortType sortType, Long storeId, JwtUserInfo userInfo);

	/**
	 *
	 * @param reviewId
	 * @param userInfo
	 * @return
	 */
	ReviewOneResponseDto deleteReview(Long reviewId, boolean withImage, ReviewSortType sortType, String cursor, JwtUserInfo userInfo);

	/**
	 *
	 * @param reviewId
	 * @param sortType
	 * @param cursor
	 * @param userInfo
	 * @return
	 */
	ReviewOneResponseDto deleteReviewOnMyReviews(Long reviewId, ReviewSortType sortType, String cursor, JwtUserInfo userInfo);

	ReviewOneResponseDto deleteReviewOnMyReviewsOnStore(Long reviewId, Long storeId, ReviewSortType sortType, String cursor, JwtUserInfo userInfo);

	ReviewListResponseDto getMyReplyReviewList(String cursor, JwtUserInfo userInfo);
}