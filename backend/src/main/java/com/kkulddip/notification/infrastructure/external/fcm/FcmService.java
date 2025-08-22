package com.kkulddip.notification.infrastructure.external.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.kkulddip.notification.infrastructure.external.fcm.dto.FcmBatchResult;
import com.kkulddip.notification.infrastructure.external.fcm.dto.FcmResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Firebase Cloud Messaging 서비스
 *
 * <p>FCM을 통해 푸시 알림을 발송합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * 단일 디바이스에 알림을 발송합니다.
     *
     * @param fcmToken FCM 토큰
     * @param title 알림 제목
     * @param content 알림 내용
     * @param actionUrl 액션 URL (선택사항)
     * @return 발송 결과
     */
    public FcmResult sendNotification(String fcmToken, String title, String content, String actionUrl) {
        try {
            log.debug("FCM 알림 발송 시작 - token: {}, title: {}", maskToken(fcmToken), title);

            Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build()
                );

            // 액션 URL이 있으면 데이터로 추가
            if (actionUrl != null && !actionUrl.trim().isEmpty()) {
                messageBuilder.putData("actionUrl", actionUrl);
            }

            String response = firebaseMessaging.send(messageBuilder.build());
            
            log.info("FCM 알림 발송 성공 - response: {}", response);
            return FcmResult.success(response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 발송 실패 - token: {}, title: {}, error: {}", 
                maskToken(fcmToken), title, e.getMessage(), e);
            return FcmResult.failure(e.getMessage());
        }
    }

    /**
     * 여러 디바이스에 동일한 알림을 발송합니다.
     *
     * @param fcmTokens FCM 토큰 리스트
     * @param title 알림 제목
     * @param content 알림 내용
     * @param actionUrl 액션 URL (선택사항)
     * @return 배치 발송 결과
     */
    public FcmBatchResult sendMulticastNotification(
        List<String> fcmTokens, 
        String title, 
        String content, 
        String actionUrl) {
        
        if (fcmTokens == null || fcmTokens.isEmpty()) {
            log.warn("FCM 토큰 리스트가 비어있습니다.");
            return FcmBatchResult.empty();
        }

        try {
            log.debug("FCM 배치 알림 발송 시작 - tokenCount: {}, title: {}", fcmTokens.size(), title);

            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build()
                );

            // 액션 URL이 있으면 데이터로 추가
            if (actionUrl != null && !actionUrl.trim().isEmpty()) {
                messageBuilder.putData("actionUrl", actionUrl);
            }

            BatchResponse response = firebaseMessaging.sendMulticast(messageBuilder.build());

            log.info("FCM 배치 알림 발송 완료 - successCount: {}, failureCount: {}", 
                response.getSuccessCount(), response.getFailureCount());

            return FcmBatchResult.fromBatchResponse(response, fcmTokens);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 배치 알림 발송 실패 - tokenCount: {}, title: {}, error: {}", 
                fcmTokens.size(), title, e.getMessage(), e);
            return FcmBatchResult.failure(e.getMessage());
        }
    }

    /**
     * 커스텀 데이터와 함께 알림을 발송합니다.
     *
     * @param fcmToken FCM 토큰
     * @param title 알림 제목
     * @param content 알림 내용
     * @param data 커스텀 데이터
     * @return 발송 결과
     */
    public FcmResult sendNotificationWithData(
        String fcmToken, 
        String title, 
        String content, 
        Map<String, String> data) {
        
        try {
            log.debug("FCM 커스텀 데이터 알림 발송 시작 - token: {}, title: {}", 
                maskToken(fcmToken), title);

            Message.Builder messageBuilder = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build()
                );

            // 커스텀 데이터 추가
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = firebaseMessaging.send(messageBuilder.build());
            
            log.info("FCM 커스텀 데이터 알림 발송 성공 - response: {}", response);
            return FcmResult.success(response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 커스텀 데이터 알림 발송 실패 - token: {}, title: {}, error: {}", 
                maskToken(fcmToken), title, e.getMessage(), e);
            return FcmResult.failure(e.getMessage());
        }
    }

    /**
     * FCM 토큰을 마스킹합니다 (로깅용).
     *
     * @param token FCM 토큰
     * @return 마스킹된 토큰
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 5) + "***" + token.substring(token.length() - 5);
    }
}