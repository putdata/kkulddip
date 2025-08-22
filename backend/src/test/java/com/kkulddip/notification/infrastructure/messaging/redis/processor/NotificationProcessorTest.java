package com.kkulddip.notification.infrastructure.messaging.redis.processor;

import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.domain.service.NotificationDomainService;
import com.kkulddip.notification.infrastructure.messaging.redis.processor.sender.NotificationSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * NotificationProcessor 통합 테스트
 * 브로드캐스트 로직 통합을 포함한 전체 알림 처리 플로우를 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationProcessor 통합 테스트")
class NotificationProcessorTest {

        @Mock
        private NotificationService notificationService;

        @Mock
        private NotificationDomainService notificationDomainService;

        @Mock
        private NotificationSenderService notificationSenderService;

        @InjectMocks
        private NotificationProcessor notificationProcessor;

        private NotificationRequest.NotificationRequestBuilder baseRequestBuilder;
        private NotificationResponse.NotificationResponseBuilder baseResponseBuilder;

        @BeforeEach
        void setUp() {
                baseRequestBuilder = NotificationRequest.builder()
                                .id("test-notification-1")
                                .title("테스트 알림")
                                .content("테스트 내용")
                                .publisherId(100L)
                                .actionUrl("https://example.com")
                                .notificationType(NotificationType.EVENT)
                                .scheduledAt(LocalDateTime.now());

                baseResponseBuilder = NotificationResponse.builder()
                                .notificationId(1L)
                                .title("테스트 알림")
                                .content("테스트 내용")
                                .publisherId(100L)
                                .actionUrl("https://example.com")
                                .notificationType(NotificationType.EVENT)
                                .scheduledAt(LocalDateTime.now())
                                .createdAt(LocalDateTime.now())
                                .isSent(false);
        }

