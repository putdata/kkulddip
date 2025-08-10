package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
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
import java.util.Optional;
import java.util.function.Function;

/**
 * OAuth2 Authorization Code를 처리하여 사용자를 생성하거나 조회하는 서비스
 * Google OAuth2 인증을 통해 사용자 정보를 가져오고, JWT 토큰을 생성합니다.
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
    private String googleCustomerRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google-owner.redirect-uri}")
    private String googleOwnerRedirectUri;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    /**
     * OAuth2 Authorization Code를 JWT 토큰 응답으로 교환
     *
     * @param authorizationCode Google에서 받은 Authorization Code
     * @param userRole 사용자 역할
     * @return OAuth2TokenResponse 객체
     */
    @Transactional
    public OAuth2TokenResponse exchangeCodeForToken(String authorizationCode, UserRole userRole) {
        User user = exchangeCodeForUser(authorizationCode, userRole);
        return createTokenResponse(user);
    }

    /**
     * OAuth2 Authorization Code로 사용자 정보를 가져와 User 객체를 반환
     *
     * @param authorizationCode Google에서 받은 Authorization Code
     * @param userRole 사용자 역할
     * @return User 객체
     */
    @Transactional
    public User exchangeCodeForUser(String authorizationCode, UserRole userRole) {
        // 1. Authorization Code를 OAuth2 Access Token으로 교환
        String oauthAccessToken = exchangeCodeForAccessToken(authorizationCode, userRole);

        // 2. OAuth2 Access Token으로 사용자 정보 가져오기
        Map<String, Object> userAttributes = getUserInfo(oauthAccessToken);

        // 3. OAuth2 사용자 정보 추출
        OAuth2UserInfo userInfo = oauth2UserInfoFactory.getOAuth2UserInfo("google", userAttributes);

        // 4. 사용자 역할에 따라 처리
        return processUser(userInfo, userRole);
    }

    /**
     * Authorization Code를 Google OAuth2 Access Token으로 교환
     * Google의 OAuth2 토큰 엔드포인트에 요청을 보내 Access Token을 획득합니다.
     *
     * @param authorizationCode Google에서 받은 Authorization Code
     * @param userRole 사용자 역할
     * @return Google OAuth2 Access Token
     * @throws BusinessException OAuth2 토큰 교환 실패 시
     */
    private String exchangeCodeForAccessToken(String authorizationCode, UserRole userRole) {
        String redirectUri = getRedirectUriByUserRole(userRole);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

        return Optional.ofNullable(
            webClient.post()
                .uri(GOOGLE_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(Map.class)
                .block()
            )
            .filter(response -> response.containsKey("access_token"))
            .map(response -> (String) response.get("access_token"))
            .orElseThrow(() -> {
                log.error("Google 토큰 교환 실패");
                return new BusinessException(ErrorCode.AUTH_OAUTH2_AUTHENTICATION_FAILED, "Google 토큰 교환에 실패했습니다.");
            });
    }

    /**
     * Google OAuth2 Access Token으로 사용자 정보 가져오기
     * Google의 사용자 정보 엔드포인트에 요청을 보내 사용자 정보를 획득합니다.
     *
     * @param oauthAccessToken Google OAuth2 Access Token
     * @return Google 사용자 정보 Map
     * @throws BusinessException 사용자 정보 조회 실패 시
     */
    private Map<String, Object> getUserInfo(String oauthAccessToken) {
        return Optional.ofNullable(
            webClient.get()
                .uri(GOOGLE_USER_INFO_URL)
                .headers(headers -> headers.setBearerAuth(oauthAccessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .block()
            )
            .orElseThrow(() -> {
                log.error("Google 사용자 정보 조회 실패");
                return new BusinessException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED, "Google 사용자 정보 조회에 실패했습니다.");
            });
    }

    /**
     * 사용자 역할에 따라 Customer 또는 Owner 처리
     * 사용자 역할에 따라 적절한 엔티티로 사용자를 생성하거나 조회합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param userRole 사용자 역할
     * @return 생성되거나 조회된 User 엔티티
     */
    private User processUser(OAuth2UserInfo userInfo, UserRole userRole) {
        OAuth2Provider provider = OAuth2Provider.GOOGLE;

        return switch (userRole) {
            case CUSTOMER -> processCustomer(userInfo, provider);
            case OWNER -> processOwner(userInfo, provider);
            case ADMIN -> throw new BusinessException(ErrorCode.USER_REGISTER_TYPE_ERROR, "ADMIN 사용자는 OAuth2 로그인을 지원하지 않습니다.");
        };
    }

    /**
     * Customer 처리
     * OAuth2 정보를 기반으로 기존 Customer를 조회하거나 새로운 Customer를 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param provider OAuth2 제공자
     * @return 조회되거나 생성된 Customer 엔티티
     */
    private Customer processCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return processUserByType(
            userInfo, 
            providerId -> customerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, providerId),
            info -> customerRepository.save(createCustomer(info, provider)),
            "Customer"
        );
    }

    /**
     * Owner 처리  
     * OAuth2 정보를 기반으로 기존 Owner를 조회하거나 새로운 Owner를 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param provider OAuth2 제공자
     * @return 조회되거나 생성된 Owner 엔티티
     */
    private Owner processOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        return processUserByType(
            userInfo, 
            providerId -> ownerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, providerId),
            info -> ownerRepository.save(createOwner(info, provider)),
            "Owner"
        );
    }

    /**
     * 제네릭 사용자 처리 메서드
     * OAuth2 정보를 기반으로 기존 사용자를 조회하거나 새로운 사용자를 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param finder OAuth2Provider와 providerId로 사용자를 찾는 Function
     * @param creator 새로운 엔티티를 생성하고 저장하는 Function
     * @param userType 사용자 타입 문자열 (로깅용)
     * @return 조회되거나 생성된 사용자 엔티티
     */
    private <T extends User> T processUserByType(
            OAuth2UserInfo userInfo,
            Function<String, Optional<T>> finder,
            Function<OAuth2UserInfo, T> creator,
            String userType) {

        return finder.apply(userInfo.getId())
            .map(existingUser -> {
                log.info("기존 {} 로그인 - Email: {}", userType, userInfo.getEmail());
                return existingUser;
            })
            .orElseGet(() -> {
                log.info("새로운 {} 회원가입 - Email: {}", userType, userInfo.getEmail());
                return creator.apply(userInfo);
            });
    }


    /**
     * Customer 엔티티 생성
     * OAuth2 사용자 정보를 기반으로 새로운 Customer 엔티티를 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param provider OAuth2 제공자
     * @return 생성된 Customer 엔티티
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
     * OAuth2 사용자 정보를 기반으로 새로운 Owner 엔티티를 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param provider OAuth2 제공자
     * @return 생성된 Owner 엔티티
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
     * 사용자 정보를 기반으로 JWT Access Token과 Refresh Token을 생성하여 응답 객체를 구성합니다.
     *
     * @param user 인증된 사용자 엔티티
     * @return OAuth2TokenResponse 객체
     */
    private OAuth2TokenResponse createTokenResponse(User user) {
        JwtUserInfo jwtUserInfo = new JwtUserInfo(
            user.getId().toString(),
            user.getEmail(),
            user.getRole().name(),
            user.getOauth2Provider().name(),
            user.getOauth2ProviderId()
        );

        String jwtAccessToken = jwtUtil.generateAccessToken(jwtUserInfo);

        String jwtRefreshToken = jwtUtil.generateRefreshToken(jwtUserInfo);

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

    /**
     * 사용자 역할에 따른 적절한 redirect URI 반환
     *
     * @param userRole 사용자 역할
     * @return 해당하는 redirect URI
     */
    private String getRedirectUriByUserRole(UserRole userRole) {
        return switch (userRole) {
            case CUSTOMER -> googleCustomerRedirectUri;
            case OWNER -> googleOwnerRedirectUri;
            case ADMIN -> throw new BusinessException(ErrorCode.USER_REGISTER_TYPE_ERROR, "ADMIN 사용자는 OAuth2 로그인을 지원하지 않습니다.");
        };
    }
}