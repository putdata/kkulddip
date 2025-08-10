package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import com.kkulddip.storeManagement.service.StoreManagementService;
import com.kkulddip.storeManagement.util.OwnerExtractor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 가게 관리 컨트롤러
 * 사장님 전용 가게 CUD 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/store-management/stores")
public class StoreManagementController implements StoreManagementApi {

    private final StoreManagementService storeManagementService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> createStore(
            @Valid @RequestBody CreateStoreRequest request) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 생성 요청 - ownerId: {}, storeName: {}", ownerId, request.storeName());
        
        StoreManagementResponse response = storeManagementService.createStore(request, ownerId);
        
        log.info("가게 생성 완료 - storeId: {}, ownerId: {}", response.storeId(), ownerId);
        return ApiResponse.of(201, response);
    }

    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> createStoreWithImage(
            @RequestPart("request") @Valid CreateStoreRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 생성 요청 (이미지 포함) - ownerId: {}, storeName: {}, hasImage: {}", 
            ownerId, request.storeName(), image != null && !image.isEmpty());
        
        StoreManagementResponse response = storeManagementService.createStoreWithImage(request, image, ownerId);
        
        log.info("가게 생성 완료 (이미지 포함) - storeId: {}, ownerId: {}", response.storeId(), ownerId);
        return ApiResponse.of(201, response);
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreRequest request) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 정보 수정 요청 - storeId: {}, ownerId: {}", storeId, ownerId);
        
        StoreManagementResponse response = storeManagementService.updateStore(storeId, request, ownerId);
        
        log.info("가게 정보 수정 완료 - storeId: {}", storeId);
        return ApiResponse.of(response);
    }

    @PatchMapping("/{storeId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> updateStoreStatus(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreStatusRequest request) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 상태 변경 요청 - storeId: {}, isActive: {}, ownerId: {}", 
            storeId, request.isActive(), ownerId);
        
        StoreManagementResponse response = storeManagementService.updateStoreStatus(storeId, request, ownerId);
        
        log.info("가게 상태 변경 완료 - storeId: {}, isActive: {}", storeId, response.isActive());
        return ApiResponse.of(response);
    }

    @PutMapping(value = "/{storeId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> updateStoreImage(
            @PathVariable Long storeId,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 이미지 업데이트 요청 - storeId: {}, ownerId: {}, hasImage: {}", 
            storeId, ownerId, image != null && !image.isEmpty());
        
        StoreManagementResponse response = storeManagementService.updateStoreImage(storeId, image, ownerId);
        
        log.info("가게 이미지 업데이트 완료 - storeId: {}", storeId);
        return ApiResponse.of(response);
    }
    
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> deleteStore(@PathVariable Long storeId) {
        
        Long ownerId = OwnerExtractor.getCurrentOwnerId();
        
        log.info("가게 삭제 요청 - storeId: {}, ownerId: {}", storeId, ownerId);
        
        storeManagementService.deleteStore(storeId, ownerId);
        
        log.info("가게 삭제 완료 - storeId: {}", storeId);
        return ApiResponse.of();
    }

}