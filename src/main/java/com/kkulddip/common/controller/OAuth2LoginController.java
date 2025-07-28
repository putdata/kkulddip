package com.kkulddip.common.controller;

import com.kkulddip.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 컨트롤러
 * Customer와 Owner를 위한 별도의 OAuth2 로그인 엔드포인트를 제공합니다.
 */
@Tag(name = "OAuth2 Login", description = "역할별 OAuth2 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2LoginController {

    /**
     * Customer용 Google OAuth2 로그인
     */
    @Operation(summary = "Customer Google 로그인", description = "고객용 Google OAuth2 로그인 시작")
    @GetMapping("/customer/google")
    public ResponseEntity<ApiResponse<Map<String, String>>> customerGoogleLogin() {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("loginUrl", "/oauth2/authorization/google-customer");
        loginInfo.put("role", "CUSTOMER");
        loginInfo.put("message", "고객용 Google 로그인을 위해 위 URL로 접속하세요");
        
        return ResponseEntity.ok(ApiResponse.of(loginInfo));
    }

    /**
     * Owner용 Google OAuth2 로그인
     */
    @Operation(summary = "Owner Google 로그인", description = "사장용 Google OAuth2 로그인 시작")
    @GetMapping("/owner/google")
    public ResponseEntity<ApiResponse<Map<String, String>>> ownerGoogleLogin() {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("loginUrl", "/oauth2/authorization/google-owner");
        loginInfo.put("role", "OWNER");
        loginInfo.put("message", "사장용 Google 로그인을 위해 위 URL로 접속하세요");
        
        return ResponseEntity.ok(ApiResponse.of(loginInfo));
    }

    /**
     * 로그인 옵션 목록
     */
    @Operation(summary = "로그인 옵션", description = "사용 가능한 로그인 옵션 목록")
    @GetMapping("/login-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLoginOptions() {
        Map<String, Object> options = new HashMap<>();
        
        Map<String, String> customerOptions = new HashMap<>();
        customerOptions.put("google", "/api/auth/customer/google");
        
        Map<String, String> ownerOptions = new HashMap<>();
        ownerOptions.put("google", "/api/auth/owner/google");
        
        options.put("customer", customerOptions);
        options.put("owner", ownerOptions);
        options.put("description", "역할에 따라 적절한 로그인 옵션을 선택하세요");
        
        return ResponseEntity.ok(ApiResponse.of(options));
    }
}