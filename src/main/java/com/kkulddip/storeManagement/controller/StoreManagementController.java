package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import com.kkulddip.storeManagement.service.StoreManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        
        // JWT에서 사장님 ID 추출 (실제 구현에서는 AuthenticationPrincipal 사용)
        Long ownerId = getCurrentOwnerId();
        
        log.info("가게 생성 요청 - ownerId: {}, storeName: {}", ownerId, request.storeName());
        
        StoreManagementResponse response = storeManagementService.createStore(request, ownerId);
        
        log.info("가게 생성 완료 - storeId: {}, ownerId: {}", response.storeId(), ownerId);
        return ApiResponse.of(201, response);
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<StoreManagementResponse> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreRequest request) {
        
        Long ownerId = getCurrentOwnerId();
        
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
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("가게 상태 변경 요청 - storeId: {}, isActive: {}, ownerId: {}", 
            storeId, request.isActive(), ownerId);
        
        StoreManagementResponse response = storeManagementService.updateStoreStatus(storeId, request, ownerId);
        
        log.info("가게 상태 변경 완료 - storeId: {}, isActive: {}", storeId, response.isActive());
        return ApiResponse.of(response);
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> deleteStore(@PathVariable Long storeId) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("가게 삭제 요청 - storeId: {}, ownerId: {}", storeId, ownerId);
        
        storeManagementService.deleteStore(storeId, ownerId);
        
        log.info("가게 삭제 완료 - storeId: {}", storeId);
        return ApiResponse.of();
    }

    /**
     * 현재 인증된 사장님의 ID를 가져옵니다.
     * 실제 구현에서는 @AuthenticationPrincipal JwtUserInfo userInfo를 사용하여
     * userInfo에서 사장님 ID를 추출해야 합니다.
     */
    private Long getCurrentOwnerId() {
        // TODO: 실제 구현에서는 JWT에서 사장님 ID 추출
        // 예시: @AuthenticationPrincipal JwtUserInfo userInfo 매개변수 사용
        // return userInfo.getOwnerId();
        
        // 임시 하드코딩 (실제로는 JWT에서 추출)
        return 1L;
    }
}