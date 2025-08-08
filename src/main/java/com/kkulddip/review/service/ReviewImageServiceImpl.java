package com.kkulddip.review.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.review.dto.response.ReviewImageResponseDto;
import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.ReviewImage;
import com.kkulddip.review.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewImageServiceImpl implements ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 상수 정의
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final String ALLOWED_CONTENT_TYPE_PREFIX = "image/";
    private static final int MAX_IMAGES_PER_REVIEW = 3; // 리뷰당 최대 이미지 수

    // ================== 리뷰 기본 메서드 ==================

    @Transactional
    @Override
    public List<ReviewImageResponseDto> addImage(
        Long reviewId,
        List<MultipartFile> images,
        int deleteImageCount
    ) {
        try {
            // 입력값 검증
            validateAddImageRequest(reviewId, images);

            // 현재 이미지 수 체크
            validateImageCount(reviewId, images.size(), deleteImageCount);

            List<String> uploadedFileNames = new ArrayList<>();
            int initialOrder = reviewImageRepository
                .findMaxUploadOrderByReviewId(reviewId).orElse(0);

            List<ReviewImageResponseDto> result = IntStream.range(0, images.size())
                .mapToObj(i -> processImageUpload(images.get(i), reviewId, initialOrder + i + 1, uploadedFileNames))
                .toList();

            log.info("이미지 업로드 완료. reviewId: {}, uploadCount: {}", reviewId, images.size());
            return result;

        } catch (BusinessException e) {
            log.error("이미지 업로드 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("이미지 업로드 실패 - 시스템 에러. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_UPLOAD_FAILED, "이미지 업로드에 실패했습니다.");
        }
    }

    private ReviewImageResponseDto processImageUpload(
        MultipartFile file,
        Long reviewId,
        int order,
        List<String> uploadedFileNames
    ) {
        try {
            validateImageFile(file);

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateUniqueFileName(extension);
            String imageUrl = uploadToS3(fileName, file, uploadedFileNames);

            ReviewImage image = createReviewImage(file, imageUrl, reviewId, order);
            ReviewImage savedImage = reviewImageRepository.save(image);

            log.debug("이미지 업로드 성공. fileName: {}, reviewId: {}", fileName, reviewId);
            return getReviewImageResponseDto(savedImage);

        } catch (BusinessException e) {
            // S3 정리
            cleanupS3Files(uploadedFileNames);
            throw e;
        } catch (Exception e) {
            // S3 정리
            cleanupS3Files(uploadedFileNames);
            log.error("이미지 처리 실패. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_UPLOAD_FAILED, "이미지 처리에 실패했습니다.");
        }
    }

    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    @Transactional
    @Override
    public void deleteImages(Long reviewId, List<Long> deleteImageIds) {
        try {
            // 1. 입력값 검증
            validateDeleteImageRequest(reviewId, deleteImageIds);

            if (deleteImageIds.isEmpty()) {
                log.info("삭제할 이미지가 없습니다. reviewId: {}", reviewId);
                return;
            }

            List<ReviewImage> deletedImageList = reviewImageRepository
                .findByReviewImgIdIn(deleteImageIds);

            if (deletedImageList.isEmpty()) {
                log.warn("삭제할 이미지를 찾을 수 없습니다. reviewId: {}, imageIds: {}", reviewId, deleteImageIds);
                return;
            }
            // 혹시 삭제할 이미지 아이디가 중복돼서 들어왔을 때 중복 제거
            List<Long> uniqueImageIds = deleteImageIds.stream()
                .distinct()
                .toList();

            List<String> fileNames = deletedImageList.stream()
                .map(ReviewImage::getImageUrl)
                .map(this::extractFileNameFromUrl)
                .filter(fileName -> !fileName.isEmpty())
                .toList();

            // 2. S3에서 삭제
            List<String> failedFiles = cleanupS3FilesWithResult(fileNames);

            // 3. S3 삭제 실패 시 경고 로그
            if (!failedFiles.isEmpty()) {
                log.warn("일부 S3 파일 삭제 실패. 수동 정리 필요: {}", failedFiles);
            }

            // 4. DB에서 삭제
            reviewImageRepository.deleteAll(deletedImageList);
            log.info("DB에서 이미지 삭제 완료. reviewId: {}, imageIds: {}", reviewId, deleteImageIds);

        } catch (BusinessException e) {
            log.error("이미지 삭제 실패 - 비즈니스 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("이미지 삭제 실패 - 시스템 에러. reviewId: {}", reviewId, e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_DELETE_FAILED, "이미지 삭제에 실패했습니다.");
        }
    }

    /**
     * S3에서 삭제가 실패할 경우, 수동 삭제 할 수 있게끔 실패 파일들 반환
     * @param fileNames
     * @return
     */
    private List<String> cleanupS3FilesWithResult(List<String> fileNames) {
        List<String> failedFiles = new ArrayList<>();
        int successCount = 0;

        for (String fileName : fileNames) {
            try {
                amazonS3.deleteObject(bucket, fileName);
                successCount++;
                log.debug("S3 파일 삭제 성공: {}", fileName);
            } catch (Exception e) {
                log.warn("S3 파일 삭제 실패: {}", fileName, e);
                failedFiles.add(fileName);
            }
        }

        log.info("S3 파일 삭제 완료. 성공: {}, 실패: {}", successCount, failedFiles.size());
        return failedFiles;
    }

    private void cleanupS3Files(List<String> fileNames) {
        cleanupS3FilesWithResult(fileNames);
    }

    /**
     * S3에 이미지 업로드 및 URL 반환
     */
    private String uploadToS3(
        String fileName,
        MultipartFile file,
        List<String> uploadedFileNames
    ) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
            uploadedFileNames.add(fileName); // 업로드된 파일 기록

            return amazonS3.getUrl(bucket, fileName).toString();

        } catch (IOException e) {
            log.error("S3 업로드 실패. fileName: {}", fileName, e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_UPLOAD_FAILED, "S3 업로드에 실패했습니다.");
        } catch (Exception e) {
            log.error("S3 업로드 중 예상치 못한 오류. fileName: {}", fileName, e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_UPLOAD_FAILED, "이미지 업로드에 실패했습니다.");
        }
    }

    @Override
    public void deleteImagesBeforeDeleteReview(Review deletedReview) {
        if (deletedReview == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "삭제할 리뷰 정보가 없습니다.");
        }

        List<ReviewImage> images = deletedReview.getImages();
        if (images == null || images.isEmpty()) {
            log.info("삭제할 이미지가 없습니다. reviewId: {}", deletedReview.getReviewId());
            return;
        }

        try {
            List<Long> deleteImageIds = images.stream()
                .map(ReviewImage::getReviewImgId)
                .toList();

            deleteImages(deletedReview.getReviewId(), deleteImageIds);
            log.info("리뷰 삭제 전 이미지 정리 완료. reviewId: {}, imageCount: {}",
                deletedReview.getReviewId(), images.size());

        } catch (BusinessException e) {
            log.error("리뷰 삭제 전 이미지 정리 실패 - 비즈니스 에러. reviewId: {}",
                deletedReview.getReviewId(), e);
            throw e; // 비즈니스 예외는 그대로 전파
        } catch (Exception e) {
            log.error("리뷰 삭제 전 이미지 정리 실패 - 시스템 에러. reviewId: {}",
                deletedReview.getReviewId(), e);
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_DELETE_FAILED,
                "리뷰 이미지 삭제에 실패하여 리뷰를 삭제할 수 없습니다.");
        }
    }

    // ================== 검증 메서드 ==================
    private void validateImageFile(MultipartFile file) {
        // 1. 파일이 비어있는지 확인
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_EMPTY_FILE, "빈 파일은 업로드할 수 없습니다.");
        }

        // 2. 파일 크기 제한
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_TOO_LARGE,
                String.format("파일 크기는 %dMB를 초과할 수 없습니다.", MAX_FILE_SIZE / (1024 * 1024)));
        }

        // 3. 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_INVALID_FORMAT,
                "지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // 4. Content-Type 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(ALLOWED_CONTENT_TYPE_PREFIX)) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_INVALID_FORMAT, "이미지 파일만 업로드 가능합니다.");
        }
    }

    private void validateAddImageRequest(Long reviewId, List<MultipartFile> images) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REVIEW_ID, "유효하지 않은 리뷰 ID입니다.");
        }

        if (images == null || images.isEmpty()) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_EMPTY_FILE, "업로드할 이미지가 없습니다.");
        }

        if (images.size() > MAX_IMAGES_PER_REVIEW) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_TOO_MANY,
                String.format("한 번에 최대 %d개의 이미지만 업로드 가능합니다.", MAX_IMAGES_PER_REVIEW));
        }
    }

    private void validateImageCount(Long reviewId, int newImageCount, int deleteImageCount) {
        int currentImageCount = reviewImageRepository.countByReviewId(reviewId);
        if (currentImageCount + newImageCount - deleteImageCount > MAX_IMAGES_PER_REVIEW) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED,
                String.format("리뷰에 현재 %d개의 이미지가 있어 %d개 이미지 등록은 불가능합니다.", currentImageCount,newImageCount));
        }
    }

    private boolean isValidImageExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        // .을 제거하고 확장자만 비교
        String extensionWithoutDot = extension.startsWith(".") ? extension.substring(1) : extension;
        return ALLOWED_EXTENSIONS.contains(extensionWithoutDot);
    }

    private void validateDeleteImageRequest(Long reviewId, List<Long> deleteImageIds) {
        if (reviewId == null || reviewId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REVIEW_ID, "유효하지 않은 리뷰 ID입니다.");
        }

        if (deleteImageIds == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "삭제할 이미지 ID 목록이 필요합니다.");
        }
        List<ReviewImage> images = reviewImageRepository.findAllById(deleteImageIds);
        // 각 이미지가 해당 리뷰에 속하는지 검증
        for (ReviewImage image : images) {
            if (!image.getReviewId().equals(reviewId)) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "삭제할 수 없는 이미지가 포함되어 있습니다.");
            }
        }
    }

    // ================== 유틸리티 메서드 ==================

    /**
     * 이미지 파일이름 추출
     */
    private String extractFileNameFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return "";
            }

            int lastSlashIndex = imageUrl.lastIndexOf('/');
            if (lastSlashIndex == -1 || lastSlashIndex == imageUrl.length() - 1) {
                return "";
            }

            return imageUrl.substring(lastSlashIndex + 1);
        } catch (Exception e) {
            log.warn("URL에서 파일명 추출 실패: {}", imageUrl, e);
            return "";
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
    }

    // ================== dto 생성 메서드 ==================
    @Override
    public List<ReviewImageResponseDto> getImageDtoList(List<ReviewImage> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }

        return images.stream()
            .map(this::getReviewImageResponseDto)
            .toList();
    }

    private ReviewImage createReviewImage(
        MultipartFile file,
        String imageUrl,
        Long reviewId,
        int order
    ) {
        return ReviewImage.builder()
            .imageUrl(imageUrl)
            .originalName(file.getOriginalFilename())
            .fileSize(file.getSize())
            .uploadOrder(order)
            .reviewId(reviewId)
            .build();
    }

    @Override
    public ReviewImageResponseDto getReviewImageResponseDto(ReviewImage image) {
        if (image == null) {
            return null;
        }

        return ReviewImageResponseDto.builder()
            .reviewImgId(image.getReviewImgId())
            .imageUrl(image.getImageUrl())
            .originalName(image.getOriginalName())
            .fileSize(image.getFileSize())
            .uploadOrder(image.getUploadOrder())
            .createdAt(image.getCreatedAt())
            .build();
    }
}