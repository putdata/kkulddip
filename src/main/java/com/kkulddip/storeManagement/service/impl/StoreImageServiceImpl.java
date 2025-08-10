//package com.kkulddip.storeManagement.service.impl;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.kkulddip.common.exception.BusinessException;
//import com.kkulddip.common.exception.ErrorCode;
//import com.kkulddip.storeManagement.dto.response.StoreImageResponseDto;
//import com.kkulddip.storeManagement.entity.StoreImage;
//import com.kkulddip.storeManagement.exception.StoreManagementException;
//import com.kkulddip.storeManagement.repository.StoreImageRepository;
//import com.kkulddip.storeManagement.service.StoreImageService;
//import com.kkulddip.store.repository.StoreRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.IntStream;
//
///**
// * 가게 이미지 서비스 구현체 (Review 스타일로 단순화)
// */
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class StoreImageServiceImpl implements StoreImageService {
//
//    private final StoreImageRepository storeImageRepository;
//    private final StoreRepository storeRepository;
//    private final AmazonS3 amazonS3;
//
//    @Value("${spring.cloud.aws.s3.bucket}")
//    private String bucket;
//
//    // 상수 정의
//    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (가게는 리뷰보다 큰 이미지 허용)
//    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
//    private static final String ALLOWED_CONTENT_TYPE_PREFIX = "image/";
//    private static final int MAX_IMAGES_PER_STORE = 10; // 가게당 최대 이미지 수
//
//    @Transactional
//    @Override
//    public List<StoreImageResponseDto> addImage(Long storeId, List<MultipartFile> images, int deleteImageCount) {
//        try {
//            // 입력값 검증
//            validateAddImageRequest(storeId, images);
//
//            // 현재 이미지 수 체크
//            validateImageCount(storeId, images.size(), deleteImageCount);
//
//            List<String> uploadedFileNames = new ArrayList<>();
//            int initialOrder = storeImageRepository.findMaxUploadOrderByStoreId(storeId).orElse(0);
//
//            List<StoreImageResponseDto> result = IntStream.range(0, images.size())
//                .mapToObj(i -> processImageUpload(images.get(i), storeId, initialOrder + i + 1, uploadedFileNames))
//                .toList();
//
//            log.info("가게 이미지 업로드 완료. storeId: {}, uploadCount: {}", storeId, images.size());
//            return result;
//
//        } catch (BusinessException e) {
//            log.error("가게 이미지 업로드 실패 - 비즈니스 에러: {}", e.getMessage(), e);
//            throw e;
//        } catch (Exception e) {
//            log.error("가게 이미지 업로드 실패 - 시스템 에러. storeId: {}", storeId, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
//        }
//    }
//
//    private StoreImageResponseDto processImageUpload(MultipartFile file, Long storeId, int order, List<String> uploadedFileNames) {
//        try {
//            validateImageFile(file);
//
//            String originalFilename = file.getOriginalFilename();
//            String extension = getFileExtension(originalFilename);
//            String fileName = generateUniqueFileName(extension);
//            String imageUrl = uploadToS3(fileName, file, uploadedFileNames);
//
//            StoreImage image = createStoreImage(file, imageUrl, storeId, order);
//            StoreImage savedImage = storeImageRepository.save(image);
//
//            log.debug("가게 이미지 업로드 성공. fileName: {}, storeId: {}", fileName, storeId);
//            return getStoreImageResponseDto(savedImage);
//
//        } catch (BusinessException e) {
//            cleanupS3Files(uploadedFileNames);
//            throw e;
//        } catch (Exception e) {
//            cleanupS3Files(uploadedFileNames);
//            log.error("가게 이미지 처리 실패. storeId: {}", storeId, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다.");
//        }
//    }
//
//    @Transactional
//    @Override
//    public void deleteImages(Long storeId, List<Long> deleteImageIds) {
//        try {
//            validateDeleteImageRequest(storeId, deleteImageIds);
//
//            if (deleteImageIds.isEmpty()) {
//                log.info("삭제할 이미지가 없습니다. storeId: {}", storeId);
//                return;
//            }
//
//            List<StoreImage> deletedImageList = storeImageRepository.findByStoreImgIdIn(deleteImageIds);
//
//            if (deletedImageList.isEmpty()) {
//                log.warn("삭제할 이미지를 찾을 수 없습니다. storeId: {}, imageIds: {}", storeId, deleteImageIds);
//                return;
//            }
//
//            // 중복 제거
//            List<Long> uniqueImageIds = deleteImageIds.stream().distinct().toList();
//
//            List<String> fileNames = deletedImageList.stream()
//                .map(StoreImage::getImageUrl)
//                .map(this::extractFileNameFromUrl)
//                .filter(fileName -> !fileName.isEmpty())
//                .toList();
//
//            // S3에서 삭제
//            List<String> failedFiles = cleanupS3FilesWithResult(fileNames);
//
//            // S3 삭제 실패 시 경고 로그
//            if (!failedFiles.isEmpty()) {
//                log.warn("일부 S3 파일 삭제 실패. 수동 정리 필요: {}", failedFiles);
//            }
//
//            // DB에서 삭제
//            storeImageRepository.deleteAll(deletedImageList);
//            log.info("DB에서 가게 이미지 삭제 완료. storeId: {}, imageIds: {}", storeId, deleteImageIds);
//
//        } catch (BusinessException e) {
//            log.error("가게 이미지 삭제 실패 - 비즈니스 에러: {}", e.getMessage(), e);
//            throw e;
//        } catch (Exception e) {
//            log.error("가게 이미지 삭제 실패 - 시스템 에러. storeId: {}", storeId, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다.");
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<StoreImageResponseDto> getStoreImages(Long storeId) {
//        List<StoreImage> images = storeImageRepository.findByStoreIdOrderByUploadOrderAsc(storeId);
//        return getImageDtoList(images);
//    }
//
//    @Override
//    public void deleteImagesBeforeDeleteStore(Long storeId) {
//        List<StoreImage> images = storeImageRepository.findByStoreIdOrderByUploadOrderAsc(storeId);
//
//        if (images.isEmpty()) {
//            log.info("삭제할 가게 이미지가 없습니다. storeId: {}", storeId);
//            return;
//        }
//
//        try {
//            List<Long> deleteImageIds = images.stream()
//                .map(StoreImage::getStoreImgId)
//                .toList();
//
//            deleteImages(storeId, deleteImageIds);
//            log.info("가게 삭제 전 이미지 정리 완료. storeId: {}, imageCount: {}", storeId, images.size());
//
//        } catch (Exception e) {
//            log.error("가게 삭제 전 이미지 정리 실패. storeId: {}", storeId, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "가게 이미지 삭제에 실패하여 가게를 삭제할 수 없습니다.");
//        }
//    }
//
//    // ================== 검증 메서드 ==================
//
//    private void validateImageFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "빈 파일은 업로드할 수 없습니다.");
//        }
//
//        if (file.getSize() > MAX_FILE_SIZE) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
//                String.format("파일 크기는 %dMB를 초과할 수 없습니다.", MAX_FILE_SIZE / (1024 * 1024)));
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
//                "지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", ALLOWED_EXTENSIONS));
//        }
//
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.startsWith(ALLOWED_CONTENT_TYPE_PREFIX)) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "이미지 파일만 업로드 가능합니다.");
//        }
//    }
//
//    private void validateAddImageRequest(Long storeId, List<MultipartFile> images) {
//        if (storeId == null || storeId <= 0) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 가게 ID입니다.");
//        }
//
//        // 가게 존재 확인
//        if (!storeRepository.existsById(storeId)) {
//            throw StoreManagementException.storeNotFound(storeId);
//        }
//
//        if (images == null || images.isEmpty()) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "업로드할 이미지가 없습니다.");
//        }
//
//        if (images.size() > MAX_IMAGES_PER_STORE) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
//                String.format("한 번에 최대 %d개의 이미지만 업로드 가능합니다.", MAX_IMAGES_PER_STORE));
//        }
//    }
//
//    private void validateImageCount(Long storeId, int newImageCount, int deleteImageCount) {
//        int currentImageCount = storeImageRepository.countByStoreId(storeId);
//        if (currentImageCount + newImageCount - deleteImageCount > MAX_IMAGES_PER_STORE) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
//                String.format("가게에 현재 %d개의 이미지가 있어 %d개 이미지 등록은 불가능합니다.", currentImageCount, newImageCount));
//        }
//    }
//
//    private void validateDeleteImageRequest(Long storeId, List<Long> deleteImageIds) {
//        if (storeId == null || storeId <= 0) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 가게 ID입니다.");
//        }
//
//        if (deleteImageIds == null) {
//            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "삭제할 이미지 ID 목록이 필요합니다.");
//        }
//
//        List<StoreImage> images = storeImageRepository.findAllById(deleteImageIds);
//        for (StoreImage image : images) {
//            if (!image.getStoreId().equals(storeId)) {
//                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "삭제할 수 없는 이미지가 포함되어 있습니다.");
//            }
//        }
//    }
//
//    // ================== 유틸리티 메서드 ==================
//
//    private String uploadToS3(String fileName, MultipartFile file, List<String> uploadedFileNames) {
//        try {
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentLength(file.getSize());
//            metadata.setContentType(file.getContentType());
//
//            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
//            uploadedFileNames.add(fileName);
//
//            return amazonS3.getUrl(bucket, fileName).toString();
//
//        } catch (IOException e) {
//            log.error("S3 업로드 실패. fileName: {}", fileName, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다.");
//        } catch (Exception e) {
//            log.error("S3 업로드 중 예상치 못한 오류. fileName: {}", fileName, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
//        }
//    }
//
//    private String generateUniqueFileName(String extension) {
//        return "store/" + UUID.randomUUID().toString() + "." + extension;
//    }
//
//    private String getFileExtension(String originalName) {
//        if (originalName == null || !originalName.contains(".")) {
//            return "";
//        }
//        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
//    }
//
//    private boolean isValidImageExtension(String filename) {
//        String extension = getFileExtension(filename).toLowerCase();
//        return ALLOWED_EXTENSIONS.contains(extension);
//    }
//
//    private String extractFileNameFromUrl(String imageUrl) {
//        try {
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                return "";
//            }
//
//            int lastSlashIndex = imageUrl.lastIndexOf('/');
//            if (lastSlashIndex == -1 || lastSlashIndex == imageUrl.length() - 1) {
//                return "";
//            }
//
//            return imageUrl.substring(lastSlashIndex + 1);
//        } catch (Exception e) {
//            log.warn("URL에서 파일명 추출 실패: {}", imageUrl, e);
//            return "";
//        }
//    }
//
//    private List<String> cleanupS3FilesWithResult(List<String> fileNames) {
//        List<String> failedFiles = new ArrayList<>();
//        int successCount = 0;
//
//        for (String fileName : fileNames) {
//            try {
//                amazonS3.deleteObject(bucket, fileName);
//                successCount++;
//                log.debug("S3 파일 삭제 성공: {}", fileName);
//            } catch (Exception e) {
//                log.warn("S3 파일 삭제 실패: {}", fileName, e);
//                failedFiles.add(fileName);
//            }
//        }
//
//        log.info("S3 파일 삭제 완료. 성공: {}, 실패: {}", successCount, failedFiles.size());
//        return failedFiles;
//    }
//
//    private void cleanupS3Files(List<String> fileNames) {
//        cleanupS3FilesWithResult(fileNames);
//    }
//
//    private StoreImage createStoreImage(MultipartFile file, String imageUrl, Long storeId, int order) {
//        return StoreImage.builder()
//            .imageUrl(imageUrl)
//            .originalName(file.getOriginalFilename())
//            .fileSize(file.getSize())
//            .uploadOrder(order)
//            .storeId(storeId)
//            .build();
//    }
//
//    @Override
//    public StoreImageResponseDto getStoreImageResponseDto(StoreImage image) {
//        return StoreImageResponseDto.from(image);
//    }
//
//    @Override
//    public List<StoreImageResponseDto> getImageDtoList(List<StoreImage> images) {
//        if (images == null || images.isEmpty()) {
//            return new ArrayList<>();
//        }
//        return images.stream().map(this::getStoreImageResponseDto).toList();
//    }
//}