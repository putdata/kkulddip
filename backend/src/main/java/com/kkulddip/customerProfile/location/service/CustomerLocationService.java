package com.kkulddip.customerProfile.location.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.customerProfile.location.cache.CustomerLocationCacheService;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import com.kkulddip.customerProfile.location.dto.LocationDistanceResponse;
import com.kkulddip.customerProfile.location.dto.UpdateRealtimeLocationRequest;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.store.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerLocationService {
    
    private final CustomerLocationCacheService locationCacheService;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    
    private static final double NEARBY_THRESHOLD_KM = 0.1;
    private static final long MIN_UPDATE_INTERVAL_SECONDS = 30;
    
    /**
     * 고객의 실시간 위치 업데이트
     * 최소 업데이트 간격을 체크하여 과도한 업데이트 방지
     */
    @Transactional
    public CustomerLocationDto updateRealtimeLocation(Long customerId, UpdateRealtimeLocationRequest request) {
        // 고객 존재 여부 확인
        Customer customer = findCustomerById(customerId);
        
        // 이전 위치 정보 조회
        Optional<CustomerLocationDto> previousLocation = locationCacheService.getLocation(customerId);
        
        // 최소 업데이트 간격 체크
        if (previousLocation.isPresent()) {
            long secondsSinceLastUpdate = ChronoUnit.SECONDS.between(
                previousLocation.get().timestamp(), 
                LocalDateTime.now()
            );
            
            if (secondsSinceLastUpdate < MIN_UPDATE_INTERVAL_SECONDS) {
                log.debug("위치 업데이트 간격이 너무 짧음 - customerId: {}, interval: {}s", 
                    customerId, secondsSinceLastUpdate);
                return previousLocation.get();
            }
        }
        
        // 위치 유효성 검증
        if (!DistanceCalculator.isValidCoordinate(request.latitude(), request.longitude())) {
            throw new BusinessException(ErrorCode.CUSTOMER_LOCATION_INVALID);
        }
        
        // 새 위치 정보 생성 및 저장
        CustomerLocationDto newLocation = CustomerLocationDto.builder()
            .customerId(customerId)
            .latitude(request.latitude())
            .longitude(request.longitude())
            .accuracy(request.accuracy())
            .timestamp(LocalDateTime.now())
            .deviceInfo(request.deviceInfo())
            .isLocationSharingEnabled(true)
            .build();
        
        locationCacheService.saveLocation(newLocation);
        
        // 고객 마지막 활동 시간 업데이트
        customer.updateLastActiveAt();
        customerRepository.save(customer);
        
        log.info("고객 실시간 위치 업데이트 - customerId: {}, lat: {}, lng: {}", 
            customerId, request.latitude(), request.longitude());
        
        return newLocation;
    }
    
    /**
     * 고객의 현재 위치 조회
     */
    public Optional<CustomerLocationDto> getCustomerLocation(Long customerId) {
        Optional<CustomerLocationDto> location = locationCacheService.getLocation(customerId);
        
        if (location.isPresent()) {
            log.debug("고객 위치 조회 성공 - customerId: {}", customerId);
        } else {
            log.debug("고객 위치 정보 없음 - customerId: {}", customerId);
        }
        
        return location;
    }
    
    /**
     * 고객과 가게 간 거리 계산
     */
    public LocationDistanceResponse calculateDistanceToStore(Long customerId, Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + storeId));
        
        // Redis GeoSpatial을 사용한 거리 계산
        Double distance = locationCacheService.getDistanceToPoint(
            customerId, 
            store.getLatitude(), 
            store.getLongitude()
        );
        
        if (distance == null) {
            // Redis에 위치 정보가 없는 경우 fallback
            Optional<CustomerLocationDto> location = getCustomerLocation(customerId);
            if (location.isPresent()) {
                distance = DistanceCalculator.calculateDistance(
                    location.get().latitude(),
                    location.get().longitude(),
                    store.getLatitude(),
                    store.getLongitude()
                );
            }
        }
        
        LocationDistanceResponse response = LocationDistanceResponse.of(customerId, distance);
        
        log.debug("고객-가게 거리 계산 - customerId: {}, storeId: {}, distance: {}km", 
            customerId, storeId, distance);
        
        return response;
    }
    
    /**
     * 고객이 가게 근처에 있는지 확인
     */
    public boolean isCustomerNearStore(Long customerId, Long storeId) {
        LocationDistanceResponse distance = calculateDistanceToStore(customerId, storeId);
        
        boolean isNearby = distance.distance() != null && 
                          distance.distance() <= NEARBY_THRESHOLD_KM;
        
        if (isNearby) {
            log.info("고객이 가게 근처에 도착 - customerId: {}, storeId: {}", customerId, storeId);
        }
        
        return isNearby;
    }
    
    /**
     * 특정 가게 주변의 고객들 조회
     */
    public List<Long> getCustomersNearStore(Long storeId, Double radiusKm) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + storeId));
        
        List<Long> nearbyCustomers = locationCacheService.getNearbyCustomers(
            store.getLatitude(),
            store.getLongitude(),
            radiusKm
        );
        
        log.info("가게 주변 고객 조회 - storeId: {}, radius: {}km, count: {}", 
            storeId, radiusKm, nearbyCustomers.size());
        
        return nearbyCustomers;
    }
    
    /**
     * 고객 위치 공유 중지 (위치 정보 삭제)
     */
    @Transactional
    public void stopLocationSharing(Long customerId) {
        locationCacheService.deleteLocation(customerId);
        
        log.info("고객 위치 공유 중지 - customerId: {}", customerId);
    }
    
    /**
     * 두 고객 간 거리 계산
     */
    public Double getDistanceBetweenCustomers(Long customerId1, Long customerId2) {
        Double distance = locationCacheService.getDistanceBetweenCustomers(customerId1, customerId2);
        
        if (distance != null) {
            log.debug("고객 간 거리 - customer1: {}, customer2: {}, distance: {}km", 
                customerId1, customerId2, distance);
        }
        
        return distance;
    }
    
    /**
     * 활성 위치 공유 중인 고객 수 조회
     */
    public Long getActiveLocationSharingCount() {
        return locationCacheService.getActiveLocationCount();
    }
    
    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
    }
}