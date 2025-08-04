package com.kkulddip.payment.infrastructure.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.payment.interfaces.dto.request.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisOrderRequestService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ORDER_REQUEST_ZSET_KEY = "order_request";

    public Set<OrderRequestDto> getOrderRequests(int count) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        
        // 현재 시간보다 작거나 같은 score를 가진 요소들을 조회 (처리할 시간이 된 요청들)
        long currentTime = System.currentTimeMillis();
        Set<String> orderRequestJsons = zSetOps.rangeByScore(ORDER_REQUEST_ZSET_KEY, 0, currentTime, 0, count);
        
        return orderRequestJsons.stream()
                .map(this::parseOrderRequest)
                .filter(dto -> dto != null)
                .collect(java.util.stream.Collectors.toSet());
    }

    public void removeOrderRequest(String orderId) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        
        // orderId로 해당 요청을 찾아서 제거
        Set<String> allRequests = zSetOps.range(ORDER_REQUEST_ZSET_KEY, 0, -1);
        
        for (String requestJson : allRequests) {
            OrderRequestDto dto = parseOrderRequest(requestJson);
            if (dto != null && orderId.equals(dto.orderId())) {
                zSetOps.remove(ORDER_REQUEST_ZSET_KEY, requestJson);
                log.info("Redis에서 주문 요청 제거: orderId={}", orderId);
                break;
            }
        }
    }

    private OrderRequestDto parseOrderRequest(String json) {
        try {
            return objectMapper.readValue(json, OrderRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("주문 요청 JSON 파싱 오류: {}", e.getMessage());
            return null;
        }
    }
}