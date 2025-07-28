package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.owner.entity.Owner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 사용자 Principal
 * Spring Security에서 인증된 OAuth2 사용자 정보를 담는 클래스입니다.
 */
@Getter
@RequiredArgsConstructor
public class OAuth2UserPrincipal implements OAuth2User {
    
    private final Object user; // Customer 또는 Owner
    private final Map<String, Object> attributes;
    
    @Override
    public String getName() {
        return getEmail();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = isCustomer() ? UserRole.CUSTOMER : UserRole.OWNER;
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.getAuthority())
        );
    }
    
    /**
     * 사용자 ID 반환
     */
    public Long getUserId() {
        if (user instanceof Customer) {
            return ((Customer) user).getId();
        } else if (user instanceof Owner) {
            return ((Owner) user).getId();
        }
        throw new IllegalStateException("Unknown user type");
    }
    
    /**
     * 사용자 이메일 반환
     */
    public String getEmail() {
        if (user instanceof Customer) {
            return ((Customer) user).getEmail();
        } else if (user instanceof Owner) {
            return ((Owner) user).getEmail();
        }
        throw new IllegalStateException("Unknown user type");
    }
    
    /**
     * 사용자 이름 반환
     */
    public String getUserName() {
        if (user instanceof Customer) {
            return ((Customer) user).getName();
        } else if (user instanceof Owner) {
            return ((Owner) user).getName();
        }
        throw new IllegalStateException("Unknown user type");
    }
    
    /**
     * 프로필 이미지 URL 반환
     */
    public String getProfileImageUrl() {
        if (user instanceof Customer) {
            return ((Customer) user).getProfileImageUrl();
        } else if (user instanceof Owner) {
            return ((Owner) user).getProfileImageUrl();
        }
        throw new IllegalStateException("Unknown user type");
    }
    
    /**
     * 사용자 역할 반환
     */
    public String getRole() {
        return isCustomer() ? UserRole.CUSTOMER.getAuthority() : UserRole.OWNER.getAuthority();
    }
    
    /**
     * Customer 여부 확인
     */
    public boolean isCustomer() {
        return user instanceof Customer;
    }
    
    /**
     * Owner 여부 확인
     */
    public boolean isOwner() {
        return user instanceof Owner;
    }
}