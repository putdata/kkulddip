package com.kkulddip.common.security.handler;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 역할 기반 접근 제어를 위한 유틸리티 클래스
 * Spring Security의 SecurityContext에서 사용자 정보를 추출하고
 * 컨트롤러나 서비스에서 특정 역할 검증이 필요할 때 사용합니다.
 * ADMIN, CUSTOMER, OWNER 세 가지 역할을 지원합니다.
 */
@Slf4j
@Component
public class RoleBasedAccessHandler {

    /**
     * 현재 인증된 사용자의 역할을 반환
     * SecurityContext에서 인증 정보를 추출하여 UserRole enum으로 변환합니다.
     * 
     * @return 현재 사용자의 역할 (ADMIN, CUSTOMER, OWNER 중 하나)
     * @throws IllegalStateException 인증되지 않은 사용자이거나 유효하지 않은 역할인 경우
     */
    public UserRole getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHENTICATED_USER);
        }
        
        return authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(role -> role.startsWith("ROLE_"))
            .map(role -> role.substring(5)) // "ROLE_" 제거
            .map(UserRole::valueOf)
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_USER_ROLE));
    }
    
    /**
     * 현재 사용자가 특정 역할인지 확인
     * 예외 발생 시 false를 반환하여 안전하게 처리합니다.
     * 
     * @param role 확인할 사용자 역할
     * @return 해당 역할을 가지고 있으면 true, 그렇지 않으면 false
     */
    public boolean hasRole(UserRole role) {
        try {
            return getCurrentUserRole() == role;
        } catch (Exception e) {
            log.warn("역할 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 현재 사용자가 고객 역할인지 확인
     * 
     * @return 고객 역할이면 true, 그렇지 않으면 false
     */
    public boolean isCustomer() {
        return hasRole(UserRole.CUSTOMER);
    }
    
    /**
     * 현재 사용자가 사장 역할인지 확인
     * 
     * @return 사장 역할이면 true, 그렇지 않으면 false
     */
    public boolean isOwner() {
        return hasRole(UserRole.OWNER);
    }
    
    /**
     * 현재 사용자가 관리자 역할인지 확인
     * 
     * @return 관리자 역할이면 true, 그렇지 않으면 false
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }
    
    /**
     * 현재 인증된 사용자의 이름(username) 반환
     * JWT 토큰의 subject 클레임에서 추출한 사용자명을 반환합니다.
     * 
     * @return 현재 사용자의 사용자명
     * @throws IllegalStateException 인증되지 않은 사용자인 경우
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHENTICATED_USER);
        }
        
        return authentication.getName();
    }
    
    /**
     * 특정 역할이 필요한 작업 수행 전 권한 검증
     * 현재 사용자의 역할이 요구되는 역할과 일치하지 않으면 AccessDeniedException을 발생시킵니다.
     * 
     * @param requiredRole 필요한 사용자 역할
     * @throws AccessDeniedException 현재 사용자가 요구되는 역할을 가지지 않은 경우
     */
    public void requireRole(UserRole requiredRole) {
        UserRole currentRole = getCurrentUserRole();
        
        if (currentRole != requiredRole) {
            throw new AccessDeniedException(
                String.format("접근 권한이 없습니다. 필요한 역할: %s, 현재 역할: %s", 
                    requiredRole.getDescription(), currentRole.getDescription())
            );
        }
    }
    
    /**
     * 고객 또는 사장 역할이 필요한 작업 수행 전 권한 검증
     * 주문 관련 기능 등에서 고객과 사장 모두 접근 가능한 경우 사용합니다.
     * 
     * @throws AccessDeniedException 현재 사용자가 고객이나 사장 역할이 아닌 경우
     */
    public void requireCustomerOrOwner() {
        UserRole currentRole = getCurrentUserRole();
        
        if (currentRole != UserRole.CUSTOMER && currentRole != UserRole.OWNER) {
            throw new AccessDeniedException("고객 또는 사장 권한이 필요합니다.");
        }
    }
}