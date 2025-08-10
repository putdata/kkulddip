package com.kkulddip.customerProfile.location.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 고객 실시간 위치 정보 Redis 캐시 서비스
 * 
 * Redis의 Hash와 GeoSpatial 데이터 구조를 활용하여 고객의 실시간 위치 정보를 관리합니다.
 * 
 * 데이터 구조:
 * - Hash: "customer:location:{customerId}" → 상세 위치 정보 (JSON 형태, 5분 TTL)
 * - GeoSpatial: "customer:locations:geo" → 거리 계산 및 근처 고객 검색용
 * 
 * 주요 기능:
 * - 실시간 위치 저장/조회 (TTL 5분)
 * - GeoSpatial 연산을 통한 거리 계산
 * - 반경 내 고객 검색
 * - 위치 기반 근접 판정
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerLocationCacheService {
    
    @Qualifier("commonRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Qualifier("commonObjectMapper")
    private final ObjectMapper objectMapper;
    
    private static final String LOCATION_KEY_PREFIX = "customer:location:";
    private static final String GEO_KEY = "customer:locations:geo";
    private static final Duration LOCATION_TTL = Duration.ofMinutes(5);
    
    /**
     * 고객 위치 정보를 Redis에 저장
     * Hash와 GeoSpatial 데이터 모두 저장
     */
    public void saveLocation(CustomerLocationDto location) {
        try {
            String key = LOCATION_KEY_PREFIX + location.customerId();
            String locationJson = objectMapper.writeValueAsString(location);
            
            // Hash로 상세 정보 저장
            redisTemplate.opsForValue().set(key, locationJson, LOCATION_TTL);
            
            // GeoSpatial 데이터 저장 (거리 계산용)
            Point point = new Point(location.longitude(), location.latitude());
            redisTemplate.opsForGeo().add(GEO_KEY, point, location.customerId().toString());
            
            log.debug("고객 위치 정보 저장 완료 - customerId: {}, lat: {}, lng: {}", 
                location.customerId(), location.latitude(), location.longitude());
                
        } catch (JsonProcessingException e) {
            log.error("위치 정보 직렬화 실패 - customerId: {}, error: {}", 
                location.customerId(), e.getMessage());
        }
    }
    
    /**
     * 고객 위치 정보 조회
     */
    public Optional<CustomerLocationDto> getLocation(Long customerId) {
        try {
            String key = LOCATION_KEY_PREFIX + customerId;
            Object locationJson = redisTemplate.opsForValue().get(key);
            
            if (locationJson != null) {
                CustomerLocationDto location = objectMapper.readValue(
                    locationJson.toString(), 
                    CustomerLocationDto.class
                );
                log.debug("고객 위치 정보 조회 성공 - customerId: {}", customerId);
                return Optional.of(location);
            }
            
        } catch (JsonProcessingException e) {
            log.error("위치 정보 역직렬화 실패 - customerId: {}, error: {}", 
                customerId, e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * 특정 지점 주변의 고객들 조회
     * 
     * @param centerLat 중심 위도
     * @param centerLng 중심 경도
     * @param radiusKm 반경 (km)
     * @return 반경 내 고객 ID 목록
     */
    public List<Long> getNearbyCustomers(Double centerLat, Double centerLng, Double radiusKm) {
        Point center = new Point(centerLng, centerLat);
        Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle circle = new Circle(center, radius);
        
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
            redisTemplate.opsForGeo().radius(GEO_KEY, circle);
        
        if (results != null) {
            List<Long> nearbyCustomerIds = results.getContent().stream()
                .map(result -> Long.parseLong(result.getContent().getName().toString()))
                .collect(Collectors.toList());
                
            log.debug("반경 {}km 내 {} 명의 고객 발견 - center: ({}, {})", 
                radiusKm, nearbyCustomerIds.size(), centerLat, centerLng);
                
            return nearbyCustomerIds;
        }
        
        return List.of();
    }
    
    /**
     * 두 고객 간 거리 계산
     * 
     * @param customerId1 첫 번째 고객 ID
     * @param customerId2 두 번째 고객 ID
     * @return 거리 (km), 위치 정보가 없으면 null
     */
    public Double getDistanceBetweenCustomers(Long customerId1, Long customerId2) {
        Distance distance = redisTemplate.opsForGeo().distance(
            GEO_KEY, 
            customerId1.toString(), 
            customerId2.toString(),
            Metrics.KILOMETERS
        );
        
        if (distance != null) {
            log.debug("고객 간 거리 계산 - customer1: {}, customer2: {}, distance: {}km", 
                customerId1, customerId2, distance.getValue());
            return distance.getValue();
        }
        
        return null;
    }
    
    /**
     * 고객과 특정 지점 간 거리 계산
     * 
     * @param customerId 고객 ID
     * @param targetLat 목표 지점 위도
     * @param targetLng 목표 지점 경도
     * @return 거리 (km), 위치 정보가 없으면 null
     */
    public Double getDistanceToPoint(Long customerId, Double targetLat, Double targetLng) {
        // 임시로 목표 지점을 GEO에 추가
        String tempKey = "temp_" + System.currentTimeMillis();
        Point targetPoint = new Point(targetLng, targetLat);
        redisTemplate.opsForGeo().add(GEO_KEY, targetPoint, tempKey);
        
        try {
            Distance distance = redisTemplate.opsForGeo().distance(
                GEO_KEY,
                customerId.toString(),
                tempKey,
                Metrics.KILOMETERS
            );
            
            if (distance != null) {
                log.debug("고객-지점 간 거리 계산 - customerId: {}, target: ({}, {}), distance: {}km",
                    customerId, targetLat, targetLng, distance.getValue());
                return distance.getValue();
            }
            
        } finally {
            // 임시 지점 삭제
            redisTemplate.opsForGeo().remove(GEO_KEY, tempKey);
        }
        
        return null;
    }
    
    /**
     * 고객 위치 정보 삭제
     */
    public void deleteLocation(Long customerId) {
        String key = LOCATION_KEY_PREFIX + customerId;
        
        // Hash 데이터 삭제
        redisTemplate.delete(key);
        
        // GeoSpatial 데이터 삭제
        redisTemplate.opsForGeo().remove(GEO_KEY, customerId.toString());
        
        log.info("고객 위치 정보 삭제 완료 - customerId: {}", customerId);
    }
    
    /**
     * 모든 활성 위치 정보 개수 조회
     */
    public Long getActiveLocationCount() {
        // Redis GeoOperations에는 size() 메서드가 없으므로 ZSet의 크기를 조회
        Long count = redisTemplate.opsForZSet().zCard(GEO_KEY);
        return count != null ? count : 0L;
    }
}