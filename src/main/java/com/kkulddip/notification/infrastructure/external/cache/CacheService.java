package com.kkulddip.notification.infrastructure.external.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

    private final CacheManager cacheManager;

    /**
     * 특정 캐시 클리어
     */
    public void clearCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("캐시 클리어 완료: {}", cacheName);
            } else {
                log.warn("캐시를 찾을 수 없음: {}", cacheName);
            }
        } catch (Exception e) {
            log.error("캐시 클리어 실패: {}, 오류: {}", cacheName, e.getMessage());
        }
    }

    /**
     * 특정 키의 캐시 삭제
     */
    public void evictCache(String cacheName, Object key) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                log.debug("캐시 키 삭제 완료: {}[{}]", cacheName, key);
            }
        } catch (Exception e) {
            log.error("캐시 키 삭제 실패: {}[{}], 오류: {}", cacheName, key, e.getMessage());
        }
    }

    /**
     * 모든 캐시 클리어
     */
    public void clearAllCaches() {
        try {
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    log.info("캐시 클리어: {}", cacheName);
                }
            });
            log.info("모든 캐시 클리어 완료");
        } catch (Exception e) {
            log.error("모든 캐시 클리어 실패: {}", e.getMessage());
        }
    }

    /**
     * 캐시 상태 조회
     */
    public void logCacheStatistics() {
        try {
            log.info("=== 캐시 상태 ===");
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    log.info("캐시명: {}, 타입: {}", cacheName, cache.getClass().getSimpleName());
                }
            });
        } catch (Exception e) {
            log.error("캐시 상태 조회 실패: {}", e.getMessage());
        }
    }

    /**
     * 캐시에서 값 조회
     */
    public <T> T getCacheValue(String cacheName, Object key, Class<T> type) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                var wrapper = cache.get(key, type);
                return wrapper;
            }
        } catch (Exception e) {
            log.error("캐시 값 조회 실패: {}[{}], 오류: {}", cacheName, key, e.getMessage());
        }
        return null;
    }

    /**
     * 캐시에 값 저장
     */
    public void putCacheValue(String cacheName, Object key, Object value) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.put(key, value);
                log.debug("캐시 값 저장: {}[{}]", cacheName, key);
            }
        } catch (Exception e) {
            log.error("캐시 값 저장 실패: {}[{}], 오류: {}", cacheName, key, e.getMessage());
        }
    }
}