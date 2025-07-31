package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.security.oauth2.exception.OAuth2UserInfoException;
import com.kkulddip.common.security.oauth2.exception.OAuth2UnsupportedProviderException;
import com.kkulddip.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 사용자 정보 팩토리
 * OAuth2 제공자에 따라 적절한 사용자 정보 객체를 생성합니다.
 */
@Slf4j
@Component
public class OAuth2UserInfoFactory {

    /**
     * OAuth2 제공자에 따른 사용자 정보 객체 생성
     *
     * @param registrationId OAuth2 제공자 등록 ID
     * @param attributes 사용자 속성 정보
     * @return OAuth2UserInfo 구현체
     */
    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {

        // 1. 입력값 검증
        validateInputs(registrationId, attributes);

        // 2. 제공자 추출
        OAuth2Provider provider = extractProvider(registrationId);
        log.debug("OAuth2 제공자 추출 완료 - Provider: {}", provider);

        // 3. 사용자 정보 생성
        OAuth2UserInfo userInfo = createUserInfo(provider, attributes);
        log.debug("OAuth2 사용자 정보 생성 완료 - Provider: {}", provider);

        return userInfo;
    }

    /**
     * 입력값 검증
     * registrationId와 사용자 속성 정보의 유효성을 검증합니다.
     *
     * @param registrationId OAuth2 제공자 등록 ID
     * @param attributes 사용자 속성 정보
     * @throws OAuth2UserInfoException 입력값이 유효하지 않은 경우
     */
    private void validateInputs(String registrationId, Map<String, Object> attributes) {
        Optional.ofNullable(registrationId)
            .filter(id -> !id.trim().isEmpty())
            .orElseThrow(() -> {
                log.error("OAuth2 registrationId가 비어있습니다");
                return new OAuth2UserInfoException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED);
            });

        Optional.ofNullable(attributes)
            .filter(attr -> !attr.isEmpty())
            .orElseThrow(() -> {
                log.error("OAuth2 사용자 속성이 비어있습니다 - RegistrationId: {}", registrationId);
                return new OAuth2UserInfoException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED);
            });
    }

    /**
     * registrationId에서 OAuth2 제공자를 추출합니다
     *
     * @param registrationId 등록 ID
     * @return OAuth2Provider enum
     */
    private OAuth2Provider extractProvider(String registrationId) {
        String normalizedRegistrationId = registrationId.toLowerCase().trim();

        return Arrays.stream(OAuth2Provider.values())
            .filter(provider -> normalizedRegistrationId.startsWith(provider.getRegistrationId().toLowerCase()))
            .findFirst()
            .orElseThrow(() -> {
                log.error("지원하지 않는 OAuth2 제공자입니다 - RegistrationId: {}", registrationId);
                return new OAuth2UnsupportedProviderException(ErrorCode.AUTH_OAUTH2_UNSUPPORTED_PROVIDER);
            });
    }

    /**
     * 제공자에 따른 사용자 정보 객체 생성
     * OAuth2 제공자별로 적절한 사용자 정보 구현체를 생성합니다.
     *
     * @param provider OAuth2 제공자
     * @param attributes 사용자 속성 정보
     * @return OAuth2UserInfo 구현체
     * @throws OAuth2UnsupportedProviderException 지원하지 않는 제공자인 경우
     */
    private OAuth2UserInfo createUserInfo(OAuth2Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> createGoogleUserInfo(attributes);
            default -> throw new OAuth2UnsupportedProviderException(ErrorCode.AUTH_OAUTH2_UNSUPPORTED_PROVIDER);
        };
    }

    /**
     * Google OAuth2 사용자 정보 생성
     * Google OAuth2 응답 속성을 Google 사용자 정보 객체로 변환합니다.
     *
     * @param attributes Google OAuth2 사용자 속성
     * @return GoogleOAuth2UserInfo 객체
     * @throws OAuth2UserInfoException 사용자 정보 생성 실패 시
     */
    private OAuth2UserInfo createGoogleUserInfo(Map<String, Object> attributes) {
        return Optional.ofNullable(attributes)
            .map(GoogleOAuth2UserInfo::new)
            .orElseThrow(() -> new OAuth2UserInfoException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED));
    }
}