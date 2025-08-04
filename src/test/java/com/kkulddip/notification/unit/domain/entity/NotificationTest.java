package com.kkulddip.notification.unit.domain.entity;

import com.kkulddip.notification.domain.model.entity.Notification;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Notification 도메인 엔티티 테스트")
class NotificationTest {

    @Nested
    @DisplayName("알림 생성 테스트")
    class CreateNotificationTest {

        @Test
        @DisplayName("정상적인 알림 생성")
        void createValidNotification() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            Notification notification = Notification.builder()
                .notificationId(1L)
                .title("주문 배송 완료")
                .content("주문하신 상품이 배송 완료되었습니다.")
                .notificationType(NotificationType.ORDER)
                .subscriberType(SubscriberType.SPECIFIC)
                .subscriberId(123L)
                .actionUrl("/orders/123")
                .createdAt(now)
                .build();

            // Then
            assertThat(notification.getNotificationId()).isEqualTo(1L);
            assertThat(notification.getTitle()).isEqualTo("주문 배송 완료");
            assertThat(notification.getContent()).isEqualTo("주문하신 상품이 배송 완료되었습니다.");
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.ORDER);
            assertThat(notification.getSubscriberType()).isEqualTo(SubscriberType.SPECIFIC);
            assertThat(notification.getSubscriberId()).isEqualTo(123L);
            assertThat(notification.getActionUrl()).isEqualTo("/orders/123");
            assertThat(notification.getCreatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("알림 유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("유효한 전체 발송 알림")
        void validBroadcastNotification() {
            // Given
            Notification notification = Notification.builder()
                .title("시스템 점검 안내")
                .content("2024년 1월 1일 새벽 2시부터 시스템 점검을 진행합니다.")
                .notificationType(NotificationType.SYSTEM)
                .subscriberType(SubscriberType.ALL)
                .build();

            // When & Then
            assertThat(notification.isValid()).isTrue();
            assertThat(notification.isBroadcast()).isTrue();
            assertThat(notification.isTargeted()).isFalse();
        }

        @Test
        @DisplayName("유효한 특정 사용자 대상 알림")
        void validTargetedNotification() {
            // Given
            Notification notification = Notification.builder()
                .title("개인 맞춤 할인")
                .content("회원님만을 위한 특별 할인 혜택이 도착했습니다!")
                .notificationType(NotificationType.MARKETING)
                .subscriberType(SubscriberType.SPECIFIC)
                .subscriberId(456L)
                .build();

            // When & Then
            assertThat(notification.isValid()).isTrue();
            assertThat(notification.isBroadcast()).isFalse();
            assertThat(notification.isTargeted()).isTrue();
        }

        @Test
        @DisplayName("제목이 없는 알림은 유효하지 않음")
        void invalidNotificationWithoutTitle() {
            // Given
            Notification notification = Notification.builder()
                .content("내용만 있는 알림")
                .notificationType(NotificationType.ORDER)
                .subscriberType(SubscriberType.CUSTOMER)
                .build();

            // When & Then
            assertThat(notification.isValid()).isFalse();
        }

        @Test
        @DisplayName("내용이 빈 문자열인 알림은 유효하지 않음")
        void invalidNotificationWithEmptyContent() {
            // Given
            Notification notification = Notification.builder()
                .title("제목은 있음")
                .content("   ") // 공백만 있는 내용
                .notificationType(NotificationType.EVENT)
                .subscriberType(SubscriberType.ALL)
                .build();

            // When & Then
            assertThat(notification.isValid()).isFalse();
        }

        @Test
        @DisplayName("SPECIFIC 타입인데 subscriberId가 없으면 유효하지 않음")
        void invalidSpecificNotificationWithoutSubscriberId() {
            // Given
            Notification notification = Notification.builder()
                .title("개인 알림")
                .content("특정 사용자를 위한 알림인데 ID가 없음")
                .notificationType(NotificationType.ORDER)
                .subscriberType(SubscriberType.SPECIFIC)
                // subscriberId 없음
                .build();

            // When & Then
            assertThat(notification.isValid()).isFalse();
            assertThat(notification.isTargeted()).isFalse();
        }
    }

    @Nested
    @DisplayName("알림 타입 및 상태 확인 테스트")
    class NotificationTypeAndStateTest {

        @Test
        @DisplayName("즉시 발송 여부 확인")
        void immediateNotificationTest() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusMinutes(10);
            LocalDateTime future = now.plusMinutes(10);

            // When & Then
            assertThat(createNotificationWithSchedule(null).isImmediate()).isTrue();
            assertThat(createNotificationWithSchedule(now).isImmediate()).isTrue();
            assertThat(createNotificationWithSchedule(past).isImmediate()).isTrue();
            assertThat(createNotificationWithSchedule(future).isImmediate()).isFalse();
        }

        @Test
        @DisplayName("예약 발송 여부 확인")
        void scheduledNotificationTest() {
            // Given
            LocalDateTime future = LocalDateTime.now().plusHours(2);
            LocalDateTime past = LocalDateTime.now().minusHours(1);

            // When & Then
            assertThat(createNotificationWithSchedule(future).getScheduledAt() != null).isTrue();
            assertThat(createNotificationWithSchedule(past).getScheduledAt() == null).isFalse();
            assertThat(createNotificationWithSchedule(null).getScheduledAt() != null).isFalse();
        }

        @Test
        @DisplayName("발송 완료 상태 확인")
        void sentNotificationTest() {
            // Given
            LocalDateTime sentTime = LocalDateTime.now().minusMinutes(5);

            // When & Then
            assertThat(createNotificationWithSentAt(null).isSent()).isFalse();
            assertThat(createNotificationWithSentAt(sentTime).isSent()).isTrue();
        }

        @Test
        @DisplayName("발송 완료 처리")
        void markAsSentTest() {
            // Given
            Notification notification = Notification.builder()
                .title("테스트 알림")
                .content("발송 완료 테스트")
                .notificationType(NotificationType.SYSTEM)
                .subscriberType(SubscriberType.ALL)
                .build();

            assertThat(notification.isSent()).isFalse();

            // When
            Notification sentNotification = notification.markAsSent();

            // Then
            assertThat(sentNotification.isSent()).isTrue();
            assertThat(sentNotification.getSentAt()).isNotNull();
            assertThat(sentNotification.getSentAt()).isBeforeOrEqualTo(LocalDateTime.now());

            // 원본은 변경되지 않음 (불변성 확인)
            assertThat(notification.isSent()).isFalse();
        }
    }

    // Helper methods
    private Notification createNotificationWithSchedule(LocalDateTime scheduledAt) {
        return Notification.builder()
            .title("예약 알림 테스트")
            .content("예약 발송 테스트용 알림")
            .notificationType(NotificationType.SYSTEM)
            .subscriberType(SubscriberType.ALL)
            .scheduledAt(scheduledAt)
            .build();
    }

    private Notification createNotificationWithSentAt(LocalDateTime sentAt) {
        return Notification.builder()
            .title("발송 완료 테스트")
            .content("발송 상태 테스트용 알림")
            .notificationType(NotificationType.ORDER)
            .subscriberType(SubscriberType.CUSTOMER)
            .sentAt(sentAt)
            .build();
    }
}