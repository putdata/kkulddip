package com.kkulddip.notification.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 알림 스케줄링 설정
 *
 * <p>Redis ZSet 소비를 위한 스케줄링을 활성화합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class NotificationSchedulingConfig {
    // 스케줄링 활성화를 위한 설정 클래스
    // NotificationRedisConsumer의 @Scheduled 메서드들이 동작하도록 함
}