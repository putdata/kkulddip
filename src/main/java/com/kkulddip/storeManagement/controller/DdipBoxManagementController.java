package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.storeManagement.dto.request.CreateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxQuantityRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.DdipBoxManagementResponse;
import com.kkulddip.storeManagement.service.DdipBoxManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 띱박스 관리 컨트롤러
 * 사장님 전용 띱박스 CUD 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/store-management/stores")
public class DdipBoxManagementController implements DdipBoxManagementApi {

    private final DdipBoxManagementService ddipBoxManagementService;

    @PostMapping("/{storeId}/ddipboxes")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<DdipBoxManagementResponse> createDdipBox(
            @PathVariable Long storeId,
            @Valid @RequestBody CreateDdipBoxRequest request) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("띱박스 생성 요청 - storeId: {}, ddipboxName: {}, ownerId: {}", 
            storeId, request.ddipboxName(), ownerId);
        
        DdipBoxManagementResponse response = ddipBoxManagementService.createDdipBox(storeId, request, ownerId);
        
        log.info("띱박스 생성 완료 - ddipboxId: {}, storeId: {}", response.ddipboxId(), storeId);
        return ApiResponse.of(201, response);
    }

    @PutMapping("/{storeId}/ddipboxes/{ddipboxId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<DdipBoxManagementResponse> updateDdipBox(
            @PathVariable Long storeId,
            @PathVariable Long ddipboxId,
            @Valid @RequestBody UpdateDdipBoxRequest request) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("띱박스 수정 요청 - storeId: {}, ddipboxId: {}, ownerId: {}", 
            storeId, ddipboxId, ownerId);
        
        DdipBoxManagementResponse response = ddipBoxManagementService.updateDdipBox(
            storeId, ddipboxId, request, ownerId);
        
        log.info("띱박스 수정 완료 - ddipboxId: {}", ddipboxId);
        return ApiResponse.of(response);
    }

    @PatchMapping("/{storeId}/ddipboxes/{ddipboxId}/quantity")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<DdipBoxManagementResponse> updateDdipBoxQuantity(
            @PathVariable Long storeId,
            @PathVariable Long ddipboxId,
            @Valid @RequestBody UpdateDdipBoxQuantityRequest request) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("띱박스 재고 업데이트 요청 - storeId: {}, ddipboxId: {}, ownerId: {}", 
            storeId, ddipboxId, ownerId);
        
        DdipBoxManagementResponse response = ddipBoxManagementService.updateDdipBoxQuantity(
            storeId, ddipboxId, request, ownerId);
        
        log.info("띱박스 재고 업데이트 완료 - ddipboxId: {}, remainingQuantity: {}", 
            ddipboxId, response.remainingQuantity());
        return ApiResponse.of(response);
    }

    @PatchMapping("/{storeId}/ddipboxes/{ddipboxId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<DdipBoxManagementResponse> updateDdipBoxStatus(
            @PathVariable Long storeId,
            @PathVariable Long ddipboxId,
            @Valid @RequestBody UpdateStoreStatusRequest request) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("띱박스 상태 변경 요청 - storeId: {}, ddipboxId: {}, isActive: {}, ownerId: {}", 
            storeId, ddipboxId, request.isActive(), ownerId);
        
        DdipBoxManagementResponse response = ddipBoxManagementService.updateDdipBoxStatus(
            storeId, ddipboxId, request, ownerId);
        
        log.info("띱박스 상태 변경 완료 - ddipboxId: {}, isActive: {}", 
            ddipboxId, response.isActive());
        return ApiResponse.of(response);
    }

    @DeleteMapping("/{storeId}/ddipboxes/{ddipboxId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> deleteDdipBox(
            @PathVariable Long storeId,
            @PathVariable Long ddipboxId) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("띱박스 삭제 요청 - storeId: {}, ddipboxId: {}, ownerId: {}", 
            storeId, ddipboxId, ownerId);
        
        ddipBoxManagementService.deleteDdipBox(storeId, ddipboxId, ownerId);
        
        log.info("띱박스 삭제 완료 - ddipboxId: {}", ddipboxId);
        return ApiResponse.of();
    }

    @GetMapping("/{storeId}/ddipboxes")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<DdipBoxManagementResponse>> getDdipBoxesByStore(
            @PathVariable Long storeId) {
        
        Long ownerId = getCurrentOwnerId();
        
        log.info("가게 띱박스 목록 조회 요청 - storeId: {}, ownerId: {}", storeId, ownerId);
        
        List<DdipBoxManagementResponse> responses = ddipBoxManagementService.getDdipBoxesByStore(storeId, ownerId);
        
        log.info("가게 띱박스 목록 조회 완료 - storeId: {}, count: {}", storeId, responses.size());
        return ApiResponse.of(responses);
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