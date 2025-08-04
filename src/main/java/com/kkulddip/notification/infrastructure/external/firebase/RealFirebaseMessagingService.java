package com.kkulddip.notification.infrastructure.external.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.infrastructure.config.FirebaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "app.notification.mock-mode", havingValue = "false", matchIfMissing = true)
public class RealFirebaseMessagingService implements FirebaseMessagingService {

    private final FirebaseConfig firebaseConfig;

    @Override
    public boolean sendMessage(String fcmToken, Notification notification) {
        try {
            // Firebase 초기화 확인
            if (!firebaseConfig.isFirebaseInitialized()) {
                log.error("❌ Firebase가 초기화되지 않았습니다: {}", notification.getTitle());
                return false;
            }

            // FCM 메시지 구성
            Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                    .setTitle(notification.getTitle())
                    .setBody(notification.getContent())
                    .build());


            Message message = messageBuilder.build();

            // FCM 발송
            String response = FirebaseMessaging.getInstance().send(message);

            log.info("🔥 실제 FCM 발송 성공: {} -> {} (messageId: {})",
                maskToken(fcmToken), notification.getTitle(), response);

            return true;

        } catch (FirebaseMessagingException e) {
            log.error("❌ FCM 발송 실패: {} -> {} (오류: {}, 메시지: {})",
                maskToken(fcmToken), notification.getTitle(), e.getErrorCode(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ FCM 발송 중 예상치 못한 오류: {} -> {}",
                maskToken(fcmToken), notification.getTitle(), e.getMessage());
            return false;
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "***" + token.substring(token.length() - 10);
    }
}