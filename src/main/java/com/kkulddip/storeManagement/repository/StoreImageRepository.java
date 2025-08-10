package com.kkulddip.storeManagement.repository;

import com.kkulddip.storeManagement.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 가게 이미지 레포지토리 (Review 스타일로 단순화)
 */
public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

    /**
     * 가게별 이미지 조회 (업로드 순서로 정렬)
     */
    List<StoreImage> findByStoreIdOrderByUploadOrderAsc(Long storeId);

    /**
     * 가게별 이미지 개수 조회
     */
    int countByStoreId(Long storeId);

    /**
     * 이미지 ID 목록으로 조회
     */
    List<StoreImage> findByStoreImgIdIn(List<Long> imageIds);

    /**
     * 가게별 최대 업로드 순서 조회
     */
    @Query("SELECT COALESCE(MAX(si.uploadOrder), 0) FROM StoreImage si WHERE si.storeId = :storeId")
    Optional<Integer> findMaxUploadOrderByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게의 특정 이미지 ID들로 조회 (소유권 검증용)
     */
    List<StoreImage> findByStoreIdAndStoreImgIdIn(Long storeId, List<Long> imageIds);
}