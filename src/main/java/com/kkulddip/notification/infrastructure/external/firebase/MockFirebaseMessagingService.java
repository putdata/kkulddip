package com.kkulddip.notification.infrastructure.external.firebase;

import com.kkulddip.notification.domain.model.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.notification.mock-mode", havingValue = "true")
public class MockFirebaseMessagingService implements FirebaseMessagingService {

    @Override
    public boolean sendMessage(String fcmToken, Notification notification) {
        log.info("🎭 FCM 발송 시뮬레이션: {} -> {}",
            maskToken(fcmToken), notification.getTitle());

        // 95% 성공률 시뮬레이션
        return Math.random() < 0.95;
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "***" + token.substring(token.length() - 10);
    }
}