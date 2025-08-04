package com.kkulddip.common.security.oauth2;

/**
 * OAuth2 사용자 정보 인터페이스
 * 각 OAuth2 제공자로부터 받은 사용자 정보를 표준화합니다.
 */
public interface OAuth2UserInfo {
    
    /**
     * OAuth2 제공자에서 제공하는 사용자 ID
     */
    String getId();
    
    /**
     * 사용자 이메일
     */
    String getEmail();
    
    /**
     * 사용자 이름
     */
    String getName();
    
    /**
     * 프로필 이미지 URL
     */
    String getImageUrl();
}