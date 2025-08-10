package com.kkulddip.storeManagement.util;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 현재 인증된 사장님의 정보를 추출하는 유틸리티 클래스
 */
public final class OwnerExtractor {

    private OwnerExtractor() {
        // Utility class - prevent instantiation
    }

    /**
     * 현재 인증된 사용자가 사장님인지 확인하고 ID를 반환
     * 
     * @return 사장님 ID
     * @throws IllegalStateException 인증되지 않았거나 사장님이 아닌 경우
     */
    public static Long getCurrentOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof JwtUserInfo)) {
            throw new IllegalStateException("JWT 인증 정보가 없습니다.");
        }
        
        JwtUserInfo userInfo = (JwtUserInfo) principal;
        
        if (!"OWNER".equals(userInfo.role())) {
            throw new IllegalStateException("사장님만 접근할 수 있습니다.");
        }
        
        // JwtUserInfo에서 사장님 ID 추출
        // 실제 구현에서는 JwtUserInfo에 ownerId 필드가 있어야 함
        // 현재는 OAuth2ProviderId를 사용한다고 가정
        try {
            return Long.parseLong(userInfo.oauth2ProviderId());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("유효하지 않은 사장님 ID입니다.");
        }
    }

    /**
     * 현재 인증된 사용자의 이메일을 반환
     */
    public static String getCurrentOwnerEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (!(principal instanceof JwtUserInfo)) {
            throw new IllegalStateException("JWT 인증 정보가 없습니다.");
        }
        
        JwtUserInfo userInfo = (JwtUserInfo) principal;
        return userInfo.email();
    }

    /**
     * 현재 인증된 사용자가 사장님인지 확인
     */
    public static boolean isCurrentUserOwner() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            Object principal = authentication.getPrincipal();
            
            if (!(principal instanceof JwtUserInfo)) {
                return false;
            }
            
            JwtUserInfo userInfo = (JwtUserInfo) principal;
            return "OWNER".equals(userInfo.role());
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 현재 사용자가 특정 가게의 소유자인지 확인
     * (서비스 레이어에서 추가 검증과 함께 사용)
     */
    public static boolean isOwnerOf(Long storeId, Long ownerId) {
        if (storeId == null || ownerId == null) {
            return false;
        }
        
        try {
            Long currentOwnerId = getCurrentOwnerId();
            return currentOwnerId.equals(ownerId);
        } catch (Exception e) {
            return false;
        }
    }
}