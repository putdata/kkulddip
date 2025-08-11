package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.SettlementQueryRequest;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.owner.service.OwnerSettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/owners")
public class OwnerSettlementController implements OwnerSettlementApi {

    private final OwnerSettlementService ownerSettlementService;

    @GetMapping("/stores/{storeId}/settlement")
    @Override
    public ApiResponse<SettlementResponse> getStoreSettlement(
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @PathVariable Long storeId,
        @Valid SettlementQueryRequest request) {
        
        log.info("가게 정산 조회 요청 - userId: {}, storeId: {}, year: {}, month: {}", 
            userInfo.userId(), storeId, request.year(), request.month());
        
        Long ownerId = Long.parseLong(userInfo.userId());
        SettlementResponse response = ownerSettlementService.getStoreSettlement(ownerId, storeId, request);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }

    @GetMapping("/settlement/summary")
    @Override
    public ApiResponse<SettlementSummaryResponse> getSettlementSummary(
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @Valid SettlementQueryRequest request) {
        
        log.info("전체 정산 요약 조회 요청 - userId: {}, year: {}, month: {}", 
            userInfo.userId(), request.year(), request.month());
        
        Long ownerId = Long.parseLong(userInfo.userId());
        SettlementSummaryResponse response = ownerSettlementService.getOwnerSettlementSummary(ownerId, request);
        
        return ApiResponse.of(HttpStatus.OK.value(), response);
    }
}