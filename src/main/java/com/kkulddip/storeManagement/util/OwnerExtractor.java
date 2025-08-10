package com.kkulddip.storeManagement.util;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 현재 인증된 사장님의 정보를 추출하는 유틸리티 클래스
 */
@Component
public class OwnerExtractor {

    private static OwnerRepository ownerRepository;

    @Autowired
    public OwnerExtractor(OwnerRepository ownerRepository) {
        OwnerExtractor.ownerRepository = ownerRepository;
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
        
        if (!(principal instanceof JwtUserInfo userInfo)) {
            throw new IllegalStateException("JWT 인증 정보가 없습니다.");
        }
        
        if (!UserRole.OWNER.name().equals(userInfo.role())) {
            throw new IllegalStateException("사장님만 접근할 수 있습니다.");
        }
        
        // OAuth2 Provider와 Provider ID로 Owner를 찾음
        try {
            OAuth2Provider provider = OAuth2Provider.valueOf(userInfo.oauth2Provider().toUpperCase());
            Owner owner = ownerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.oauth2ProviderId())
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 사장님입니다."));
            
            return owner.getOwnerId();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("지원하지 않는 OAuth2 제공자입니다: " + userInfo.oauth2Provider());
        }
    }

}