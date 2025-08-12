package com.kkulddip.customerProfile.location.service;

import com.kkulddip.customerProfile.location.cache.CustomerLocationCacheService;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import com.kkulddip.customerProfile.location.dto.LocationDistanceResponse;
import com.kkulddip.customerProfile.location.dto.UpdateRealtimeLocationRequest;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerLocationServiceTest {
    
    @Mock
    private CustomerLocationCacheService locationCacheService;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private StoreRepository storeRepository;
    
    @InjectMocks
    private CustomerLocationService customerLocationService;
    
    private Customer customer;
    private Store store;
    private Long customerId = 1L;
    private Long storeId = 1L;
    
    @BeforeEach
    void setUp() {
        customer = Customer.builder()
            .email("test@example.com")
            .name("테스트 고객")
            .level(CustomerLevel.SPROUT_BEE)
            .build();
            
        store = Store.builder()
            .storeName("테스트 매장")
            .latitude(37.5665)
            .longitude(126.9780)
            .build();
    }
    
    @Test
    @DisplayName("실시간 위치 업데이트 성공")
    void updateRealtimeLocation_Success() {
        UpdateRealtimeLocationRequest request = UpdateRealtimeLocationRequest.builder()
            .latitude(37.5145)
            .longitude(127.1058)
            .accuracy(10.5)
            .deviceInfo("iPhone 14")
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(locationCacheService.getLocation(customerId)).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        CustomerLocationDto result = customerLocationService.updateRealtimeLocation(customerId, request);
        
        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.latitude()).isEqualTo(37.5145);
        assertThat(result.longitude()).isEqualTo(127.1058);
        assertThat(result.accuracy()).isEqualTo(10.5);
        assertThat(result.deviceInfo()).isEqualTo("iPhone 14");
        
        verify(locationCacheService).saveLocation(any(CustomerLocationDto.class));
        verify(customerRepository).save(customer);
    }
    
    @Test
    @DisplayName("최소 업데이트 간격 미만 시 이전 위치 반환")
    void updateRealtimeLocation_TooFrequent() {
        UpdateRealtimeLocationRequest request = UpdateRealtimeLocationRequest.builder()
            .latitude(37.5145)
            .longitude(127.1058)
            .build();
        
        CustomerLocationDto previousLocation = CustomerLocationDto.builder()
            .customerId(customerId)
            .latitude(37.5000)
            .longitude(127.0000)
            .timestamp(LocalDateTime.now().minusSeconds(10))
            .build();
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(locationCacheService.getLocation(customerId)).thenReturn(Optional.of(previousLocation));
        
        CustomerLocationDto result = customerLocationService.updateRealtimeLocation(customerId, request);
        
        assertThat(result).isEqualTo(previousLocation);
        verify(locationCacheService, never()).saveLocation(any());
    }
    
    @Test
    @DisplayName("고객 위치 조회 성공")
    void getCustomerLocation_Success() {
        CustomerLocationDto expectedLocation = CustomerLocationDto.builder()
            .customerId(customerId)
            .latitude(37.5665)
            .longitude(126.9780)
            .timestamp(LocalDateTime.now())
            .build();
        
        when(locationCacheService.getLocation(customerId)).thenReturn(Optional.of(expectedLocation));
        
        Optional<CustomerLocationDto> result = customerLocationService.getCustomerLocation(customerId);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedLocation);
    }
    
    @Test
    @DisplayName("가게까지 거리 계산 성공")
    void calculateDistanceToStore_Success() {
        Double expectedDistance = 2.5;
        
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(locationCacheService.getDistanceToPoint(customerId, store.getLatitude(), store.getLongitude()))
            .thenReturn(expectedDistance);
        
        LocationDistanceResponse result = customerLocationService.calculateDistanceToStore(customerId, storeId);
        
        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.distance()).isEqualTo(expectedDistance);
        assertThat(result.estimatedTimeInMinutes()).isEqualTo(8);
        assertThat(result.isNearby()).isFalse();
    }
    
    @Test
    @DisplayName("고객이 가게 근처에 있는지 확인 - 근처에 있음")
    void isCustomerNearStore_Nearby() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(locationCacheService.getDistanceToPoint(customerId, store.getLatitude(), store.getLongitude()))
            .thenReturn(0.05);
        
        boolean result = customerLocationService.isCustomerNearStore(customerId, storeId);
        
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("고객이 가게 근처에 있는지 확인 - 멀리 있음")
    void isCustomerNearStore_Far() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(locationCacheService.getDistanceToPoint(customerId, store.getLatitude(), store.getLongitude()))
            .thenReturn(1.5);
        
        boolean result = customerLocationService.isCustomerNearStore(customerId, storeId);
        
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("가게 주변 고객 조회 성공")
    void getCustomersNearStore_Success() {
        List<Long> expectedCustomers = List.of(1L, 2L, 3L);
        Double radius = 1.0;
        
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(locationCacheService.getNearbyCustomers(store.getLatitude(), store.getLongitude(), radius))
            .thenReturn(expectedCustomers);
        
        List<Long> result = customerLocationService.getCustomersNearStore(storeId, radius);
        
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedCustomers);
    }
    
    @Test
    @DisplayName("위치 공유 중지 성공")
    void stopLocationSharing_Success() {
        customerLocationService.stopLocationSharing(customerId);
        
        verify(locationCacheService).deleteLocation(customerId);
    }
    
    @Test
    @DisplayName("두 고객 간 거리 계산 성공")
    void getDistanceBetweenCustomers_Success() {
        Long customerId2 = 2L;
        Double expectedDistance = 1.2;
        
        when(locationCacheService.getDistanceBetweenCustomers(customerId, customerId2))
            .thenReturn(expectedDistance);
        
        Double result = customerLocationService.getDistanceBetweenCustomers(customerId, customerId2);
        
        assertThat(result).isEqualTo(expectedDistance);
    }
    
    @Test
    @DisplayName("활성 위치 공유 중인 고객 수 조회")
    void getActiveLocationSharingCount_Success() {
        Long expectedCount = 15L;
        
        when(locationCacheService.getActiveLocationCount()).thenReturn(expectedCount);
        
        Long result = customerLocationService.getActiveLocationSharingCount();
        
        assertThat(result).isEqualTo(expectedCount);
    }
}