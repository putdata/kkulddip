package com.kkulddip.common.security.oauth2;

import java.util.Map;

/**
 * OAuth2 사용자 정보 팩토리
 * OAuth2 제공자에 따라 적절한 사용자 정보 객체를 생성합니다.
 */
public class OAuth2UserInfoFactory {

    /**
     * OAuth2 제공자에 따른 사용자 정보 객체 생성
     *
     * @param registrationId OAuth2 제공자 등록 ID
     * @param attributes 사용자 속성 정보
     * @return OAuth2UserInfo 구현체
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        // registrationId에서 실제 provider 추출
        String provider = extractProvider(registrationId);

        switch (provider.toLowerCase()) {
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
            case "kakao":
                // TODO: 향후 카카오 지원 시 구현
                throw new IllegalArgumentException("카카오 OAuth2는 아직 지원하지 않습니다: " + registrationId);
            case "naver":
                // TODO: 향후 네이버 지원 시 구현
                throw new IllegalArgumentException("네이버 OAuth2는 아직 지원하지 않습니다: " + registrationId);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }

    /**
     * registrationId에서 실제 provider 이름을 추출합니다.
     *
     * @param registrationId 등록 ID (예: google-customer, google-owner)
     * @return provider 이름 (예: google)
     */
    private static String extractProvider(String registrationId) {
        if (registrationId == null) {
            return registrationId;
        }

        // google-customer, google-owner -> google
        if (registrationId.startsWith("google")) {
            return "google";
        }

        // kakao-customer, kakao-owner -> kakao (향후 지원)
        if (registrationId.startsWith("kakao")) {
            return "kakao";
        }

        // naver-customer, naver-owner -> naver (향후 지원)
        if (registrationId.startsWith("naver")) {
            return "naver";
        }

        // 기타 경우 그대로 반환
        return registrationId;
    }
}