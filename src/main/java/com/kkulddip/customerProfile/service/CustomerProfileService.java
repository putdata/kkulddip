package com.kkulddip.customerProfile.service;

import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.CustomerProfileResponse;
import com.kkulddip.customerProfile.dto.response.CustomerStatsResponse;
import com.kkulddip.customerProfile.dto.response.UpdateLocationResponse;
import com.kkulddip.customerProfile.dto.response.UpdateProfileResponse;
import com.kkulddip.customerProfile.dto.CustomerStatsDto;
import com.kkulddip.customerProfile.util.CustomerStatsCalculator;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.customerProfile.mapper.CustomerProfileMapper;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 고객 프로필 관리 서비스
 * 
 * 고객의 프로필 정보, 위치 정보, 통계 정보를 관리하는 핵심 비즈니스 로직을 처리합니다.
 * 모든 메서드는 기본적으로 읽기 전용 트랜잭션으로 동작하며, 데이터 변경이 필요한 경우에만
 * @Transactional을 명시적으로 선언합니다.
 * 
 * 주요 기능:
 * - 고객 프로필 조회 및 수정
 * - 위치 정보 업데이트 (주소, 좌표)
 * - 고객 통계 조회 및 업데이트
 * - 고객 레벨 시스템 관리
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CustomerProfileService {
    
    private final CustomerRepository customerRepository;
    private final CustomerProfileMapper customerProfileMapper;
    private final CustomerStatsCalculator statsCalculator;
    
    /**
     * 고객 프로필 정보를 조회합니다.
     * 
     * 고객의 기본 정보(이름, 이메일, 프로필 이미지, 주소, 좌표, 레벨 등)를 조회하며,
     * 조회 시점에 마지막 활동 시간을 현재 시간으로 업데이트합니다.
     * 또한 실시간으로 통계를 계산하여 업데이트합니다.
     * 
     * @param customerId 조회할 고객의 ID
     * @return 고객 프로필 응답 DTO
     * @throws BusinessException 고객을 찾을 수 없는 경우 (CUSTOMER_NOT_FOUND)
     */
    @Transactional
    public CustomerProfileResponse getProfile(Long customerId) {
        Customer customer = findCustomerById(customerId);
        updateLastActiveTime(customer);
        
        updateCustomerStatsIfNeeded(customer);
        
        log.info("고객 프로필 조회 - customerId: {}", customerId);
        return customerProfileMapper.toProfileResponse(customer);
    }
    
    /**
     * 고객 통계 정보를 조회합니다.
     * 
     * 고객의 주문 수, 절약 금액, CO2 절약량, 현재 레벨, 다음 레벨까지 필요한 주문 수 등의
     * 통계 정보를 조회합니다. 레벨 시스템은 주문 수에 따라 자동으로 계산됩니다.
     * 조회 시 실시간으로 통계를 재계산하여 업데이트합니다.
     * 
     * @param customerId 조회할 고객의 ID
     * @return 고객 통계 응답 DTO
     * @throws BusinessException 고객을 찾을 수 없는 경우 (CUSTOMER_NOT_FOUND)
     */
    @Transactional
    public CustomerStatsResponse getStats(Long customerId) {
        Customer customer = findCustomerById(customerId);
        
        updateCustomerStatsIfNeeded(customer);
        
        log.info("고객 통계 조회 - customerId: {}, totalOrder: {}", 
            customerId, customer.getTotalOrder());
        return customerProfileMapper.toStatsResponse(customer);
    }
    
    /**
     * 고객 프로필을 수정합니다.
     * 
     * 고객의 이름과 프로필 이미지 URL을 수정할 수 있습니다.
     * 수정된 정보는 즉시 데이터베이스에 반영됩니다.
     * 
     * @param customerId 수정할 고객의 ID
     * @param request 프로필 수정 요청 DTO (이름, 프로필 이미지 URL)
     * @return 수정된 프로필 응답 DTO
     * @throws BusinessException 고객을 찾을 수 없는 경우 (CUSTOMER_NOT_FOUND)
     */
    @Transactional
    public UpdateProfileResponse updateProfile(Long customerId, UpdateProfileRequest request) {
        Customer customer = findCustomerById(customerId);
        
        customer.updateProfile(request.name(), request.profileImageUrl());
        Customer savedCustomer = customerRepository.save(customer);
        
        log.info("고객 프로필 업데이트 - customerId: {}, name: {}", 
            customerId, request.name());
        return customerProfileMapper.toUpdateProfileResponse(savedCustomer);
    }
    
    /**
     * 고객의 위치 정보(주소)를 업데이트합니다.
     * 
     * 주소, 위도, 경도 정보를 수정할 수 있습니다. 좌표 유효성 검증을 수행한 후
     * 데이터베이스에 저장합니다. 이는 고객의 집 주소 정보를 관리하며,
     * 실시간 위치 추적과는 별개의 기능입니다.
     * 
     * @param customerId 수정할 고객의 ID
     * @param request 위치 정보 수정 요청 DTO (주소, 위도, 경도)
     * @return 수정된 위치 정보 응답 DTO
     * @throws BusinessException 고객을 찾을 수 없는 경우 (CUSTOMER_NOT_FOUND)
     * @throws BusinessException 유효하지 않은 좌표인 경우 (CUSTOMER_LOCATION_INVALID)
     */
    @Transactional
    public UpdateLocationResponse updateLocation(Long customerId, UpdateLocationRequest request) {
        validateLocation(request.latitude(), request.longitude());
        
        Customer customer = findCustomerById(customerId);
        customer.updateLocation(request.address(), request.latitude(), request.longitude());
        Customer savedCustomer = customerRepository.save(customer);
        
        log.info("고객 위치 정보 업데이트 - customerId: {}, address: {}", 
            customerId, request.address());
        return customerProfileMapper.toUpdateLocationResponse(savedCustomer);
    }
    
    @Transactional
    public void updateCustomerStats(Long customerId, Integer orderIncrement, 
                                   Long moneySavedIncrement, Double co2SavedIncrement) {
        Customer customer = findCustomerById(customerId);
        customer.updateStats(orderIncrement, moneySavedIncrement, co2SavedIncrement);
        customerRepository.save(customer);
        
        log.info("고객 통계 업데이트 - customerId: {}, orderIncrement: {}, " +
            "moneySavedIncrement: {}, co2SavedIncrement: {}", 
            customerId, orderIncrement, moneySavedIncrement, co2SavedIncrement);
    }
    
    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
    }
    
    /**
     * 고객의 마지막 활동 시간을 현재 시간으로 업데이트합니다.
     * 
     * 고객이 API를 호출할 때마다 활동 시간을 기록하여 활성 사용자를 추적합니다.
     * 
     * @param customer 업데이트할 고객 엔티티
     */
    @Transactional
    protected void updateLastActiveTime(Customer customer) {
        customer.updateLastActiveAt();
        customerRepository.save(customer);
    }
    
    private void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_LOCATION_INVALID);
        }
        
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new BusinessException(ErrorCode.CUSTOMER_LOCATION_INVALID);
        }
    }
    
    /**
     * 고객의 통계 정보를 실시간으로 계산하여 업데이트합니다.
     * 
     * @param customer 업데이트할 고객 엔티티
     */
    private void updateCustomerStatsIfNeeded(Customer customer) {
        try {
            CustomerStatsDto currentStats = statsCalculator.calculateStatsFast(customer.getCustomerId());
            
            int orderDiff = currentStats.totalOrder() - customer.getTotalOrder();
            long moneySavedDiff = currentStats.totalMoneySaved() - customer.getTotalMoneySaved();
            double co2SavedDiff = currentStats.totalCo2Saved() - customer.getTotalCo2Saved();
            
            if (orderDiff != 0 || moneySavedDiff != 0 || Math.abs(co2SavedDiff) > 0.01) {
                customer.updateStats(orderDiff, moneySavedDiff, co2SavedDiff);
                customerRepository.save(customer);
                
                log.info("고객 통계 업데이트 - customerId: {}, orderDiff: {}, moneySavedDiff: {}, co2SavedDiff: {}", 
                    customer.getCustomerId(), orderDiff, moneySavedDiff, co2SavedDiff);
            }
        } catch (Exception e) {
            log.error("고객 통계 업데이트 실패 - customerId: {}", customer.getCustomerId(), e);
        }
    }
    
    /**
     * 고객 프로필을 통계와 함께 업데이트된 상태로 조회합니다.
     * 
     * @param customerId 조회할 고객의 ID
     * @return 업데이트된 프로필 응답 DTO
     */
    @Transactional
    public CustomerProfileResponse getProfileWithUpdatedStats(Long customerId) {
        Customer customer = findCustomerById(customerId);
        
        CustomerStatsDto stats = statsCalculator.calculateStatsFast(customerId);
        
        int orderIncrement = stats.totalOrder() - customer.getTotalOrder();
        long moneyIncrement = stats.totalMoneySaved() - customer.getTotalMoneySaved();
        double co2Increment = stats.totalCo2Saved() - customer.getTotalCo2Saved();
        
        if (orderIncrement != 0 || moneyIncrement != 0 || Math.abs(co2Increment) > 0.01) {
            customer.updateStats(orderIncrement, moneyIncrement, co2Increment);
            customerRepository.save(customer);
            
            log.info("고객 프로필 및 통계 갱신 - customerId: {}, orders: +{}, saved: +{}, co2: +{}kg", 
                customerId, orderIncrement, moneyIncrement, co2Increment);
        }
        
        updateLastActiveTime(customer);
        
        return customerProfileMapper.toProfileResponse(customer);
    }
    
    /**
     * 갱신된 통계 정보만 조회합니다.
     * 
     * @param customerId 조회할 고객의 ID
     * @return 갱신된 통계 응답 DTO
     */
    @Transactional
    public CustomerStatsResponse getUpdatedStats(Long customerId) {
        Customer customer = findCustomerById(customerId);
        
        updateCustomerStatsIfNeeded(customer);
        
        return customerProfileMapper.toStatsResponse(customer);
    }
}