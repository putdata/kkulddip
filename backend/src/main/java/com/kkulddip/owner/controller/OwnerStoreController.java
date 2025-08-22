package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.StoreListResponse;
import com.kkulddip.owner.service.OwnerStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/owners")
public class OwnerStoreController implements OwnerStoreApi {

    private final OwnerStoreService ownerStoreService;

    @GetMapping("/stores")
    @Override
    public ApiResponse<StoreListResponse> getOwnerStores(
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @RequestParam(required = false) Boolean activeOnly) {
        
        log.info("Owner 가게 목록 조회 요청 - userId: {}, activeOnly: {}", userInfo.userId(), activeOnly);
        
        Long ownerId = Long.parseLong(userInfo.userId());
        StoreListResponse response = ownerStoreService.getOwnerStores(ownerId, activeOnly);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }

    @GetMapping("/stores/{storeId}")
    @Override
    public ApiResponse<OwnerStoreResponse> getOwnerStore(
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @PathVariable Long storeId) {
        
        log.info("Owner 특정 가게 조회 요청 - userId: {}, storeId: {}", userInfo.userId(), storeId);
        
        Long ownerId = Long.parseLong(userInfo.userId());
        OwnerStoreResponse response = ownerStoreService.getOwnerStore(ownerId, storeId);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }
}