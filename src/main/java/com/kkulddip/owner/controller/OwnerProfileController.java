package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.UpdateOwnerProfileRequest;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.service.OwnerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/owners")
public class OwnerProfileController implements OwnerProfileApi {

    private final OwnerProfileService ownerProfileService;

    @GetMapping("/profile")
    @Override
    public ApiResponse<OwnerProfileResponse> getOwnerProfile(
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        log.info("Owner 프로필 조회 요청 - userId: {}", userInfo.userId());
        
        Long ownerId = Long.parseLong(userInfo.userId());
        OwnerProfileResponse response = ownerProfileService.getOwnerProfile(ownerId);
        
        // 마지막 활동 시간 업데이트
        ownerProfileService.updateLastActiveAt(ownerId);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }

    @PutMapping("/profile")
    @Override
    public ApiResponse<OwnerProfileResponse> updateOwnerProfile(
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @Valid @RequestBody UpdateOwnerProfileRequest request) {
        
        log.info("Owner 프로필 수정 요청 - userId: {}, name: {}", userInfo.userId(), request.name());
        
        Long ownerId = Long.parseLong(userInfo.userId());
        OwnerProfileResponse response = ownerProfileService.updateOwnerProfile(ownerId, request);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }
}