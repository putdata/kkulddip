package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUtil;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenResponse;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * OAuth2 Authorization Code를 처리하여 사용자를 생성하거나 조회하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2TokenService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final OAuth2UserInfoFactory oauth2UserInfoFactory;
    private final WebClient webClient;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google-customer.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google-customer.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google-customer.redirect-uri}")
    private String googleRedirectUri;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    /**
     * OAuth2 Authorization Code를 JWT 토큰 응답으로 교환
     *
     * @param authorizationCode Google에서 받은 Authorization Code
     * @param userType 사용자 타입
     * @return OAuth2TokenResponse 객체
     */
    @Transactional
    public OAuth2TokenResponse exchangeCodeForToken(String authorizationCode, String userType) {
        User user = exchangeCodeForUser(authorizationCode, userType);
        return createTokenResponse(user);
    }

    /**
     * OAuth2 Authorization Code로 사용자 정보를 가져와 User 객체를 반환
     *
     * @param authorizationCode Google에서 받은 Authorization Code
     * @param userType 사용자 타입
     * @return User 객체
     */
    @Transactional
    public User exchangeCodeForUser(String authorizationCode, String userType) {
        try {
            // 1. Authorization Code를 OAuth2 Access Token으로 교환
            String oauthAccessToken = exchangeCodeForAccessToken(authorizationCode);

            // 2. OAuth2 Access Token으로 사용자 정보 가져오기
            Map<String, Object> userAttributes = getUserInfo(oauthAccessToken);

            // 3. OAuth2 사용자 정보 추출
            OAuth2UserInfo userInfo = oauth2UserInfoFactory.getOAuth2UserInfo("google", userAttributes);

            // 4. 사용자 타입에 따라 처리
            return processUser(userInfo, userType);

        } catch (Exception e) {
            log.error("OAuth2 토큰 교환 실패 - Code: {}, UserType: {}", authorizationCode, userType, e);
            throw new BusinessException(ErrorCode.AUTH_OAUTH2_AUTHENTICATION_FAILED);
        }
    }

    /**
     * Authorization Code를 Google OAuth2 Access Token으로 교환
     */
    private String exchangeCodeForAccessToken(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", googleRedirectUri);

        try {
            Map<String, Object> tokenResponse = webClient.post()
                .uri(GOOGLE_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                throw new BusinessException(ErrorCode.AUTH_OAUTH2_AUTHENTICATION_FAILED,
                                          "Google 토큰 응답이 유효하지 않습니다.");
            }

            return (String) tokenResponse.get("access_token");

        } catch (Exception e) {
            log.error("Google 토큰 교환 실패", e);
            throw new BusinessException(ErrorCode.AUTH_OAUTH2_AUTHENTICATION_FAILED,
                                      "Google 토큰 교환에 실패했습니다.");
        }
    }

    /**
     * Google OAuth2 Access Token으로 사용자 정보 가져오기
     */
    private Map<String, Object> getUserInfo(String oauthAccessToken) {
        try {
            Map<String, Object> userInfo = webClient.get()
                .uri(GOOGLE_USER_INFO_URL)
                .headers(headers -> headers.setBearerAuth(oauthAccessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (userInfo == null) {
                throw new BusinessException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED,
                                          "Google 사용자 정보를 가져올 수 없습니다.");
            }

            log.info("Google userInfo response: {}", userInfo);


            return userInfo;

        } catch (Exception e) {
            log.error("Google 사용자 정보 조회 실패", e);
            throw new BusinessException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED,
                                      "Google 사용자 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 사용자 타입에 따라 Customer 또는 Owner 처리
     */
    private User processUser(OAuth2UserInfo userInfo, String userType) {
        OAuth2Provider provider = OAuth2Provider.GOOGLE;

        return switch (userType.toLowerCase()) {
            case "customer" -> processCustomer(userInfo, provider);
            case "owner" -> processOwner(userInfo, provider);
            default -> throw new BusinessException(ErrorCode.USER_REGISTER_TYPE_ERROR, "지원하지 않는 사용자 타입입니다: " + userType);
        };
    }

    /**
     * Customer 처리
     */
    private Customer processCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return customerRepository
            .findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
            .map(existingCustomer -> {
                log.info("기존 Customer 로그인 - Email: {}", userInfo.getEmail());
                return existingCustomer;
            })
            .orElseGet(() -> {
                log.info("새로운 Customer 회원가입 - Email: {}", userInfo.getEmail());
                return customerRepository.save(createCustomer(userInfo, provider));
            });
    }

    /**
     * Owner 처리
     */
    private Owner processOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return ownerRepository
            .findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
            .map(existingOwner -> {
                log.info("기존 Owner 로그인 - Email: {}", userInfo.getEmail());
                return existingOwner;
            })
            .orElseGet(() -> {
                log.info("새로운 Owner 회원가입 - Email: {}", userInfo.getEmail());
                return ownerRepository.save(createOwner(userInfo, provider));
            });
    }

    /**
     * Customer 엔티티 생성
     */
    private Customer createCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return Customer.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
    }

    /**
     * Owner 엔티티 생성
     */
    private Owner createOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return Owner.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
    }

    /**
     * JWT 토큰 응답 생성
     */
    private OAuth2TokenResponse createTokenResponse(User user) {
        String jwtAccessToken = jwtUtil.generateAccessToken(
            user.getEmail(),
            user.getRole().name(),
            user.getOauth2Provider().name(),
            user.getOauth2ProviderId()
        );

        String jwtRefreshToken = jwtUtil.generateRefreshToken(
            user.getEmail(),
            user.getRole().name(),
            user.getOauth2Provider().name(),
            user.getOauth2ProviderId()
        );

        return OAuth2TokenResponse.builder()
            .accessToken(jwtAccessToken)
            .refreshToken(jwtRefreshToken)
            .expiresIn(accessTokenExpiration)
            .user(OAuth2TokenResponse.UserInfo.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .profileImageUrl(user.getProfileImageUrl())
                .build())
            .build();
    }
}