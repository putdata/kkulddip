package com.kkulddip.common.security.oauth2;

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

/**
 * OAuth2 사용자 정보를 처리하는 커스텀 서비스
 * Google OAuth2 로그인 시 사용자 정보를 데이터베이스에 저장하거나 업데이트합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final OwnerRepository ownerRepository;
    private final OwnerService ownerService;
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
            
            // 데이터베이스에 사용자 저장 또는 업데이트
            Object user = saveOrUpdateUser(userInfo, registrationId);
            
            return OAuth2UserPrincipal.create(user, oauth2User.getAttributes());
            
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
     * @return 저장된 사용자 엔티티 (Customer 또는 Owner)
     */
    private Object saveOrUpdateUser(OAuth2UserInfo userInfo, String registrationId) {
        try {
            if (registrationId.contains("-customer")) {
                return handleCustomerLogin(userInfo, registrationId);
            } else if (registrationId.contains("-owner")) {
                return handleOwnerLogin(userInfo, registrationId);
            } else {
                // 기본값 설정
                log.info("Registration ID에 사용자 타입이 명시되지 않아 기본값 사용: customer");
                
                if (true) { // 기본값은 customer
                    return handleCustomerLogin(userInfo, registrationId);
                } else {
                    return handleOwnerLogin(userInfo, registrationId);
                }
            }
        } catch (Exception ex) {
            log.error("OAuth2 사용자 저장/업데이트 실패 - Email: {}, RegistrationId: {}", 
                     userInfo.getEmail(), registrationId, ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_CREATION_FAILED
            );
        }
    }

    /**
     * Customer 로그인 처리
     */
    private Customer handleCustomerLogin(OAuth2UserInfo userInfo, String registrationId) {
        try {
            OAuth2Provider provider = OAuth2Provider.fromRegistrationId(
                registrationId.replace("-customer", ""));
            
            return customerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
                .map(existingCustomer -> updateExistingCustomer(existingCustomer, userInfo))
                .orElseGet(() -> createNewCustomer(userInfo, provider));
                
        } catch (Exception ex) {
            log.error("Customer 로그인 처리 실패 - Email: {}, RegistrationId: {}", 
                     userInfo.getEmail(), registrationId, ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_CREATION_FAILED
            );
        }
    }

    /**
     * Owner 로그인 처리
     */
    private Owner handleOwnerLogin(OAuth2UserInfo userInfo, String registrationId) {
        try {
            OAuth2Provider provider = OAuth2Provider.fromRegistrationId(
                registrationId.replace("-owner", ""));
            
            return ownerRepository.findByOauth2ProviderAndOauth2ProviderId(provider, userInfo.getId())
                .map(existingOwner -> updateExistingOwner(existingOwner, userInfo))
                .orElseGet(() -> createNewOwner(userInfo, provider));
                
        } catch (Exception ex) {
            log.error("Owner 로그인 처리 실패 - Email: {}, RegistrationId: {}", 
                     userInfo.getEmail(), registrationId, ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_CREATION_FAILED
            );
        }
    }

    /**
     * 기존 Customer 정보 업데이트
     */
    private Customer updateExistingCustomer(Customer customer, OAuth2UserInfo userInfo) {
        try {
            return customerService.updateProfile(customer, userInfo.getName(), userInfo.getImageUrl());
        } catch (Exception ex) {
            log.error("Customer 업데이트 실패 - Customer ID: {}, Email: {}", 
                     customer.getCustomerId(), customer.getEmail(), ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_UPDATE_FAILED
            );
        }
    }

    /**
     * 기존 Owner 정보 업데이트
     */
    private Owner updateExistingOwner(Owner owner, OAuth2UserInfo userInfo) {
        try {
            return ownerService.updateProfile(owner, userInfo.getName(), userInfo.getImageUrl());
        } catch (Exception ex) {
            log.error("Owner 업데이트 실패 - Owner ID: {}, Email: {}", 
                     owner.getOwnerId(), owner.getEmail(), ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_UPDATE_FAILED
            );
        }
    }

    /**
     * 새로운 Customer 생성 (기본값)
     */
    private Customer createNewCustomer(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        try {
            Customer newCustomer = Customer.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .profileImageUrl(userInfo.getImageUrl())
                .oauth2Provider(provider)
                .oauth2ProviderId(userInfo.getId())
                .build();
            
            Customer savedCustomer = customerRepository.save(newCustomer);
            
            log.info("새로운 Customer가 생성되었습니다. ID: {}, 이메일: {}", 
                     savedCustomer.getCustomerId(), savedCustomer.getEmail());
            
            return savedCustomer;
            
        } catch (Exception ex) {
            log.error("Customer 생성 실패 - Email: {}, Provider: {}", 
                     userInfo.getEmail(), provider, ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_CREATION_FAILED
            );
        }
    }

    /**
     * 새로운 Owner 생성
     */
    private Owner createNewOwner(OAuth2UserInfo userInfo, OAuth2Provider provider) {
        try {
            Owner newOwner = Owner.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .profileImageUrl(userInfo.getImageUrl())
                .oauth2Provider(provider)
                .oauth2ProviderId(userInfo.getId())
                .build();
            
            Owner savedOwner = ownerRepository.save(newOwner);
            
            log.info("새로운 Owner가 생성되었습니다. ID: {}, 이메일: {}", 
                     savedOwner.getOwnerId(), savedOwner.getEmail());
            
            return savedOwner;
            
        } catch (Exception ex) {
            log.error("Owner 생성 실패 - Email: {}, Provider: {}", 
                     userInfo.getEmail(), provider, ex);
            throw new OAuth2AuthenticationException(
                ErrorCode.AUTH_OAUTH2_USER_CREATION_FAILED
            );
        }
    }
}