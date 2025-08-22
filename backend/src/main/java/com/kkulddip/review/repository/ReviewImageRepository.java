package com.kkulddip.review.repository;

import com.kkulddip.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @Query("SELECT MAX(ri.uploadOrder) FROM ReviewImage ri WHERE ri.reviewId = :reviewId")
    Optional<Integer> findMaxUploadOrderByReviewId(@Param("reviewId") Long reviewId);

    List<ReviewImage> findByReviewImgIdIn(List<Long> deleteImageIds);

    void deleteByReviewImgIdIn(List<Long> deleteImageIds);

    int countByReviewId(Long reviewId);
}