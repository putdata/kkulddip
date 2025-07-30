package com.kkulddip.common.security.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Google OAuth2 사용자 정보 구현체
 * Google에서 제공하는 사용자 정보 형식에 맞춰 구현합니다.
 */
@Slf4j
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    
    private final Map<String, Object> attributes;
    
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        Object sub = attributes.get("id");
        return sub != null ? sub.toString() : null;
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getName() {
        Object name = attributes.get("name");
        return name != null ? name.toString() : null;
    }

    @Override
    public String getImageUrl() {
        Object picture = attributes.get("picture");
        return picture != null ? picture.toString() : null;
    }
}