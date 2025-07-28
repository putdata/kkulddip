package com.kkulddip.common.security.oauth2;

import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * OAuth2 제공자로부터 받은 사용자 정보를 처리
     * 
     * @param userRequest OAuth2 사용자 요청 정보
     * @return OAuth2User 객체 (사용자 정보 포함)
     * @throws OAuth2AuthenticationException OAuth2 인증 예외
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());
        
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 받을 수 없습니다.");
        }

        Object user = saveOrUpdateUser(userInfo, registrationId);
        
        return new OAuth2UserPrincipal(user, oauth2User.getAttributes());
    }

    /**
     * 사용자 정보를 저장하거나 업데이트
     * registrationId에 따라 Customer 또는 Owner 테이블에 저장
     * 
     * @param userInfo OAuth2 사용자 정보
     * @param registrationId OAuth2 제공자 ID (google-customer, google-owner)
     * @return 저장된 사용자 엔티티 (Customer 또는 Owner)
     */
    private Object saveOrUpdateUser(OAuth2UserInfo userInfo, String registrationId) {
        if (registrationId.contains("customer")) {
            return handleCustomerLogin(userInfo, registrationId);
        } else if (registrationId.contains("owner")) {
            return handleOwnerLogin(userInfo, registrationId);
        } else {
            // 기본값은 customer (기존 호환성)
            return handleCustomerLogin(userInfo, registrationId);
        }
    }

    /**
     * Customer 로그인 처리
     */
    private Customer handleCustomerLogin(OAuth2UserInfo userInfo, String registrationId) {
        Customer.OAuth2Provider provider = Customer.OAuth2Provider.fromRegistrationId(
            registrationId.replace("-customer", ""));
        
        return customerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
            .map(existingCustomer -> updateExistingCustomer(existingCustomer, userInfo))
            .orElseGet(() -> createNewCustomer(userInfo, provider));
    }

    /**
     * Owner 로그인 처리
     */
    private Owner handleOwnerLogin(OAuth2UserInfo userInfo, String registrationId) {
        Owner.OAuth2Provider provider = Owner.OAuth2Provider.fromRegistrationId(
            registrationId.replace("-owner", ""));
        
        return ownerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
            .map(existingOwner -> updateExistingOwner(existingOwner, userInfo))
            .orElseGet(() -> createNewOwner(userInfo, provider));
    }

    /**
     * 기존 Customer 정보 업데이트
     */
    private Customer updateExistingCustomer(Customer customer, OAuth2UserInfo userInfo) {
        customer.updateProfile(userInfo.getName(), userInfo.getImageUrl());
        Customer updatedCustomer = customerRepository.save(customer);
        
        log.info("기존 Customer 정보가 업데이트되었습니다. ID: {}, 이메일: {}", 
                 updatedCustomer.getId(), updatedCustomer.getEmail());
        
        return updatedCustomer;
    }

    /**
     * 기존 Owner 정보 업데이트
     */
    private Owner updateExistingOwner(Owner owner, OAuth2UserInfo userInfo) {
        owner.updateProfile(userInfo.getName(), userInfo.getImageUrl());
        Owner updatedOwner = ownerRepository.save(owner);
        
        log.info("기존 Owner 정보가 업데이트되었습니다. ID: {}, 이메일: {}", 
                 updatedOwner.getId(), updatedOwner.getEmail());
        
        return updatedOwner;
    }

    /**
     * 새로운 Customer 생성 (기본값)
     */
    private Customer createNewCustomer(OAuth2UserInfo userInfo, Customer.OAuth2Provider provider) {
        Customer newCustomer = Customer.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Customer savedCustomer = customerRepository.save(newCustomer);
        
        log.info("새로운 Customer가 생성되었습니다. ID: {}, 이메일: {}", 
                 savedCustomer.getId(), savedCustomer.getEmail());
        
        return savedCustomer;
    }

    /**
     * 새로운 Owner 생성
     */
    private Owner createNewOwner(OAuth2UserInfo userInfo, Owner.OAuth2Provider provider) {
        Owner newOwner = Owner.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .profileImageUrl(userInfo.getImageUrl())
            .oauth2Provider(provider)
            .oauth2ProviderId(userInfo.getId())
            .build();
        
        Owner savedOwner = ownerRepository.save(newOwner);
        
        log.info("새로운 Owner가 생성되었습니다. ID: {}, 이메일: {}", 
                 savedOwner.getId(), savedOwner.getEmail());
        
        return savedOwner;
    }
}