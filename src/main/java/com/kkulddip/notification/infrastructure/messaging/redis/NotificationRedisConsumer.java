package com.kkulddip.notification.infrastructure.messaging.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.infrastructure.messaging.redis.processor.NotificationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis ZSet에서 알림 요청을 소비하는 컨슈머
 *
 * <p>1초마다 스케줄링되어 Redis ZSet에서 알림 요청을 폴링하고 처리합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Component
public class NotificationRedisConsumer {

    private static final String NOTIFICATION_QUEUE_KEY = "notification_request";
    private static final int BATCH_SIZE = 100;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationProcessor notificationProcessor;

    public NotificationRedisConsumer(
        @Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
        @Qualifier("commonObjectMapper") ObjectMapper objectMapper,
        NotificationProcessor notificationProcessor) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.notificationProcessor = notificationProcessor;
    }

    /**
     * 1초마다 Redis ZSet에서 알림 요청을 폴링합니다.
     */
    @Scheduled(fixedRate = 1000)
    public void consumeNotificationRequests() {
        try {
            long currentTime = System.currentTimeMillis();
            processNotificationQueue(currentTime);
        } catch (Exception e) {
            log.error("알림 요청 소비 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 알림 큐를 처리합니다.
     *
     * @param currentTime 현재 시간 (밀리초)
     */
    private void processNotificationQueue(long currentTime) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        
        // 현재 시간 이전의 스케줄된 알림들을 가져옴
        Set<ZSetOperations.TypedTuple<Object>> notifications = zSetOps
            .rangeByScoreWithScores(NOTIFICATION_QUEUE_KEY, 0, currentTime, 0, BATCH_SIZE);

        if (notifications == null || notifications.isEmpty()) {
            log.trace("처리할 알림 요청이 없습니다.");
            return;
        }

        log.info("Redis ZSet에서 {} 개의 알림 요청을 가져왔습니다.", notifications.size());

        for (ZSetOperations.TypedTuple<Object> tuple : notifications) {
            processNotificationTuple(tuple, zSetOps);
        }
    }

    /**
     * 개별 알림 튜플을 처리합니다.
     *
     * @param tuple 알림 튜플
     * @param zSetOps ZSet 연산 객체
     */
    private void processNotificationTuple(
        ZSetOperations.TypedTuple<Object> tuple, 
        ZSetOperations<String, Object> zSetOps) {
        
        try {
            Object value = tuple.getValue();
            Double score = tuple.getScore();
            
            if (value == null) {
                log.warn("빈 알림 요청을 건너뜁니다.");
                return;
            }

            // JSON 문자열을 NotificationRequest로 역직렬화
            NotificationRequest request = objectMapper.readValue(
                value.toString(), 
                NotificationRequest.class
            );

            log.debug("알림 요청 처리 시작 - id: {}, title: {}, score: {}", 
                request.getId(), request.getTitle(), score);

            // 알림 처리
            boolean processed = notificationProcessor.processNotification(request);

            // 성공/실패 관계없이 ZSet에서 제거
            zSetOps.remove(NOTIFICATION_QUEUE_KEY, value);
            
            if (processed) {
                log.info("알림 요청 처리 완료 및 제거 - id: {}", request.getId());
            } else {
                log.error("알림 요청 처리 실패 후 제거 - id: {}, title: {}, subscriberType: {}, subscriberId: {}", 
                    request.getId(), request.getTitle(), request.getSubscriberType(), request.getSubscriberId());
            }

        } catch (Exception e) {
            log.error("알림 요청 처리 중 오류 발생 - value: {}, score: {}, error: {}", 
                tuple.getValue(), tuple.getScore(), e.getMessage(), e);
            
            // 오류 발생한 요청은 ZSet에서 제거 (무한 재시도 방지)
            zSetOps.remove(NOTIFICATION_QUEUE_KEY, tuple.getValue());
        }
    }


    /**
     * Redis ZSet의 현재 상태를 모니터링합니다.
     */
    @Scheduled(fixedRate = 60000) // 1분마다
    public void monitorQueue() {
        try {
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            Long queueSize = zSetOps.zCard(NOTIFICATION_QUEUE_KEY);
            
            if (queueSize != null && queueSize > 0) {
                log.info("알림 요청 큐 모니터링 - 대기 중인 요청 수: {}", queueSize);
            }

            // 큐가 너무 크면 경고 로그
            if (queueSize != null && queueSize > 1000) {
                log.warn("알림 요청 큐가 너무 큽니다 - 대기 중인 요청 수: {}", queueSize);
            }
            
        } catch (Exception e) {
            log.error("알림 큐 모니터링 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}