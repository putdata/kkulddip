package com.kkulddip.common.security.oauth2;

import com.kkulddip.domain.user.entity.User;
import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.oauth2.exception.OAuth2AuthenticationException;

import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * OAuth2 사용자 정보를 처리하는 커스텀 서비스
 * Google OAuth2 로그인 시 사용자 정보를 데이터베이스에 처리합니다.
 * 로그인과 회원가입을 명확히 구분하여 처리합니다.
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
            validateUserInfo(userInfo);
            
            // OAuth2 제공자 정보 추출 (google-customer -> google)
            String provider = extractProvider(registrationId);
            
            // 사용자 타입 결정 및 처리
            User user = processUserAuthentication(userInfo, registrationId, provider);
            
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
     * 사용자 정보 검증
     */
    private void validateUserInfo(OAuth2UserInfo userInfo) {
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException(ErrorCode.AUTH_OAUTH2_USER_INFO_FAILED);
        }
    }

    /**
     * OAuth2 제공자 추출
     */
    private String extractProvider(String registrationId) {
        return registrationId.contains("-") ? 
            registrationId.substring(0, registrationId.indexOf("-")) : registrationId;
    }

    /**
     * 사용자 인증 처리 - 로그인과 회원가입을 구분하여 처리
     */
    private User processUserAuthentication(OAuth2UserInfo userInfo, String registrationId, String provider) {
        OAuth2Provider oauth2Provider = OAuth2Provider.fromRegistrationId(provider);
        
        if (isCustomerRegistration(registrationId)) {
            return processCustomerAuthentication(userInfo, oauth2Provider);
        } else if (isOwnerRegistration(registrationId)) {
            return processOwnerAuthentication(userInfo, oauth2Provider);
        } else {
            // 기본값은 Customer로 처리
            return processCustomerAuthentication(userInfo, oauth2Provider);
        }
    }

    /**
     * Customer 인증 처리
     */
    private Customer processCustomerAuthentication(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        Optional<Customer> existingCustomer = customerRepository
            .findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId());
        
        if (existingCustomer.isPresent()) {
            return loginExistingCustomer(existingCustomer.get());
        } else {
            return registerNewCustomer(userInfo, provider);
        }
    }

    /**
     * Owner 인증 처리
     */
    private Owner processOwnerAuthentication(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        Optional<Owner> existingOwner = ownerRepository
            .findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId());
        
        if (existingOwner.isPresent()) {
            return loginExistingOwner(existingOwner.get());
        } else {
            return registerNewOwner(userInfo, provider);
        }
    }

    /**
     * 기존 Customer 로그인 처리
     */
    private Customer loginExistingCustomer(Customer customer) {
        log.info("기존 Customer 로그인 - ID: {}, Email: {}", 
                 customer.getCustomerId(), customer.getEmail());
        
        return customer;
    }

    /**
     * 기존 Owner 로그인 처리
     */
    private Owner loginExistingOwner(Owner owner) {
        log.info("기존 Owner 로그인 - ID: {}, Email: {}", 
                 owner.getOwnerId(), owner.getEmail());
        
        return owner;
    }

    /**
     * 새로운 Customer 회원가입 처리
     */
    private Customer registerNewCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        log.info("새로운 Customer 회원가입 시작 - Email: {}", userInfo.getEmail());
        
        Customer newCustomer = Customer.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Customer savedCustomer = customerRepository.save(newCustomer);
        
        log.info("새로운 Customer 회원가입 완료 - ID: {}, Email: {}", 
                 savedCustomer.getCustomerId(), savedCustomer.getEmail());
        
        return savedCustomer;
    }

    /**
     * 새로운 Owner 회원가입 처리
     */
    private Owner registerNewOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        log.info("새로운 Owner 회원가입 시작 - Email: {}", userInfo.getEmail());
        
        Owner newOwner = Owner.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Owner savedOwner = ownerRepository.save(newOwner);
        
        log.info("새로운 Owner 회원가입 완료 - ID: {}, Email: {}", 
                 savedOwner.getOwnerId(), savedOwner.getEmail());
        
        return savedOwner;
    }

    /**
     * Customer 등록인지 확인
     */
    private boolean isCustomerRegistration(String registrationId) {
        return registrationId.contains("-customer");
    }

    /**
     * Owner 등록인지 확인
     */
    private boolean isOwnerRegistration(String registrationId) {
        return registrationId.contains("-owner");
    }
}