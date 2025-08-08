package com.kkulddip.review.service;

import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.ReviewImage;
import com.kkulddip.review.dto.response.ReviewImageResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ReviewImageService {
    /**
     * 리뷰에 이미지 추가
     * @param reviewId 리뷰 ID
     * @param imageUrl 이미지 URL
     * @param originalName 원본 파일명
     * @param fileSize 파일 크기 (bytes)
     * @param uploadOrder 업로드 순서
     * @return 생성된 이미지 엔티티
     */
    List<ReviewImageResponseDto> addImage(Long reviewId, List<MultipartFile> images, int deleteImageCount);

    /**
     *
     * @param reviewId
     * @param deleteImageIds
     */
    void deleteImages(Long reviewId, List<Long> deleteImageIds);

    /**
     *
     * @param image
     * @return
     */
    ReviewImageResponseDto getReviewImageResponseDto(ReviewImage image);

    /**
     *
     * @param images
     * @return
     */
    List<ReviewImageResponseDto> getImageDtoList(List<ReviewImage> images);

    /**
     *
     * @param deletedReview
     */
    void deleteImagesBeforeDeleteReview(Review deletedReview);
}
