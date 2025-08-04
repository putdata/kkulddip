package com.kkulddip.notification.infrastructure.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.notification.interfaces.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisNotificationQueueService {

    private static final String NOTIFICATION_QUEUE_KEY = "notification_request";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis ZSet에서 알림 요청 조회 (가장 오래된 것부터)
     */
    public Set<NotificationRequest> popNotificationRequests(int count) {
        try {
            // ZSet에서 점수가 낮은 순서대로 조회 (가장 오래된 것부터)
            Set<Object> rawRequests = redisTemplate.opsForZSet()
                .range(NOTIFICATION_QUEUE_KEY, 0, count - 1);

            if (rawRequests == null || rawRequests.isEmpty()) {
                return Set.of();
            }

            // JSON 문자열을 NotificationRequest 객체로 변환
            Set<NotificationRequest> requests = rawRequests.stream()
                .map(this::deserializeNotificationRequest)
                .filter(request -> request != null)
                .collect(toSet());

            // 처리한 요청들을 ZSet에서 제거
            Object[] rawRequestsArray = rawRequests.toArray();
            redisTemplate.opsForZSet().remove(NOTIFICATION_QUEUE_KEY, rawRequestsArray);
            log.info("Redis ZSet에서 {}개 알림 요청 조회 및 제거", requests.size());

            return requests;

        } catch (Exception e) {
            log.error("Redis ZSet에서 알림 요청 조회 실패: {}", e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Redis ZSet의 크기 조회
     */
    public long getQueueSize() {
        try {
            Long size = redisTemplate.opsForZSet().zCard(NOTIFICATION_QUEUE_KEY);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Redis ZSet 크기 조회 실패: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 테스트용: 알림 요청을 Redis ZSet에 추가
     */
    public void addNotificationRequest(NotificationRequest request) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            double score = request.getScoreTimestamp();

            redisTemplate.opsForZSet().add(NOTIFICATION_QUEUE_KEY, jsonRequest, score);
            log.debug("Redis ZSet에 알림 요청 추가: score={}", score);

        } catch (JsonProcessingException e) {
            log.error("알림 요청 직렬화 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Redis ZSet에 알림 요청 추가 실패: {}", e.getMessage());
        }
    }

    /**
     * JSON 문자열을 NotificationRequest 객체로 변환
     */
    private NotificationRequest deserializeNotificationRequest(Object rawRequest) {
        try {
            if (rawRequest instanceof String jsonString) {
                return objectMapper.readValue(jsonString, NotificationRequest.class);
            }
            log.warn("예상하지 않은 데이터 타입: {}", rawRequest.getClass());
            return null;
        } catch (JsonProcessingException e) {
            log.error("알림 요청 역직렬화 실패: {}, 데이터: {}", e.getMessage(), rawRequest);
            return null;
        }
    }

    /**
     * 개발용: ZSet 전체 내용 조회 (디버깅용)
     */
    public void logQueueContents() {
        try {
            Set<Object> allRequests = redisTemplate.opsForZSet()
                .range(NOTIFICATION_QUEUE_KEY, 0, -1);

            log.info("=== Redis ZSet 내용 (총 {}개) ===", allRequests != null ? allRequests.size() : 0);

            if (allRequests != null) {
                allRequests.forEach(request -> {
                    Double score = redisTemplate.opsForZSet().score(NOTIFICATION_QUEUE_KEY, request);
                    log.info("Score: {}, Data: {}", score, request);
                });
            }

        } catch (Exception e) {
            log.error("Redis ZSet 내용 조회 실패: {}", e.getMessage());
        }
    }
}