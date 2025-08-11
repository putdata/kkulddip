package com.kkulddip.storeManagement.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 가게 이미지 서비스 인터페이스 - 단일 이미지 처리
 */
public interface StoreImageService {
    
    /**
     * 단일 이미지를 S3에 업로드
     * @param image 업로드할 이미지 파일
     * @return S3 이미지 URL
     */
    String uploadSingleImageToS3(MultipartFile image);
    
    /**
     * S3에서 이미지 삭제
     * @param imageUrl S3 이미지 URL
     */
    void deleteImageFromS3(String imageUrl);
}