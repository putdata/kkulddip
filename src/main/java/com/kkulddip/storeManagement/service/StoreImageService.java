package com.kkulddip.storeManagement.service;

import com.kkulddip.storeManagement.dto.response.StoreImageResponseDto;
import com.kkulddip.storeManagement.entity.StoreImage;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 가게 이미지 서비스 인터페이스 (Review 스타일로 단순화)
 */
public interface StoreImageService {

    /**
     * 가게에 이미지 추가
     * @param storeId 가게 ID
     * @param images 이미지 파일들
     * @param deleteImageCount 삭제될 이미지 개수 (개수 제한 확인용)
     * @return 생성된 이미지 DTO 목록
     */
    List<StoreImageResponseDto> addImage(Long storeId, List<MultipartFile> images, int deleteImageCount);

    /**
     * 이미지 삭제
     * @param storeId 가게 ID
     * @param deleteImageIds 삭제할 이미지 ID 목록
     */
    void deleteImages(Long storeId, List<Long> deleteImageIds);

    /**
     * 가게 이미지 목록 조회
     * @param storeId 가게 ID
     * @return 이미지 DTO 목록
     */
    List<StoreImageResponseDto> getStoreImages(Long storeId);

    /**
     * 이미지 엔티티를 DTO로 변환
     * @param image 이미지 엔티티
     * @return 이미지 DTO
     */
    StoreImageResponseDto getStoreImageResponseDto(StoreImage image);

    /**
     * 이미지 목록을 DTO 목록으로 변환
     * @param images 이미지 엔티티 목록
     * @return 이미지 DTO 목록
     */
    List<StoreImageResponseDto> getImageDtoList(List<StoreImage> images);

    /**
     * 가게 삭제 전 이미지 정리
     * @param storeId 가게 ID
     */
    void deleteImagesBeforeDeleteStore(Long storeId);
}