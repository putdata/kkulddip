package com.kkulddip.customerProfile.location.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerLocationCacheServiceTest {
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private ValueOperations<String, Object> valueOperations;
    
    @Mock
    private GeoOperations<String, Object> geoOperations;
    
    @Mock
    private org.springframework.data.redis.core.ZSetOperations<String, Object> zSetOperations;
    
    @InjectMocks
    private CustomerLocationCacheService cacheService;
    
    private CustomerLocationDto testLocation;
    private Long customerId = 1L;
    
    @BeforeEach
    void setUp() {
        testLocation = CustomerLocationDto.builder()
            .customerId(customerId)
            .latitude(37.5665)
            .longitude(126.9780)
            .accuracy(10.0)
            .timestamp(LocalDateTime.now())
            .deviceInfo("iPhone 14")
            .isLocationSharingEnabled(true)
            .build();
    }
    
    @Test
    @DisplayName("위치 정보 저장 성공")
    void saveLocation_Success() throws Exception {
        String locationJson = "{\"customerId\":1,\"latitude\":37.5665}";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        when(objectMapper.writeValueAsString(testLocation)).thenReturn(locationJson);
        
        cacheService.saveLocation(testLocation);
        
        verify(valueOperations).set(eq("customer:location:1"), eq(locationJson), any());
        verify(geoOperations).add(eq("customer:locations:geo"), any(Point.class), eq("1"));
    }
    
    @Test
    @DisplayName("위치 정보 조회 성공")
    void getLocation_Success() throws Exception {
        String locationJson = "{\"customerId\":1,\"latitude\":37.5665}";
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("customer:location:1")).thenReturn(locationJson);
        when(objectMapper.readValue(locationJson, CustomerLocationDto.class)).thenReturn(testLocation);
        
        Optional<CustomerLocationDto> result = cacheService.getLocation(customerId);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testLocation);
    }
    
    @Test
    @DisplayName("위치 정보 없을 때 빈 Optional 반환")
    void getLocation_NotFound() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("customer:location:1")).thenReturn(null);
        
        Optional<CustomerLocationDto> result = cacheService.getLocation(customerId);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("근처 고객 조회 성공")
    void getNearbyCustomers_Success() {
        Double centerLat = 37.5665;
        Double centerLng = 126.9780;
        Double radiusKm = 1.0;
        
        @SuppressWarnings("unchecked")
        GeoResults<RedisGeoCommands.GeoLocation<Object>> mockResults = mock(GeoResults.class);
        
        @SuppressWarnings("unchecked")
        List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> mockContent = List.of(
            createGeoResult("1"),
            createGeoResult("2"),
            createGeoResult("3")
        );
        
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        when(mockResults.getContent()).thenReturn(mockContent);
        when(geoOperations.radius(eq("customer:locations:geo"), any(Circle.class))).thenReturn(mockResults);
        
        List<Long> result = cacheService.getNearbyCustomers(centerLat, centerLng, radiusKm);
        
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1L, 2L, 3L);
    }
    
    @Test
    @DisplayName("고객 간 거리 계산 성공")
    void getDistanceBetweenCustomers_Success() {
        Long customerId2 = 2L;
        Distance mockDistance = new Distance(1.5, Metrics.KILOMETERS);
        
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        when(geoOperations.distance("customer:locations:geo", "1", "2", Metrics.KILOMETERS))
            .thenReturn(mockDistance);
        
        Double result = cacheService.getDistanceBetweenCustomers(customerId, customerId2);
        
        assertThat(result).isEqualTo(1.5);
    }
    
    @Test
    @DisplayName("특정 지점과의 거리 계산 성공")
    void getDistanceToPoint_Success() {
        Double targetLat = 37.5000;
        Double targetLng = 127.0000;
        Distance mockDistance = new Distance(2.0, Metrics.KILOMETERS);
        
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        when(geoOperations.distance(eq("customer:locations:geo"), eq("1"), anyString(), eq(Metrics.KILOMETERS)))
            .thenReturn(mockDistance);
        
        Double result = cacheService.getDistanceToPoint(customerId, targetLat, targetLng);
        
        assertThat(result).isEqualTo(2.0);
        
        verify(geoOperations).add(eq("customer:locations:geo"), any(Point.class), anyString());
        verify(geoOperations).remove(eq("customer:locations:geo"), anyString());
    }
    
    @Test
    @DisplayName("위치 정보 삭제 성공")
    void deleteLocation_Success() {
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        
        cacheService.deleteLocation(customerId);
        
        verify(redisTemplate).delete("customer:location:1");
        verify(geoOperations).remove("customer:locations:geo", "1");
    }
    
    @Test
    @DisplayName("활성 위치 개수 조회 성공")
    void getActiveLocationCount_Success() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("customer:locations:geo")).thenReturn(10L);
        
        Long result = cacheService.getActiveLocationCount();
        
        assertThat(result).isEqualTo(10L);
    }
    
    @Test
    @DisplayName("활성 위치 개수가 null일 때 0 반환")
    void getActiveLocationCount_NullReturns0() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("customer:locations:geo")).thenReturn(null);
        
        Long result = cacheService.getActiveLocationCount();
        
        assertThat(result).isEqualTo(0L);
    }
    
    @SuppressWarnings("unchecked")
    private GeoResult<RedisGeoCommands.GeoLocation<Object>> createGeoResult(String customerId) {
        RedisGeoCommands.GeoLocation<Object> geoLocation = mock(RedisGeoCommands.GeoLocation.class);
        when(geoLocation.getName()).thenReturn((Object) customerId);
        
        GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult = mock(GeoResult.class);
        when(geoResult.getContent()).thenReturn(geoLocation);
        
        return geoResult;
    }
}