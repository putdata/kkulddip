package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.domain.user.entity.User;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.storeManagement.dto.response.StoreImageResponseDto;
import com.kkulddip.storeManagement.service.StoreImageService;
import com.kkulddip.storeManagement.util.OwnerExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 가게 이미지 관리 컨트롤러
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/store-management")
@Tag(name = "Store Image Management", description = "가게 이미지 관리 API")
public class StoreImageController {

    private final StoreImageService storeImageService;
    private final StoreRepository storeRepository;

    @Operation(
        summary = "가게 이미지 업로드",
        description = "가게에 이미지를 업로드합니다."
    )
    @PostMapping(value = "/stores/{storeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<StoreImageResponseDto>> uploadImages(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable Long storeId,
        
        @Parameter(description = "업로드할 이미지 파일들", required = true)
        @RequestPart("images") List<MultipartFile> images,
        
        @Parameter(description = "삭제될 이미지 개수", required = false)
        @RequestParam(defaultValue = "0") int deleteImageCount,
        
        @AuthenticationPrincipal User user) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        // 소유권 검증
        validateStoreOwnership(storeId, ownerId);
        
        log.info("가게 이미지 업로드 요청 - storeId: {}, imageCount: {}, ownerId: {}", 
            storeId, images.size(), ownerId);

        List<StoreImageResponseDto> response = storeImageService.addImage(storeId, images, deleteImageCount);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "가게 이미지 조회",
        description = "가게의 모든 이미지를 조회합니다."
    )
    @GetMapping("/stores/{storeId}/images")
    public ResponseEntity<List<StoreImageResponseDto>> getStoreImages(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable Long storeId) {
        
        log.info("가게 이미지 조회 요청 - storeId: {}", storeId);

        List<StoreImageResponseDto> response = storeImageService.getStoreImages(storeId);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "가게 이미지 삭제",
        description = "지정된 이미지들을 삭제합니다."
    )
    @DeleteMapping("/stores/{storeId}/images")
    public ResponseEntity<Void> deleteImages(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable Long storeId,
        
        @Parameter(description = "삭제할 이미지 ID 목록", required = true)
        @RequestParam List<Long> imageIds,
        
        @AuthenticationPrincipal User user) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        // 소유권 검증
        validateStoreOwnership(storeId, ownerId);
        
        log.info("가게 이미지 삭제 요청 - storeId: {}, imageIds: {}, ownerId: {}", 
            storeId, imageIds, ownerId);

        storeImageService.deleteImages(storeId, imageIds);

        return ResponseEntity.noContent().build();
    }
    
    /**
     * 가게 소유권 검증
     */
    private void validateStoreOwnership(Long storeId, Long ownerId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_MANAGEMENT_NOT_FOUND, "가게를 찾을 수 없습니다."));
            
        if (!store.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.STORE_MANAGEMENT_NOT_OWNED, "해당 가게의 소유자가 아닙니다.");
        }
    }
}