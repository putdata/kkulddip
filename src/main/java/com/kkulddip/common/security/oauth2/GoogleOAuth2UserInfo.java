package com.kkulddip.common.security.oauth2;

import java.util.Map;
import java.util.Optional;

/**
 * Google OAuth2 사용자 정보 구현체
 * Google에서 제공하는 사용자 정보 형식에 맞춰 구현합니다.
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    /**
     * Google OAuth2UserInfo 생성자
     *
     * @param attributes Google OAuth2에서 제공받은 사용자 속성 정보
     */
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Google 사용자의 고유 ID를 반환합니다.
     *
     * @return Google 사용자 ID
     */
    @Override
    public String getId() {
        return Optional.ofNullable(attributes.get("id"))
            .map(Object::toString)
            .orElse(null);
    }

    /**
     * Google 사용자의 이메일 주소를 반환합니다.
     *
     * @return 사용자 이메일 주소
     */
    @Override
    public String getEmail() {
        return Optional.ofNullable(attributes.get("email"))
            .map(Object::toString)
            .orElse(null);
    }

    /**
     * Google 사용자의 이름을 반환합니다.
     *
     * @return 사용자 이름
     */
    @Override
    public String getName() {
        return Optional.ofNullable(attributes.get("name"))
            .map(Object::toString)
            .orElse(null);
    }

    /**
     * Google 사용자의 프로필 이미지 URL을 반환합니다.
     *
     * @return 프로필 이미지 URL
     */
    @Override
    public String getImageUrl() {
        return Optional.ofNullable(attributes.get("picture"))
            .map(Object::toString)
            .orElse(null);
    }
}