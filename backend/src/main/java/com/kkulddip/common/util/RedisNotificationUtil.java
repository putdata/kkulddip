package com.kkulddip.common.util;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis를 사용한 푸시 알림 범용 유틸리티
 * 
 * <p>모든 도메인에서 사용할 수 있는 푸시 알림 발행 기능을 제공합니다.</p>
 * 
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Component
public class RedisNotificationUtil {

    private static final String NOTIFICATION_QUEUE_KEY = "notification_request";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisNotificationUtil(
        @Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
        @Qualifier("commonObjectMapper") ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 알림 요청을 Redis ZSet에 발행
     * 
     * @param request 알림 요청 정보
     * @throws RuntimeException JSON 직렬화 실패 또는 Redis 오류 시
     */
    public void publishNotification(NotificationRequest request) {
        try {
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(LocalDateTime.now());
            }

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
            throw new RuntimeException("알림 요청 직렬화 실패", e);
        } catch (Exception e) {
            log.error("Redis ZSet 알림 발행 실패 - operation: publishNotification, details: {}", e.getMessage(), e);
            throw new RuntimeException("Redis 알림 발행 실패", e);
        }
    }

    /**
     * 고객에게 알림 발행
     * 
     * @param customerId 고객 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
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
            .publisherId(null)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(customerId)
            .subscriberType(SubscriberType.CUSTOMER)
            .notificationType(notificationType)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 특정 사장에게 알림 발행
     * 
     * @param ownerId 사장 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
     */
    public void publishOwnerNotification(
        Long ownerId, 
        String title, 
        String content, 
        NotificationType notificationType
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(ownerId)
            .subscriberType(SubscriberType.OWNER)
            .notificationType(notificationType)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 가게에 알림 발행 (해당 가게의 모든 사장들에게 전송)
     * 
     * @param storeId 가게 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
     */
    public void publishStoreNotification(
        Long storeId, 
        String title, 
        String content, 
        NotificationType notificationType
    ) {
        NotificationRequest request = NotificationRequest.builder()
            .title(title)
            .content(content)
            .publisherId(null)
            .publisherType(PublisherType.SYSTEM)
            .subscriberId(storeId)
            .subscriberType(SubscriberType.STORE)
            .notificationType(notificationType)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 액션 URL이 포함된 고객 알림 발행
     * 
     * @param customerId 고객 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
     * @param actionUrl 액션 URL
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
     * 
     * @param ownerId 사장 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
     * @param actionUrl 액션 URL
     */
    public void publishOwnerNotificationWithAction(
        Long ownerId, 
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
            .subscriberId(ownerId)
            .subscriberType(SubscriberType.OWNER)
            .notificationType(notificationType)
            .actionUrl(actionUrl)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

    /**
     * 액션 URL이 포함된 가게 알림 발행
     * 
     * @param storeId 가게 ID
     * @param title 알림 제목
     * @param content 알림 내용
     * @param notificationType 알림 타입
     * @param actionUrl 액션 URL
     */
    public void publishStoreNotificationWithAction(
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
            .subscriberType(SubscriberType.STORE)
            .notificationType(notificationType)
            .actionUrl(actionUrl)
            .createdAt(LocalDateTime.now())
            .build();

        publishNotification(request);
    }

}