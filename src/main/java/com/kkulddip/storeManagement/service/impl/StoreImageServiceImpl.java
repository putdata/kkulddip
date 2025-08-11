package com.kkulddip.storeManagement.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.storeManagement.service.StoreImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 가게 이미지 서비스 구현체 - 단일 이미지 처리
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StoreImageServiceImpl implements StoreImageService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 상수 정의
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final String ALLOWED_CONTENT_TYPE_PREFIX = "image/";

    @Override
    public String uploadSingleImageToS3(MultipartFile image) {
        // 이미지 파일 검증
        validateImageFile(image);
        
        try {
            String originalFilename = image.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateUniqueFileName(extension);
            
            // S3에 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());
            
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata));
            
            String imageUrl = amazonS3.getUrl(bucket, fileName).toString();
            log.info("단일 이미지 S3 업로드 성공 - fileName: {}, url: {}", fileName, imageUrl);
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new BusinessException(ErrorCode.STORE_IMAGE_UPLOAD_FAILED, "S3 업로드에 실패했습니다.");
        } catch (Exception e) {
            log.error("S3 업로드 중 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.STORE_IMAGE_UPLOAD_FAILED, "이미지 업로드에 실패했습니다.");
        }
    }
    
    @Override
    public void deleteImageFromS3(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("S3 이미지 URL이 비어있습니다.");
            return;
        }
        
        try {
            String fileName = extractFileNameFromUrl(imageUrl);
            if (!fileName.isEmpty()) {
                amazonS3.deleteObject(bucket, fileName);
                log.info("S3 이미지 삭제 성공 - fileName: {}", fileName);
            }
        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패 - imageUrl: {}", imageUrl, e);
            // 삭제 실패해도 예외를 던지지 않고 로그만 남김
        }
    }

    // ================== 유틸리티 메서드 ==================
    
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.STORE_IMAGE_EMPTY_FILE, "빈 파일은 업로드할 수 없습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.STORE_IMAGE_TOO_LARGE,
                String.format("파일 크기는 %dMB를 초과할 수 없습니다.", MAX_FILE_SIZE / (1024 * 1024)));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
            throw new BusinessException(ErrorCode.STORE_IMAGE_INVALID_FORMAT,
                "지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(ALLOWED_CONTENT_TYPE_PREFIX)) {
            throw new BusinessException(ErrorCode.STORE_IMAGE_INVALID_FORMAT, "이미지 파일만 업로드 가능합니다.");
        }
    }
    
    private String generateUniqueFileName(String extension) {
        return "store/" + UUID.randomUUID().toString() + "." + extension;
    }

    private String getFileExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidImageExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private String extractFileNameFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return "";
            }

            // S3 URL에서 버킷명 이후의 경로를 추출
            // 예: https://bucket-name.s3.amazonaws.com/store/uuid.jpg -> store/uuid.jpg
            String bucketPattern = bucket + ".s3";
            int bucketIndex = imageUrl.indexOf(bucketPattern);
            if (bucketIndex != -1) {
                int startIndex = imageUrl.indexOf("/", bucketIndex + bucketPattern.length());
                if (startIndex != -1) {
                    return imageUrl.substring(startIndex + 1);
                }
            }
            
            // 대체 패턴: 마지막 슬래시 이후를 파일명으로 간주
            int lastSlashIndex = imageUrl.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < imageUrl.length() - 1) {
                // store/ 프리픽스가 있는지 확인
                String fileName = imageUrl.substring(lastSlashIndex + 1);
                if (imageUrl.contains("/store/")) {
                    return "store/" + fileName;
                }
                return fileName;
            }
            
            return "";
        } catch (Exception e) {
            log.warn("URL에서 파일명 추출 실패: {}", imageUrl, e);
            return "";
        }
    }
}