        @Test
        @DisplayName("브로드캐스트 알림 처리 - 가게에서 즐겨찾기 고객들에게 브로드캐스트")
        void processNotification_StoreBroadcastToFavoriteCustomers_Success() {
                // given
                Long storeId = 100L;
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null) // 브로드캐스트 패턴
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.isStoreFavoriteBroadcast(notification)).thenReturn(true);
                when(notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId))
                                .thenReturn(true);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isTrue();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).isStoreFavoriteBroadcast(notification);
                verify(notificationSenderService).sendBroadcastToFavoriteCustomers(notification, storeId);
                verify(notificationService).markNotificationAsSent(notification.getNotificationId());

                // 다른 발송 메서드들은 호출되지 않아야 함
                verify(notificationSenderService, never()).sendToAllCustomers(any());
                verify(notificationSenderService, never()).sendToAll(any());
        }

        @Test
        @DisplayName("브로드캐스트 알림 처리 실패 - publisherId가 null인 경우")
        void processNotification_StoreBroadcastWithNullPublisherId_Failure() {
                // given
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .publisherId(null) // publisherId가 null
                                .build();

                // Mock 설정
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.isStoreFavoriteBroadcast(notification)).thenReturn(true);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isFalse();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).isStoreFavoriteBroadcast(notification);
                verify(notificationSenderService, never()).sendBroadcastToFavoriteCustomers(any(), any());
                verify(notificationService, never()).markNotificationAsSent(any());
        }

        @Test
        @DisplayName("브로드캐스트 알림 처리 실패 - 브로드캐스트 발송 실패")
        void processNotification_StoreBroadcastSendFailure_Failure() {
                // given
                Long storeId = 100L;
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.isStoreFavoriteBroadcast(notification)).thenReturn(true);
                when(notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId))
                                .thenReturn(false);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isFalse();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).isStoreFavoriteBroadcast(notification);
                verify(notificationSenderService).sendBroadcastToFavoriteCustomers(notification, storeId);
                verify(notificationService, never()).markNotificationAsSent(any());
        }

        @Test
        @DisplayName("일반 고객 알림 처리 - 브로드캐스트 패턴이 아닌 경우")
        void processNotification_RegularCustomerNotification_Success() {
                // given
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.ADMIN)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.ADMIN)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.isStoreFavoriteBroadcast(notification)).thenReturn(false);
                when(notificationSenderService.sendToAllCustomers(notification)).thenReturn(true);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isTrue();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).isStoreFavoriteBroadcast(notification);
                verify(notificationSenderService).sendToAllCustomers(notification);
                verify(notificationService).markNotificationAsSent(notification.getNotificationId());

                // 브로드캐스트 메서드는 호출되지 않아야 함
                verify(notificationSenderService, never()).sendBroadcastToFavoriteCustomers(any(), any());
        }

        @Test
        @DisplayName("특정 고객 알림 처리 - subscriberId가 있는 경우")
        void processNotification_SpecificCustomerNotification_Success() {
                // given
                Long customerId = 200L;
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.ADMIN)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(customerId)
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.ADMIN)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(customerId)
                                .build();

                // Mock 설정
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.sendToSpecificUser(eq(notification), eq(customerId), any()))
                                .thenReturn(true);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isTrue();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).sendToSpecificUser(eq(notification), eq(customerId), any());
                verify(notificationService).markNotificationAsSent(notification.getNotificationId());

                // 브로드캐스트나 전체 발송 메서드는 호출되지 않아야 함
                verify(notificationSenderService, never()).sendBroadcastToFavoriteCustomers(any(), any());
                verify(notificationSenderService, never()).sendToAllCustomers(any());
                verify(notificationSenderService, never()).isStoreFavoriteBroadcast(any());
        }

        @Test
        @DisplayName("알림 저장 실패 - createNotificationFromRedis 실패")
        void processNotification_NotificationCreationFailure_Failure() {
                // given
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정 - 알림 저장 실패
                when(notificationService.createNotificationFromRedis(request)).thenReturn(null);

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isFalse();
                verify(notificationService).createNotificationFromRedis(request);

                // 발송 관련 메서드들은 호출되지 않아야 함
                verify(notificationSenderService, never()).isStoreFavoriteBroadcast(any());
                verify(notificationSenderService, never()).sendBroadcastToFavoriteCustomers(any(), any());
                verify(notificationSenderService, never()).sendToAllCustomers(any());
                verify(notificationService, never()).markNotificationAsSent(any());
        }

        @Test
        @DisplayName("예외 처리 - 브로드캐스트 발송 중 예외 발생")
        void processNotification_BroadcastExceptionHandling_Failure() {
                // given
                Long storeId = 100L;
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                NotificationResponse notification = baseResponseBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정 - 브로드캐스트 발송 중 예외 발생
                when(notificationService.createNotificationFromRedis(request)).thenReturn(notification);
                when(notificationSenderService.isStoreFavoriteBroadcast(notification)).thenReturn(true);
                when(notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId))
                                .thenThrow(new RuntimeException("브로드캐스트 발송 실패"));

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isFalse();
                verify(notificationService).createNotificationFromRedis(request);
                verify(notificationSenderService).isStoreFavoriteBroadcast(notification);
                verify(notificationSenderService).sendBroadcastToFavoriteCustomers(notification, storeId);
                verify(notificationService, never()).markNotificationAsSent(any());
        }

        @Test
        @DisplayName("전체 처리 플로우 예외 처리 - 최상위 예외 발생")
        void processNotification_TopLevelExceptionHandling_Failure() {
                // given
                NotificationRequest request = baseRequestBuilder
                                .publisherType(PublisherType.STORE)
                                .subscriberType(SubscriberType.CUSTOMER)
                                .subscriberId(null)
                                .build();

                // Mock 설정 - 알림 저장 중 예외 발생
                when(notificationService.createNotificationFromRedis(request))
                                .thenThrow(new RuntimeException("데이터베이스 연결 실패"));

                // when
                boolean result = notificationProcessor.processNotification(request);

                // then
                assertThat(result).isFalse();
                verify(notificationService).createNotificationFromRedis(request);

                // 발송 관련 메서드들은 호출되지 않아야 함
                verify(notificationSenderService, never()).isStoreFavoriteBroadcast(any());
                verify(notificationSenderService, never()).sendBroadcastToFavoriteCustomers(any(), any());
                verify(notificationService, never()).markNotificationAsSent(any());
        }
}