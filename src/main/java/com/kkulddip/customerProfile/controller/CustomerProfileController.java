package com.kkulddip.customerProfile.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.CustomerProfileResponse;
import com.kkulddip.customerProfile.dto.response.CustomerStatsResponse;
import com.kkulddip.customerProfile.dto.response.UpdateLocationResponse;
import com.kkulddip.customerProfile.dto.response.UpdateProfileResponse;
import com.kkulddip.customerProfile.service.CustomerProfileService;
import com.kkulddip.customerProfile.location.service.CustomerLocationService;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import com.kkulddip.customerProfile.location.dto.LocationDistanceResponse;
import com.kkulddip.customerProfile.location.dto.UpdateRealtimeLocationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 고객 프로필 관리 컨트롤러
 * 
 * 고객의 프로필 정보, 위치 정보, 통계 정보를 관리하는 REST API를 제공합니다.
 * JWT 토큰을 통해 인증된 고객만 자신의 정보에 접근할 수 있습니다.
 * 
 * 주요 기능:
 * - 프로필 조회/수정 (이름, 프로필 이미지)
 * - 위치 정보 관리 (주소, 좌표)
 * - 실시간 위치 추적 (Redis 기반)
 * - 고객 통계 및 레벨 시스템
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/customers")
public class CustomerProfileController implements CustomerProfileApi {
    
    private final CustomerProfileService customerProfileService;
    private final CustomerLocationService customerLocationService;
    
    @Override
    @GetMapping("/profile")
    public ApiResponse<CustomerProfileResponse> getMyProfile() {
        Long customerId = getCurrentCustomerId();
        log.info("프로필 조회 요청 - customerId: {}", customerId);
        
        CustomerProfileResponse response = customerProfileService.getProfileWithUpdatedStats(customerId);
        return ApiResponse.of(response);
    }
    
    @Override
    @PutMapping("/profile")
    public ApiResponse<UpdateProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        Long customerId = getCurrentCustomerId();
        log.info("프로필 수정 요청 - customerId: {}, name: {}", customerId, request.name());
        
        UpdateProfileResponse response = customerProfileService.updateProfile(customerId, request);
        return ApiResponse.of(response);
    }
    
    @Override
    @PutMapping("/location")
    public ApiResponse<UpdateLocationResponse> updateLocation(
            @Valid @RequestBody UpdateLocationRequest request) {
        Long customerId = getCurrentCustomerId();
        log.info("위치 정보 업데이트 요청 - customerId: {}, address: {}", 
            customerId, request.address());
        
        UpdateLocationResponse response = customerProfileService.updateLocation(customerId, request);
        return ApiResponse.of(response);
    }
    
    @Override
    @GetMapping("/stats")
    public ApiResponse<CustomerStatsResponse> getMyStats() {
        Long customerId = getCurrentCustomerId();
        log.info("통계 조회 요청 - customerId: {}", customerId);
        
        CustomerStatsResponse response = customerProfileService.getUpdatedStats(customerId);
        return ApiResponse.of(response);
    }
    
    /**
     * 현재 인증된 고객의 ID를 추출합니다.
     * 
     * SecurityContextHolder에서 JWT 인증 정보를 가져와서
     * 고객 ID를 Long 타입으로 변환하여 반환합니다.
     * 
     * @return 현재 로그인한 고객의 ID
     * @throws IllegalStateException 인증 정보가 없거나 올바르지 않은 경우
     */
    private Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserInfo) {
            JwtUserInfo userInfo = (JwtUserInfo) authentication.getPrincipal();
            return Long.parseLong(userInfo.userId());
        }
        
        throw new IllegalStateException("인증 정보를 찾을 수 없습니다");
    }
    
    @Override
    @PostMapping("/location/realtime")
    public ApiResponse<CustomerLocationDto> updateRealtimeLocation(
            @Valid @RequestBody UpdateRealtimeLocationRequest request) {
        Long customerId = getCurrentCustomerId();
        log.info("실시간 위치 업데이트 요청 - customerId: {}, lat: {}, lng: {}", 
            customerId, request.latitude(), request.longitude());
        
        CustomerLocationDto response = customerLocationService.updateRealtimeLocation(customerId, request);
        return ApiResponse.of(response);
    }
    
    @Override
    @GetMapping("/location/realtime")
    public ApiResponse<CustomerLocationDto> getRealtimeLocation() {
        Long customerId = getCurrentCustomerId();
        log.info("실시간 위치 조회 요청 - customerId: {}", customerId);
        
        CustomerLocationDto location = customerLocationService.getCustomerLocation(customerId)
            .orElseThrow(() -> new IllegalArgumentException("위치 정보를 찾을 수 없습니다"));
        
        return ApiResponse.of(location);
    }
    
    @Override
    @GetMapping("/location/distance/store/{storeId}")
    public ApiResponse<LocationDistanceResponse> calculateDistanceToStore(@PathVariable Long storeId) {
        Long customerId = getCurrentCustomerId();
        log.info("가게까지 거리 계산 요청 - customerId: {}, storeId: {}", customerId, storeId);
        
        LocationDistanceResponse response = customerLocationService.calculateDistanceToStore(customerId, storeId);
        return ApiResponse.of(response);
    }
    
    @Override
    @DeleteMapping("/location/realtime")
    public ApiResponse<Void> stopLocationSharing() {
        Long customerId = getCurrentCustomerId();
        log.info("위치 공유 중지 요청 - customerId: {}", customerId);
        
        customerLocationService.stopLocationSharing(customerId);
        return ApiResponse.of();
    }
}