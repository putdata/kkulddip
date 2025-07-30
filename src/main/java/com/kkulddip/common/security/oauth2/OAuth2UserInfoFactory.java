package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.security.oauth2.exception.OAuth2UserInfoException;
import com.kkulddip.common.security.oauth2.exception.OAuth2UnsupportedProviderException;
import com.kkulddip.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

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
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     */
    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        try {
            // registrationId에서 실제 provider 추출
            OAuth2Provider provider = extractProvider(registrationId);
            
            log.debug("OAuth2 사용자 정보 생성 - Provider: {}, RegistrationId: {}", provider, registrationId);

            return switch (provider) {
                case GOOGLE -> createGoogleUserInfo(attributes);
                default -> throw new OAuth2UnsupportedProviderException(ErrorCode.AUTH_OAUTH2_UNSUPPORTED_PROVIDER);
            };
        } catch (Exception ex) {
            log.error("OAuth2 사용자 정보 생성 실패 - RegistrationId: {}", registrationId, ex);
            if (ex instanceof OAuth2UnsupportedProviderException) {
                throw ex;
            }
            throw new OAuth2UserInfoException(
                ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
            );
        }
    }
    
    /**
     * Google OAuth2 사용자 정보 생성
     */
    private OAuth2UserInfo createGoogleUserInfo(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new OAuth2UserInfoException(
                    ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
            );
        }
        return new GoogleOAuth2UserInfo(attributes);
    }

    /**
     * registrationId에서 실제 provider를 추출합니다.
     *
     * @param registrationId 등록 ID (예: google-customer, google-owner)
     * @return OAuth2Provider enum
     */
    private OAuth2Provider extractProvider(String registrationId) {
        if (registrationId == null) {
            throw new OAuth2UserInfoException(
                ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
            );
        }

        // OAuth2Provider enum에서 직접 매칭 확인
        for (OAuth2Provider provider : OAuth2Provider.values()) {
            if (registrationId.toLowerCase().startsWith(provider.getRegistrationId().toLowerCase())) {
                return provider;
            }
        }

        throw new OAuth2UnsupportedProviderException(
            ErrorCode.AUTH_OAUTH2_UNSUPPORTED_PROVIDER
        );
    }
}