package com.kkulddip.common.security.oauth2;

import com.kkulddip.common.entity.User;
import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.oauth2.exception.OAuth2AuthenticationException;

import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.customer.service.CustomerService;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

/**
 * OAuth2 사용자 정보를 처리하는 커스텀 서비스
 * Google OAuth2 로그인 시 사용자 정보를 데이터베이스에 저장하거나 업데이트합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final OAuth2UserInfoFactory oauth2UserInfoFactory;

    /**
     * OAuth2 제공자로부터 받은 사용자 정보를 처리
     * 
     * @param userRequest OAuth2 사용자 요청 정보
     * @return OAuth2User 객체 (사용자 정보 포함)
     * @throws OAuth2AuthenticationException OAuth2 인증 예외
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) 
            throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            
            log.info("OAuth2 로그인 요청 - 제공자: {}", registrationId);
            
            // OAuth2 사용자 정보 추출
            OAuth2UserInfo userInfo = oauth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oauth2User.getAttributes());
            
            // 이메일 검증
            if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
                throw new OAuth2AuthenticationException(
                    ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
                );
            }
            
            // OAuth2 제공자 정보 추출 (google-customer -> google)
            String provider = registrationId.contains("-") ? 
                registrationId.substring(0, registrationId.indexOf("-")) : registrationId;
            
            // 데이터베이스에 사용자 저장 또는 업데이트
            User user = saveOrUpdateUser(userInfo, registrationId, provider);
            
            return OAuth2UserPrincipal.create(user, oauth2User.getAttributes(), provider);
            
        } catch (Exception ex) {
            log.error("OAuth2 사용자 로드 실패 - RegistrationId: {}", 
                     userRequest.getClientRegistration().getRegistrationId(), ex);
            
            throw new OAuth2AuthenticationException(
                    ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED
            );
        }
    }

    /**
     * 사용자 정보를 데이터베이스에 저장하거나 업데이트
     *
     * @param userInfo OAuth2 사용자 정보
     * @param registrationId OAuth2 제공자 등록 ID
     * @param provider OAuth2 제공자 (google, kakao 등)
     * @return 저장된 사용자 엔티티 (Customer 또는 Owner)
     */
    private User saveOrUpdateUser(OAuth2UserInfo userInfo, String registrationId, String provider) {
        if (registrationId.contains("-customer")) {
            return handleCustomerLogin(userInfo, provider);
        } else if (registrationId.contains("-owner")) {
            return handleOwnerLogin(userInfo, provider);
        } else {
            return handleCustomerLogin(userInfo, provider);
        }
    }

    /**
     * 공통 OAuth2 로그인 처리
     * 
     * @param userInfo OAuth2 사용자 정보
     * @param providerString OAuth2 제공자 문자열
     * @param findByProviderAndId 기존 사용자 검색 함수
     * @param updateUser 기존 사용자 업데이트 함수
     * @param createUser 새 사용자 생성 함수
     * @return 처리된 사용자 객체
     */
    private <T> T handleOAuth2Login(
            OAuth2UserInfo userInfo, 
            String providerString,
            Function<OAuth2Provider, Optional<T>> findByProviderAndId,
            Function<T, T> updateUser,
            Function<OAuth2Provider, T> createUser) {
        
        OAuth2Provider provider = OAuth2Provider.fromRegistrationId(providerString);
        
        return findByProviderAndId.apply(provider)
            .map(updateUser)
            .orElseGet(() -> createUser.apply(provider));
    }

    /**
     * Customer 로그인 처리
     */
    private Customer handleCustomerLogin(OAuth2UserInfo userInfo, String providerString) {
        return handleOAuth2Login(
            userInfo,
            providerString,
            provider -> customerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId()),
            existingCustomer -> updateExistingCustomer(existingCustomer, userInfo),
            provider -> createNewCustomer(userInfo, provider)
        );
    }

    /**
     * Owner 로그인 처리
     */
    private Owner handleOwnerLogin(OAuth2UserInfo userInfo, String providerString) {
        return handleOAuth2Login(
            userInfo,
            providerString,
            provider -> ownerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId()),
            existingOwner -> updateExistingOwner(existingOwner, userInfo),
            provider -> createNewOwner(userInfo, provider)
        );
    }

    /**
     * 기존 Customer 정보 업데이트
     */
    private Customer updateExistingCustomer(Customer customer, OAuth2UserInfo userInfo) {
        customer.updateName(userInfo.getName());
        customer.updateProfileImageUrl(userInfo.getImageUrl());
        log.info("Customer 정보 업데이트 - ID: {}, Email: {}", 
                 customer.getCustomerId(), customer.getEmail());
        return customer;
    }

    /**
     * 기존 Owner 정보 업데이트
     */
    private Owner updateExistingOwner(Owner owner, OAuth2UserInfo userInfo) {
        owner.updateName(userInfo.getName());
        owner.updateProfileImageUrl(userInfo.getImageUrl());
        log.info("Owner 정보 업데이트 - ID: {}, Email: {}", 
                 owner.getOwnerId(), owner.getEmail());
        return owner;
    }

    /**
     * 새로운 Customer 생성
     */
    private Customer createNewCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        Customer newCustomer = Customer.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Customer savedCustomer = customerRepository.save(newCustomer);
        log.info("새로운 Customer 생성 - ID: {}, Email: {}", 
                 savedCustomer.getCustomerId(), savedCustomer.getEmail());
        
        return savedCustomer;
    }

    /**
     * 새로운 Owner 생성
     */
    private Owner createNewOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        Owner newOwner = Owner.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Owner savedOwner = ownerRepository.save(newOwner);
        log.info("새로운 Owner 생성 - ID: {}, Email: {}", 
                 savedOwner.getOwnerId(), savedOwner.getEmail());
        
        return savedOwner;
    }
}