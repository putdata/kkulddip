package com.kkulddip.notification.infrastructure.messaging.redis.processor.sender;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.NotificationType;
import com.kkulddip.notification.domain.model.enums.PublisherType;
import com.kkulddip.notification.domain.model.enums.RecipientType;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import com.kkulddip.notification.infrastructure.external.fcm.FcmService;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.favorite.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * NotificationSenderService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSenderService 테스트")
class NotificationSenderServiceTest {

    @Mock
    private FcmService fcmService;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private NotificationSenderService notificationSenderService;

    private NotificationResponse.NotificationResponseBuilder baseNotificationBuilder;

    @BeforeEach
    void setUp() {
        baseNotificationBuilder = NotificationResponse.builder()
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
    @DisplayName("브로드캐스트 패턴 감지 - 정상적인 브로드캐스트 알림")
    void isStoreFavoriteBroadcast_ValidBroadcast_ReturnsTrue() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - publisherType이 STORE가 아닌 경우")
    void isStoreFavoriteBroadcast_PublisherTypeNotStore_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.ADMIN)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - subscriberType이 CUSTOMER가 아닌 경우")
    void isStoreFavoriteBroadcast_SubscriberTypeNotCustomer_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.OWNER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - subscriberId가 null이 아닌 경우")
    void isStoreFavoriteBroadcast_SubscriberIdNotNull_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(123L)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - 모든 조건이 맞지 않는 경우")
    void isStoreFavoriteBroadcast_AllConditionsFalse_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.ADMIN)
                .subscriberType(SubscriberType.OWNER)
                .subscriberId(123L)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - null 알림")
    void isStoreFavoriteBroadcast_NullNotification_ReturnsFalse() {
        // given
        NotificationResponse notification = null;

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - publisherType이 null인 경우")
    void isStoreFavoriteBroadcast_PublisherTypeNull_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(null)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 패턴 감지 - subscriberType이 null인 경우")
    void isStoreFavoriteBroadcast_SubscriberTypeNull_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(null)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.isStoreFavoriteBroadcast(notification);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 정상적인 경우")
    void sendBroadcastToFavoriteCustomers_ValidStore_ReturnsTrue() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        NotificationSenderService spyService = spy(notificationSenderService);
        
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);
        doReturn(true).when(spyService).sendToSpecificUser(any(), anyLong(), any());

        // when
        boolean result = spyService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isTrue();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
        verify(spyService, times(3)).sendToSpecificUser(any(), anyLong(), any());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 존재하지 않는 가게")
    void sendBroadcastToFavoriteCustomers_NonExistentStore_ReturnsFalse() {
        // given
        Long storeId = 999L;
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        when(storeRepository.existsById(storeId)).thenReturn(false);

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository, never()).findCustomerIdsByStoreId(anyLong());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 즐겨찾기한 고객이 없는 경우")
    void sendBroadcastToFavoriteCustomers_NoFavoriteCustomers_ReturnsTrue() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Collections.emptyList();
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isTrue();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 일부 고객 발송 실패")
    void sendBroadcastToFavoriteCustomers_PartialFailure_ReturnsTrue() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        NotificationSenderService spyService = spy(notificationSenderService);
        
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);
        doReturn(true).when(spyService).sendToSpecificUser(eq(notification), eq(1L), any());
        doReturn(false).when(spyService).sendToSpecificUser(eq(notification), eq(2L), any());
        doReturn(true).when(spyService).sendToSpecificUser(eq(notification), eq(3L), any());

        // when
        boolean result = spyService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isTrue(); // 1개 이상 성공하면 true
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
        verify(spyService, times(3)).sendToSpecificUser(any(), anyLong(), any());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 모든 고객 발송 실패")
    void sendBroadcastToFavoriteCustomers_AllFailures_ReturnsFalse() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        NotificationSenderService spyService = spy(notificationSenderService);
        
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);
        doReturn(false).when(spyService).sendToSpecificUser(any(), anyLong(), any());

        // when
        boolean result = spyService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
        verify(spyService, times(3)).sendToSpecificUser(any(), anyLong(), any());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 단일 고객")
    void sendBroadcastToFavoriteCustomers_SingleCustomer_ReturnsTrue() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Arrays.asList(1L);
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        NotificationSenderService spyService = spy(notificationSenderService);
        
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);
        doReturn(true).when(spyService).sendToSpecificUser(any(), anyLong(), any());

        // when
        boolean result = spyService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isTrue();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
        verify(spyService, times(1)).sendToSpecificUser(any(), anyLong(), any());
    }

    // === 에러 처리 테스트 ===

    @Test
    @DisplayName("브로드캐스트 알림 발송 - null 알림 객체")
    void sendBroadcastToFavoriteCustomers_NullNotification_ReturnsFalse() {
        // given
        Long storeId = 100L;
        NotificationResponse notification = null;

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository, never()).existsById(anyLong());
        verify(favoriteRepository, never()).findCustomerIdsByStoreId(anyLong());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 유효하지 않은 storeId (null)")
    void sendBroadcastToFavoriteCustomers_NullStoreId_ReturnsFalse() {
        // given
        Long storeId = null;
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository, never()).existsById(anyLong());
        verify(favoriteRepository, never()).findCustomerIdsByStoreId(anyLong());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 유효하지 않은 storeId (0 이하)")
    void sendBroadcastToFavoriteCustomers_InvalidStoreId_ReturnsFalse() {
        // given
        Long storeId = -1L;
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository, never()).existsById(anyLong());
        verify(favoriteRepository, never()).findCustomerIdsByStoreId(anyLong());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 가게 존재 확인 중 예외 발생")
    void sendBroadcastToFavoriteCustomers_StoreExistsException_ReturnsFalse() {
        // given
        Long storeId = 100L;
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        when(storeRepository.existsById(storeId)).thenThrow(new RuntimeException("Database connection error"));

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository, never()).findCustomerIdsByStoreId(anyLong());
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 즐겨찾기 고객 조회 중 예외 발생")
    void sendBroadcastToFavoriteCustomers_FavoriteRepositoryException_ReturnsFalse() {
        // given
        Long storeId = 100L;
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId))
                .thenThrow(new RuntimeException("Database query error"));

        // when
        boolean result = notificationSenderService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
    }

    @Test
    @DisplayName("브로드캐스트 알림 발송 - 개별 고객 발송 중 예외 발생")
    void sendBroadcastToFavoriteCustomers_IndividualCustomerException_ContinuesProcessing() {
        // given
        Long storeId = 100L;
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
        NotificationResponse notification = baseNotificationBuilder
                .publisherType(PublisherType.STORE)
                .subscriberType(SubscriberType.CUSTOMER)
                .subscriberId(null)
                .build();

        NotificationSenderService spyService = spy(notificationSenderService);
        
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(favoriteRepository.findCustomerIdsByStoreId(storeId)).thenReturn(customerIds);
        doReturn(true).when(spyService).sendToSpecificUser(eq(notification), eq(1L), any());
        doThrow(new RuntimeException("FCM error")).when(spyService).sendToSpecificUser(eq(notification), eq(2L), any());
        doReturn(true).when(spyService).sendToSpecificUser(eq(notification), eq(3L), any());

        // when
        boolean result = spyService.sendBroadcastToFavoriteCustomers(notification, storeId);

        // then
        assertThat(result).isTrue(); // 2개 성공했으므로 true
        verify(storeRepository).existsById(storeId);
        verify(favoriteRepository).findCustomerIdsByStoreId(storeId);
        verify(spyService, times(3)).sendToSpecificUser(any(), anyLong(), any());
    }

    @Test
    @DisplayName("특정 사용자 알림 발송 - null 알림 객체")
    void sendToSpecificUser_NullNotification_ReturnsFalse() {
        // given
        NotificationResponse notification = null;
        Long userId = 1L;
        RecipientType recipientType = RecipientType.CUSTOMER;

        // when
        boolean result = notificationSenderService.sendToSpecificUser(notification, userId, recipientType);

        // then
        assertThat(result).isFalse();
        verify(userTokenRepository, never()).findByUserIdAndUserTypeAndIsActiveTrue(anyLong(), any());
    }

    @Test
    @DisplayName("특정 사용자 알림 발송 - 유효하지 않은 userId")
    void sendToSpecificUser_InvalidUserId_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long userId = -1L;
        RecipientType recipientType = RecipientType.CUSTOMER;

        // when
        boolean result = notificationSenderService.sendToSpecificUser(notification, userId, recipientType);

        // then
        assertThat(result).isFalse();
        verify(userTokenRepository, never()).findByUserIdAndUserTypeAndIsActiveTrue(anyLong(), any());
    }

    @Test
    @DisplayName("특정 사용자 알림 발송 - null recipientType")
    void sendToSpecificUser_NullRecipientType_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long userId = 1L;
        RecipientType recipientType = null;

        // when
        boolean result = notificationSenderService.sendToSpecificUser(notification, userId, recipientType);

        // then
        assertThat(result).isFalse();
        verify(userTokenRepository, never()).findByUserIdAndUserTypeAndIsActiveTrue(anyLong(), any());
    }

    @Test
    @DisplayName("특정 사용자 알림 발송 - 사용자 토큰 조회 중 예외 발생")
    void sendToSpecificUser_UserTokenRepositoryException_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long userId = 1L;
        RecipientType recipientType = RecipientType.CUSTOMER;

        when(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, UserRole.CUSTOMER))
                .thenThrow(new RuntimeException("Database error"));

        // when
        boolean result = notificationSenderService.sendToSpecificUser(notification, userId, recipientType);

        // then
        assertThat(result).isFalse();
        verify(userTokenRepository).findByUserIdAndUserTypeAndIsActiveTrue(userId, UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("가게 사장 알림 발송 - null 알림 객체")
    void sendToStoreOwner_NullNotification_ReturnsFalse() {
        // given
        NotificationResponse notification = null;
        Long storeId = 100L;

        // when
        boolean result = notificationSenderService.sendToStoreOwner(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository, never()).findOwnerIdByStoreId(anyLong());
    }

    @Test
    @DisplayName("가게 사장 알림 발송 - 유효하지 않은 storeId")
    void sendToStoreOwner_InvalidStoreId_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long storeId = 0L;

        // when
        boolean result = notificationSenderService.sendToStoreOwner(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository, never()).findOwnerIdByStoreId(anyLong());
    }

    @Test
    @DisplayName("가게 사장 알림 발송 - 가게 소유자 조회 중 예외 발생")
    void sendToStoreOwner_StoreRepositoryException_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long storeId = 100L;

        when(storeRepository.findOwnerIdByStoreId(storeId))
                .thenThrow(new RuntimeException("Database connection error"));

        // when
        boolean result = notificationSenderService.sendToStoreOwner(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).findOwnerIdByStoreId(storeId);
        verify(userTokenRepository, never()).findByUserIdAndUserTypeAndIsActiveTrue(anyLong(), any());
    }

    @Test
    @DisplayName("가게 사장 알림 발송 - 사장 토큰 조회 중 예외 발생")
    void sendToStoreOwner_UserTokenRepositoryException_ReturnsFalse() {
        // given
        NotificationResponse notification = baseNotificationBuilder.build();
        Long storeId = 100L;
        Long ownerId = 200L;

        when(storeRepository.findOwnerIdByStoreId(storeId)).thenReturn(Optional.of(ownerId));
        when(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(ownerId, UserRole.OWNER))
                .thenThrow(new RuntimeException("Token query error"));

        // when
        boolean result = notificationSenderService.sendToStoreOwner(notification, storeId);

        // then
        assertThat(result).isFalse();
        verify(storeRepository).findOwnerIdByStoreId(storeId);
        verify(userTokenRepository).findByUserIdAndUserTypeAndIsActiveTrue(ownerId, UserRole.OWNER);
    }
}