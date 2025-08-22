package com.kkulddip.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOCK_PREFIX = "lock:";
    private static final Duration DEFAULT_TTL = Duration.ofSeconds(30);

    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_TTL);
    }

    public boolean tryLock(String key, Duration ttl) {
        String lockKey = LOCK_PREFIX + key;
        
        log.debug("🔒 분산 락 획득 시도: key={}", lockKey);
        
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "1", ttl);
        
        if (Boolean.TRUE.equals(acquired)) {
            log.debug("✅ 분산 락 획득 성공: key={}", lockKey);
            return true;
        } else {
            log.debug("❌ 분산 락 획득 실패: key={} (이미 사용 중)", lockKey);
            return false;
        }
    }

    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        
        try {
            Boolean deleted = redisTemplate.delete(lockKey);
            
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("🔓 분산 락 해제 성공: key={}", lockKey);
            } else {
                log.debug("⚠️ 분산 락 해제 실패: key={} (이미 만료됨)", lockKey);
            }
            
        } catch (Exception e) {
            log.error("분산 락 해제 중 오류 발생: key={}, error={}", lockKey, e.getMessage());
        }
    }
}