package com.kkulddip.common.security.controller;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.oauth2.OAuth2TokenService;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenRequest;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 Authorization Code를 JWT 토큰으로 교환하는 컨트롤러
 * 고객과 사업자 사용자 타입별로 토큰 교환 엔드포인트를 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "인증", description = "OAuth2 인증 및 토큰 관리")
public class TokenController implements TokenApi {

    private final OAuth2TokenService oauth2TokenService;

    /**
     * OAuth2 Authorization Code를 JWT 토큰으로 교환 (고객용)
     * Google OAuth2 인증을 통해 받은 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다.
     *
     * @param request OAuth2 토큰 요청 객체 (Authorization Code 포함)
     * @return JWT 토큰 정보가 담긴 응답 객체
     */
    @PostMapping("/customer/token")
    @Operation(
        summary = "고객 토큰 교환",
        description = "OAuth2 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다. (고객용)"
    )
    public ApiResponse<OAuth2TokenResponse> exchangeCustomerToken(
        @Valid @RequestBody OAuth2TokenRequest request) {
        return processTokenExchange(request, UserRole.CUSTOMER);
    }

    /**
     * OAuth2 Authorization Code를 JWT 토큰으로 교환 (사업자용)
     * Google OAuth2 인증을 통해 받은 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다.
     *
     * @param request OAuth2 토큰 요청 객체 (Authorization Code 포함)
     * @return JWT 토큰 정보가 담긴 응답 객체
     */
    @PostMapping("/owner/token")
    @Operation(
        summary = "사업자 토큰 교환",
        description = "OAuth2 Authorization Code를 JWT Access Token과 Refresh Token으로 교환합니다. (사업자용)"
    )
    public ApiResponse<OAuth2TokenResponse> exchangeOwnerToken(
        @Valid @RequestBody OAuth2TokenRequest request) {
        return processTokenExchange(request, UserRole.OWNER);
    }

    /**
     * 공통 토큰 교환 처리 로직
     * 사용자 타입에 관계없이 공통으로 사용되는 토큰 교환 처리를 수행합니다.
     *
     * @param request OAuth2 토큰 요청 객체
     * @param userRole 사용자 역할
     * @return JWT 토큰 정보가 담긴 응답 객체
     */
    private ApiResponse<OAuth2TokenResponse> processTokenExchange(
        OAuth2TokenRequest request, UserRole userRole) {

        log.info("{} 토큰 교환 요청 - 코드: {}", userRole.getAuthority(), request.code());

        OAuth2TokenResponse tokenResponse = oauth2TokenService.exchangeCodeForToken(request.code(), userRole);

        log.info("{} 토큰 교환 성공", userRole.getAuthority());

        return ApiResponse.of(tokenResponse);
    }
}