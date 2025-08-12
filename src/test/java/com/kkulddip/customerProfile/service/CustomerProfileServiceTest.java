package com.kkulddip.customerProfile.service;

import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.*;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.customerProfile.mapper.CustomerProfileMapper;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CustomerProfileMapper customerProfileMapper;
    
    @InjectMocks
    private CustomerProfileService customerProfileService;
    
    private Customer customer;
    private Long customerId = 1L;
    
    @BeforeEach
    void setUp() {
        customer = Customer.builder()
            .email("test@example.com")
            .name("테스트 고객")
            .profileImageUrl("https://example.com/profile.jpg")
            .address("서울시 강남구")
            .latitude(37.5665)
            .longitude(126.9780)
            .level(CustomerLevel.SPROUT_BEE)
            .totalOrder(5)
            .totalMoneySaved(10000L)
            .totalCo2Saved(5.5)
            .build();
    }
    
    @Test
    @DisplayName("고객 프로필 조회 성공")
    void getProfile_Success() {
        CustomerProfileResponse expectedResponse = CustomerProfileResponse.builder()
            .customerId(customerId)
            .email("test@example.com")
            .name("테스트 고객")
            .level(CustomerLevel.SPROUT_BEE)
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerProfileMapper.toProfileResponse(customer)).thenReturn(expectedResponse);
        
        CustomerProfileResponse response = customerProfileService.getProfile(customerId);
        
        assertThat(response).isNotNull();
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.name()).isEqualTo("테스트 고객");
        
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(customer);
    }
    
    @Test
    @DisplayName("존재하지 않는 고객 조회 시 예외 발생")
    void getProfile_CustomerNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> customerProfileService.getProfile(customerId))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> {
                BusinessException businessEx = (BusinessException) ex;
                assertThat(businessEx.getErrorCode()).isEqualTo(ErrorCode.CUSTOMER_NOT_FOUND);
            });
    }
    
    @Test
    @DisplayName("고객 통계 조회 성공")
    void getStats_Success() {
        CustomerStatsResponse expectedResponse = CustomerStatsResponse.builder()
            .customerId(customerId)
            .level(CustomerLevel.SPROUT_BEE)
            .totalOrder(5)
            .totalMoneySaved(10000L)
            .totalCo2Saved(5.5)
            .ordersUntilNextLevel(5)
            .nextLevel(CustomerLevel.WORKER_BEE)
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerProfileMapper.toStatsResponse(customer)).thenReturn(expectedResponse);
        
        CustomerStatsResponse response = customerProfileService.getStats(customerId);
        
        assertThat(response).isNotNull();
        assertThat(response.totalOrder()).isEqualTo(5);
        assertThat(response.ordersUntilNextLevel()).isEqualTo(5);
        assertThat(response.nextLevel()).isEqualTo(CustomerLevel.WORKER_BEE);
    }
    
    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .name("새로운 이름")
            .profileImageUrl("https://example.com/new-profile.jpg")
            .build();
        
        UpdateProfileResponse expectedResponse = UpdateProfileResponse.builder()
            .customerId(customerId)
            .name("새로운 이름")
            .profileImageUrl("https://example.com/new-profile.jpg")
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerProfileMapper.toUpdateProfileResponse(customer)).thenReturn(expectedResponse);
        
        UpdateProfileResponse response = customerProfileService.updateProfile(customerId, request);
        
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("새로운 이름");
        
        verify(customerRepository).save(customer);
    }
    
    @Test
    @DisplayName("위치 정보 업데이트 성공")
    void updateLocation_Success() {
        UpdateLocationRequest request = UpdateLocationRequest.builder()
            .address("서울시 송파구")
            .latitude(37.5145)
            .longitude(127.1058)
            .build();
        
        UpdateLocationResponse expectedResponse = UpdateLocationResponse.builder()
            .customerId(customerId)
            .address("서울시 송파구")
            .latitude(37.5145)
            .longitude(127.1058)
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerProfileMapper.toUpdateLocationResponse(customer)).thenReturn(expectedResponse);
        
        UpdateLocationResponse response = customerProfileService.updateLocation(customerId, request);
        
        assertThat(response).isNotNull();
        assertThat(response.address()).isEqualTo("서울시 송파구");
        
        verify(customerRepository).save(customer);
    }
    
    @Test
    @DisplayName("유효하지 않은 위치 정보로 업데이트 시 예외 발생")
    void updateLocation_InvalidLocation() {
        UpdateLocationRequest request = UpdateLocationRequest.builder()
            .address("서울시 송파구")
            .latitude(91.0)
            .longitude(127.1058)
            .build();
        
        assertThatThrownBy(() -> customerProfileService.updateLocation(customerId, request))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> {
                BusinessException businessEx = (BusinessException) ex;
                assertThat(businessEx.getErrorCode()).isEqualTo(ErrorCode.CUSTOMER_LOCATION_INVALID);
            });
    }
    
    @Test
    @DisplayName("고객 통계 업데이트 성공")
    void updateCustomerStats_Success() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        customerProfileService.updateCustomerStats(customerId, 2, 5000L, 2.5);
        
        verify(customerRepository).save(customer);
    }
}