package com.kkulddip.payment.infrastructure.external.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.payment.domain.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String PAYMENT_CACHE_PREFIX = "payment:cache:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public void cachePayment(String paymentKey, Payment payment) {
        try {
            String cacheKey = PAYMENT_CACHE_PREFIX + paymentKey;
            String paymentJson = objectMapper.writeValueAsString(payment);
            
            redisTemplate.opsForValue().set(cacheKey, paymentJson, CACHE_TTL);
            log.debug("결제 정보 캐시 저장: paymentKey={}", paymentKey);
            
        } catch (JsonProcessingException e) {
            log.error("결제 정보 캐시 저장 실패: paymentKey={}, error={}", paymentKey, e.getMessage());
        }
    }

    public Optional<Payment> getCachedPayment(String paymentKey) {
        try {
            String cacheKey = PAYMENT_CACHE_PREFIX + paymentKey;
            String paymentJson = redisTemplate.opsForValue().get(cacheKey);
            
            if (paymentJson != null) {
                Payment payment = objectMapper.readValue(paymentJson, Payment.class);
                log.debug("결제 정보 캐시 조회 성공: paymentKey={}", paymentKey);
                return Optional.of(payment);
            }
            
        } catch (JsonProcessingException e) {
            log.error("결제 정보 캐시 조회 실패: paymentKey={}, error={}", paymentKey, e.getMessage());
        }
        
        return Optional.empty();
    }

    public void evictPaymentCache(String paymentKey) {
        String cacheKey = PAYMENT_CACHE_PREFIX + paymentKey;
        redisTemplate.delete(cacheKey);
        log.debug("결제 정보 캐시 삭제: paymentKey={}", paymentKey);
    }
}