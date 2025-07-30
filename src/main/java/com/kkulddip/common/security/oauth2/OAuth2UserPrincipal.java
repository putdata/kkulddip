package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.user.entity.User;
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
    
    private final User user;
    private final Map<String, Object> attributes;
    private final String provider;

    @Override
    public String getName() {
        return user.getName();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getAuthority())
        );
    }
    
    /**
     * 사용자 ID 반환
     */
    public Long getUserId() {
        return user.getId();
    }
    
    /**
     * OAuth2 제공자 ID 반환
     */
    public String getProviderId() {
        return user.getOauth2ProviderId();
    }
    
    /**
     * 사용자 이메일 반환
     */
    public String getEmail() {
        return user.getEmail();
    }
    
    /**
     * 사용자 이름 반환
     */
    public String getUserName() {
        return user.getName();
    }
    
    /**
     * 프로필 이미지 URL 반환
     */
    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }
    
    /**
     * 사용자 역할 반환
     */
    public String getRole() {
        return user.getRole().getAuthority();
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

    /**
     * OAuth2UserPrincipal 생성
     */
    public static OAuth2UserPrincipal create(User user, Map<String, Object> attributes, String provider) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (attributes == null) {
            attributes = Collections.emptyMap();
        }

        return new OAuth2UserPrincipal(user, attributes, provider);
    }
}