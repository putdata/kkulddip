package com.kkulddip.notification.unit.domain.service;

import com.kkulddip.notification.application.service.NotificationDomainService;
import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.model.status.DeviceType;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.repository.NotificationLogRepository;
import com.kkulddip.notification.infrastructure.external.firebase.FirebaseMessagingService;
import com.kkulddip.notification.infrastructure.external.firebase.MockFirebaseMessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationDomainService 단위 테스트")
class NotificationDomainServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private FirebaseMessagingService firebaseMessagingService;

    @Mock
    private MockFirebaseMessagingService mockFirebaseMessagingService;

    private NotificationDomainService notificationDomainService;

    @BeforeEach
    void setUp() {
        // Mock 모드로 설정하여 MockFirebaseMessagingService 사용
        notificationDomainService = new NotificationDomainService(
            notificationLogRepository,
            mockFirebaseMessagingService
        );
    }

    @Nested
    @DisplayName("단일 사용자 알림 발송 테스트")
    class SingleUserNotificationTest {

        @Test
        @DisplayName("고객에게 주문 알림 발송 성공")
        void sendOrderNotificationToCustomerSuccess() {
            // Given
            Notification orderNotification = createOrderNotification();
            UserToken customerToken = createCustomerToken();

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenReturn(true);

            // When
            notificationDomainService.sendToSingleUser(orderNotification, customerToken);

            // Then
            verify(mockFirebaseMessagingService).sendMessage(
                eq(customerToken.getFcmToken()),
                eq(orderNotification)
            );
            verify(notificationLogRepository).save(any());
        }

        @Test
        @DisplayName("사장에게 리뷰 알림 발송 성공")
        void sendReviewNotificationToOwnerSuccess() {
            // Given
            Notification reviewNotification = createReviewNotification();
            UserToken ownerToken = createOwnerToken();

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenReturn(true);

            // When
            notificationDomainService.sendToSingleUser(reviewNotification, ownerToken);

            // Then
            verify(mockFirebaseMessagingService).sendMessage(
                eq(ownerToken.getFcmToken()),
                eq(reviewNotification)
            );
            verify(notificationLogRepository).save(any());
        }

        @Test
        @DisplayName("FCM 발송 실패 시 로그 저장")
        void sendNotificationFailureLogging() {
            // Given
            Notification notification = createSystemNotification();
            UserToken userToken = createCustomerToken();

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenReturn(false); // 발송 실패

            // When
            notificationDomainService.sendToSingleUser(notification, userToken);

            // Then
            verify(mockFirebaseMessagingService).sendMessage(anyString(), any(Notification.class));
            verify(notificationLogRepository).save(any()); // 실패해도 로그는 저장
        }

        @DisplayName("FCM 발송 중 예외 발생 시 예외 전파")
        void sendNotificationExceptionHandling() {
            // Given
            Notification notification = createSystemNotification();
            UserToken userToken = createCustomerToken();

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenThrow(new RuntimeException("FCM 서버 오류"));

            // When & Then
            assertThatThrownBy(() ->
                notificationDomainService.sendToSingleUser(notification, userToken)
            ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("FCM 서버 오류");

            verify(mockFirebaseMessagingService).sendMessage(anyString(), any(Notification.class));
            verify(notificationLogRepository).save(any()); // 예외 발생해도 에러 로그는 저장
        }
    }

    @Nested
    @DisplayName("다중 사용자 알림 발송 테스트")
    class MultipleUsersNotificationTest {

        @Test
        @DisplayName("고객 3명에게 이벤트 알림 발송 성공")
        void sendEventNotificationToMultipleCustomersSuccess() {
            // Given
            Notification eventNotification = createEventNotification();
            List<UserToken> customerTokens = List.of(
                createCustomerToken(1L, "customer1_token"),
                createCustomerToken(2L, "customer2_token"),
                createCustomerToken(3L, "customer3_token")
            );

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenReturn(true);

            // When
            notificationDomainService.sendToMultipleUsers(eventNotification, customerTokens);

            // Then
            verify(mockFirebaseMessagingService, times(3))
                .sendMessage(anyString(), eq(eventNotification));
            verify(notificationLogRepository, times(3)).save(any());
        }

        @Test
        @DisplayName("혼합 사용자 타입에게 시스템 알림 발송")
        void sendSystemNotificationToMixedUserTypes() {
            // Given
            Notification systemNotification = createSystemNotification();
            List<UserToken> mixedTokens = List.of(
                createCustomerToken(1L, "customer_token"),
                createOwnerToken(2L, "owner_token"),
                createCustomerToken(3L, "customer2_token")
            );

            when(mockFirebaseMessagingService.sendMessage(anyString(), any(Notification.class)))
                .thenReturn(true);

            // When
            notificationDomainService.sendToMultipleUsers(systemNotification, mixedTokens);

            // Then
            verify(mockFirebaseMessagingService, times(3))
                .sendMessage(anyString(), eq(systemNotification));
            verify(notificationLogRepository, times(3)).save(any());
        }

        @Test
        @DisplayName("일부 사용자 발송 실패해도 계속 진행")
        void sendToMultipleUsersWithPartialFailure() {
            // Given
            Notification notification = createOrderNotification();
            List<UserToken> userTokens = List.of(
                createCustomerToken(1L, "success_token1"),
                createCustomerToken(2L, "fail_token"),
                createCustomerToken(3L, "success_token2")
            );

            when(mockFirebaseMessagingService.sendMessage(eq("success_token1"), any(Notification.class)))
                .thenReturn(true);
            when(mockFirebaseMessagingService.sendMessage(eq("fail_token"), any(Notification.class)))
                .thenThrow(new RuntimeException("특정 토큰 오류"));
            when(mockFirebaseMessagingService.sendMessage(eq("success_token2"), any(Notification.class)))
                .thenReturn(true);

            // When
            notificationDomainService.sendToMultipleUsers(notification, userTokens);

            // Then
            verify(mockFirebaseMessagingService, times(3))
                .sendMessage(anyString(), eq(notification));
            verify(notificationLogRepository, times(3)).save(any()); // 모든 결과가 로그에 저장
        }

        @Test
        @DisplayName("빈 토큰 리스트로 발송 시도")
        void sendToEmptyTokenList() {
            // Given
            Notification notification = createSystemNotification();
            List<UserToken> emptyTokens = List.of();

            // When
            notificationDomainService.sendToMultipleUsers(notification, emptyTokens);

            // Then
            verify(mockFirebaseMessagingService, never())
                .sendMessage(anyString(), any(Notification.class));
            verify(notificationLogRepository, never()).save(any());
        }

        @Test
        @DisplayName("null 토큰 리스트로 발송 시도")
        void sendToNullTokenList() {
            // Given
            Notification notification = createEventNotification();

            // When
            notificationDomainService.sendToMultipleUsers(notification, null);

            // Then
            verify(mockFirebaseMessagingService, never())
                .sendMessage(anyString(), any(Notification.class));
            verify(notificationLogRepository, never()).save(any());
        }
    }

    // Helper methods
    private Notification createOrderNotification() {
        return Notification.builder()
            .notificationId(1L)
            .title("주문 접수 완료")
            .content("주문이 정상적으로 접수되었습니다. 곧 조리를 시작합니다!")
            .notificationType(NotificationType.ORDER)
            .subscriberType(SubscriberType.SPECIFIC)
            .subscriberId(123L)
            .actionUrl("/orders/123")
            .createdAt(LocalDateTime.now())
            .build();
    }

    private Notification createReviewNotification() {
        return Notification.builder()
            .notificationId(2L)
            .title("새로운 리뷰가 등록되었습니다")
            .content("고객님이 5점 리뷰를 남겨주셨습니다!")
            .notificationType(NotificationType.REVIEW)
            .subscriberType(SubscriberType.SPECIFIC)
            .subscriberId(456L)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private Notification createSystemNotification() {
        return Notification.builder()
            .notificationId(3L)
            .title("시스템 점검 안내")
            .content("오늘 밤 2시부터 1시간 동안 시스템 점검이 진행됩니다.")
            .notificationType(NotificationType.SYSTEM)
            .subscriberType(SubscriberType.ALL)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private Notification createEventNotification() {
        return Notification.builder()
            .notificationId(4L)
            .title("🎉 특별 할인 이벤트")
            .content("오늘 하루만! 모든 메뉴 20% 할인!")
            .notificationType(NotificationType.EVENT)
            .subscriberType(SubscriberType.CUSTOMER)
            .actionUrl("/events/discount")
            .createdAt(LocalDateTime.now())
            .build();
    }

    private UserToken createCustomerToken() {
        return createCustomerToken(123L, "customer_fcm_token_" + "a".repeat(100));
    }

    private UserToken createCustomerToken(Long userId, String fcmToken) {
        return UserToken.builder()
            .userTokenId(userId)
            .userId(userId)
            .userType(UserType.CUSTOMER)
            .fcmToken(fcmToken)
            .deviceType(DeviceType.WEB)
            .isActive(true)
            .lastLoginAt(LocalDateTime.now().minusMinutes(10))
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusMinutes(10))
            .build();
    }

    private UserToken createOwnerToken() {
        return createOwnerToken(456L, "owner_fcm_token_" + "b".repeat(100));
    }

    private UserToken createOwnerToken(Long userId, String fcmToken) {
        return UserToken.builder()
            .userTokenId(userId + 1000)
            .userId(userId)
            .userType(UserType.OWNER)
            .fcmToken(fcmToken)
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .lastLoginAt(LocalDateTime.now().minusHours(1))
            .createdAt(LocalDateTime.now().minusWeeks(1))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();
    }
}