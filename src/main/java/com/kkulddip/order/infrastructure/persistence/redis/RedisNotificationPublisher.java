package com.kkulddip.order.infrastructure.persistence.redis;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.order.application.exception.OrderException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisNotificationPublisher {

    private static final String NOTIFICATION_QUEUE_KEY = "notification_request";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisNotificationPublisher(
        @Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
        @Qualifier("commonObjectMapper") ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 알림 요청을 Redis ZSet에 발행
     */
    public void publishNotification(NotificationRequest request) {
        try {
            // 생성 시간이 없으면 현재 시간으로 설정
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(LocalDateTime.now());
            }

            // ID가 없으면 UUID 생성
            if (request.getId() == null) {
                request.setId(UUID.randomUUID().toString());
            }

            String jsonRequest = objectMapper.writeValueAsString(request);
            double score = request.getScoreTimestamp();

            redisTemplate.opsForZSet().add(NOTIFICATION_QUEUE_KEY, jsonRequest, score);
            
            log.info("알림 요청을 Redis ZSet에 발행 완료 - id: {}, subscriberType: {}, subscriberId: {}, score: {}", 
                request.getId(), request.getSubscriberType(), request.getSubscriberId(), score);

        } catch (JsonProcessingException e) {
            log.error("Redis 알림 요청 직렬화 실패 - operation: publishNotification, details: {}", e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        } catch (Exception e) {
            log.error("Redis ZSet 알림 발행 실패 - operation: publishNotification, details: {}", e.getMessage(), e);
            throw OrderException.orderExternalApiError(e);
        }
    }

    /**
     * 고객에게 알림 발행
     */
    public void publishCustomerNotification(
        Long customerId, 
        String title, 
        String content, 
        NotificationType notificationType
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null) // ORDER_SERVICE의 ID (필요시 설정)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(customerId)
            .subscriberType(SubscriberType.CUSTOMER)
            .notificationType(notificationType)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 가게 사장에게 알림 발행
     */
    public void publishOwnerNotification(
        Long storeId, 
        String title, 
        String content, 
        NotificationType notificationType
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null) // ORDER_SERVICE의 ID (필요시 설정)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(storeId)
            .subscriberType(SubscriberType.OWNER)
            .notificationType(notificationType)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 액션 URL이 포함된 고객 알림 발행
     */
    public void publishCustomerNotificationWithAction(
        Long customerId, 
        String title, 
        String content, 
        NotificationType notificationType,
        String actionUrl
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(customerId)
            .subscriberType(SubscriberType.CUSTOMER)
            .notificationType(notificationType)
            .actionUrl(actionUrl)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 액션 URL이 포함된 사장 알림 발행
     */
    public void publishOwnerNotificationWithAction(
        Long storeId, 
        String title, 
        String content, 
        NotificationType notificationType,
        String actionUrl
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(storeId)
            .subscriberType(SubscriberType.OWNER)
            .notificationType(notificationType)
            .actionUrl(actionUrl)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }
}