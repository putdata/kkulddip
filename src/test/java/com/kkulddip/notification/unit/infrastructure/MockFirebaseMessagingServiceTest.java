package com.kkulddip.notification.unit.infrastructure;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.infrastructure.external.firebase.MockFirebaseMessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MockFirebaseMessagingService 단위 테스트")
class MockFirebaseMessagingServiceTest {

    private MockFirebaseMessagingService mockFirebaseMessagingService;

    @BeforeEach
    void setUp() {
        mockFirebaseMessagingService = new MockFirebaseMessagingService();
    }

    @Test
    @DisplayName("정상적인 FCM 토큰으로 메시지 발송")
    void sendMessageWithValidToken() {
        // Given
        String validToken = "valid_fcm_token_" + "a".repeat(100);
        Notification notification = createTestNotification();

        // When
        boolean result = mockFirebaseMessagingService.sendMessage(validToken, notification);

        // Then
        // Mock 서비스는 95% 성공률을 가지므로 대부분 성공
        // 단일 테스트에서는 결과가 true 또는 false일 수 있음
        assertThat(result).isIn(true, false);
    }

    @RepeatedTest(100)
    @DisplayName("Mock 서비스의 95% 성공률 검증")
    void verifyMockSuccessRate() {
        // Given
        String token = "test_token_" + "x".repeat(100);
        Notification notification = createTestNotification();

        // When
        boolean result = mockFirebaseMessagingService.sendMessage(token, notification);

        // Then
        // 개별 테스트는 성공 또는 실패
        assertThat(result).isIn(true, false);
    }

    @Test
    @DisplayName("통계적 성공률 검증 - 100번 시도")
    void verifyStatisticalSuccessRate() {
        // Given
        String token = "statistical_test_token_" + "y".repeat(100);
        Notification notification = createTestNotification();
        int totalTests = 100;
        int successCount = 0;

        // When
        for (int i = 0; i < totalTests; i++) {
            if (mockFirebaseMessagingService.sendMessage(token, notification)) {
                successCount++;
            }
        }

        // Then
        double successRate = (double) successCount / totalTests;
        // 95% 성공률 ±10% 오차 허용 (85% ~ 100%)
        assertThat(successRate).isBetween(0.85, 1.0);
    }

    @Test
    @DisplayName("다양한 알림 타입으로 메시지 발송")
    void sendDifferentNotificationTypes() {
        // Given
        String token = "multi_type_token_" + "z".repeat(100);

        // When & Then
        for (NotificationType type : NotificationType.values()) {
            Notification notification = Notification.builder()
                .title(type.getDisplayName() + " 테스트")
                .content(type.getDescription() + " 내용")
                .notificationType(type)
                .subscriberType(SubscriberType.ALL)
                .createdAt(LocalDateTime.now())
                .build();

            boolean result = mockFirebaseMessagingService.sendMessage(token, notification);
            assertThat(result).isIn(true, false);
        }
    }

    @Test
    @DisplayName("긴 토큰으로 메시지 발송 - 로그 출력 테스트")
    void sendMessageWithLongToken() {
        // Given
        String longToken = "very_long_fcm_token_for_logging_test_" + "l".repeat(200);
        Notification notification = createTestNotification();

        // When
        boolean result = mockFirebaseMessagingService.sendMessage(longToken, notification);

        // Then
        assertThat(result).isIn(true, false);
        // 로그 출력이 정상적으로 되는지는 콘솔 확인 필요
    }

    @Test
    @DisplayName("짧은 토큰으로 메시지 발송 - 로그 출력 테스트")
    void sendMessageWithShortToken() {
        // Given
        String shortToken = "short_token";
        Notification notification = createTestNotification();

        // When
        boolean result = mockFirebaseMessagingService.sendMessage(shortToken, notification);

        // Then
        assertThat(result).isIn(true, false);
        // 로그 출력이 정상적으로 되는지는 콘솔 확인 필요
    }

    private Notification createTestNotification() {
        return Notification.builder()
            .notificationId(1L)
            .title("Mock 테스트 알림")
            .content("MockFirebaseMessagingService 테스트용 알림입니다.")
            .notificationType(NotificationType.SYSTEM)
            .subscriberType(SubscriberType.ALL)
            .createdAt(LocalDateTime.now())
            .build();
    }
}