package com.kkulddip.common.controller;

import com.kkulddip.common.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 필터 및 보안 설정 테스트를 위한 컨트롤러
 * 애플리케이션 기본 동작과 JWT 인증 필터가 정상적으로 작동하는지 확인하는 용도로 사용합니다.
 */
@RestController
@RequiredArgsConstructor
public class TestController {
    
    private final JwtUtil jwtUtil;
    
    /**
     * 기본 홈 페이지 엔드포인트
     * 애플리케이션이 정상적으로 실행되고 있는지 확인하는 용도입니다.
     * 
     * @return 환영 메시지
     */
    @GetMapping("/")
    public String home() {
        return "Hello World!";
    }
    
    /**
     * JWT 필터 동작 테스트 엔드포인트 (인증 필요)
     * JWT 인증 필터가 정상적으로 작동하는지 확인하는 용도입니다.
     * 
     * @return JWT 필터 동작 확인 메시지
     */
    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public String test() {
        return "JWT Filter is working!";
    }
    
    /**
     * 고객 전용 테스트 엔드포인트
     * CUSTOMER 역할을 가진 사용자만 접근 가능합니다.
     * 
     * @return 고객 전용 메시지
     */
    @GetMapping("/test/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String testCustomer() {
        return "Customer access granted!";
    }
    
    /**
     * 사장 전용 테스트 엔드포인트
     * OWNER 역할을 가진 사용자만 접근 가능합니다.
     * 
     * @return 사장 전용 메시지
     */
    @GetMapping("/test/owner")
    @PreAuthorize("hasRole('OWNER')")
    public String testOwner() {
        return "Owner access granted!";
    }
    
    /**
     * 관리자 전용 테스트 엔드포인트
     * ADMIN 역할을 가진 사용자만 접근 가능합니다.
     * 
     * @return 관리자 전용 메시지
     */
    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdmin() {
        return "Admin access granted!";
    }
    
    /**
     * 고객 또는 사장 접근 가능한 테스트 엔드포인트
     * CUSTOMER 또는 OWNER 역할을 가진 사용자만 접근 가능합니다.
     * 
     * @return 고객/사장 접근 허용 메시지
     */
    @GetMapping("/test/customer-or-owner")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OWNER')")
    public String testCustomerOrOwner() {
        return "Customer or Owner access granted!";
    }
    
    /**
     * JWT 토큰 생성 테스트 엔드포인트
     * 테스트용 JWT 토큰을 생성하여 반환합니다.
     * 
     * @param username 사용자명 (기본값: testuser)
     * @param role 사용자 역할 (기본값: CUSTOMER, 가능한 값: ADMIN, CUSTOMER, OWNER)
     * @return 생성된 액세스 토큰과 리프레시 토큰
     */
    @GetMapping("/auth/token/generate")
    public Map<String, String> generateToken(
        @RequestParam(defaultValue = "testuser") String username,
        @RequestParam(defaultValue = "CUSTOMER") String role
    ) {
        String accessToken = jwtUtil.generateAccessToken(username, role);
        String refreshToken = jwtUtil.generateRefreshToken(username, role);
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("tokenType", "Bearer");
        tokens.put("username", username);
        tokens.put("role", role);
        
        return tokens;
    }
    
    /**
     * JWT 토큰 검증 테스트 엔드포인트
     * 제공된 토큰을 검증하고 토큰 정보를 반환합니다.
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰에서 추출한 사용자 정보
     */
    @GetMapping("/auth/token/verify")
    public Map<String, Object> verifyToken(@RequestParam String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            boolean isExpired = jwtUtil.isTokenExpired(token);
            
            result.put("valid", true);
            result.put("username", username);
            result.put("role", role);
            result.put("expired", isExpired);
        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}