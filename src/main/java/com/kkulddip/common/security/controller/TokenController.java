package com.kkulddip.common.security.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.oauth2.OAuth2TokenService;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenRequest;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 Authorization Code를 JWT 토큰으로 교환하는 컨트롤러
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "OAuth2 인증 및 토큰 관리")
public class TokenController {

    private final OAuth2TokenService oauth2TokenService;

    /**
     * OAuth2 Authorization Code를 JWT 토큰으로 교환 (고객용)
     */
    @PostMapping("/customer/token")
    @Operation(
            summary = "고객 토큰 교환",
            description = "OAuth2 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다. (고객용)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 교환 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 코드)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (만료된 코드)"
            )
    })
    public ResponseEntity<ApiResponse<OAuth2TokenResponse>> exchangeCustomerToken(
            @Valid @RequestBody OAuth2TokenRequest request) {
        return processTokenExchange(request, "CUSTOMER");
    }

    /**
     * OAuth2 Authorization Code를 JWT 토큰으로 교환 (사업자용)
     */
    @PostMapping("/owner/token")
    @Operation(
            summary = "사업자 토큰 교환",
            description = "OAuth2 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다. (사업자용)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 교환 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 코드)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (만료된 코드)"
            )
    })
    public ResponseEntity<ApiResponse<OAuth2TokenResponse>> exchangeOwnerToken(
            @Valid @RequestBody OAuth2TokenRequest request) {
        return processTokenExchange(request, "OWNER");
    }

    /**
     * 공통 토큰 교환 처리 로직
     */
    private ResponseEntity<ApiResponse<OAuth2TokenResponse>> processTokenExchange(
            OAuth2TokenRequest request, String userType) {

        log.info("{} 토큰 교환 요청 - 코드: {}", userType, request.code());

        // OAuth2 Authorization Code를 JWT 토큰으로 교환
        OAuth2TokenResponse tokenResponse = oauth2TokenService.exchangeCodeForToken(request.code(), userType);

        log.info("{} 토큰 교환 성공", userType);

        return ResponseEntity.ok(ApiResponse.of(tokenResponse));
    }
}