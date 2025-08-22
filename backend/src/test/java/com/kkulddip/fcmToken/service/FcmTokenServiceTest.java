package com.kkulddip.fcmToken.service;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.enums.DeviceType;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import com.kkulddip.domain.userToken.service.FcmTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("FcmTokenService 테스트")
class FcmTokenServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    private Long userId;
    private UserRole userRole;
    private FcmTokenRequest fcmTokenRequest;
    private UserToken userToken;

    @BeforeEach
    void setUp() {
        userId = 1L;
        userRole = UserRole.CUSTOMER;
        fcmTokenRequest = new FcmTokenRequest("test-fcm-token", DeviceType.ANDROID);
        userToken = createTestUserToken();
    }

    @Nested
    @DisplayName("토큰 등록/업데이트 테스트")
    class RegisterOrUpdateTokenTest {

        @Test
        @DisplayName("새 토큰 등록 - 성공")
        void registerNewToken_Success() {
            // given
            given(customerRepository.existsById(userId)).willReturn(true);
            given(userTokenRepository.findByUserIdAndUserTypeAndDeviceType(userId, userRole, DeviceType.ANDROID))
                .willReturn(Optional.empty());
            given(userTokenRepository.save(any(UserToken.class))).willReturn(userToken);

            // when
            FcmTokenResponse response = fcmTokenService.registerOrUpdateToken(userId, userRole, fcmTokenRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.fcmToken()).isEqualTo(fcmTokenRequest.fcmToken());
            assertThat(response.deviceType()).isEqualTo(fcmTokenRequest.deviceType());
            then(userTokenRepository).should().save(any(UserToken.class));
        }

        @Test
        @DisplayName("기존 토큰 업데이트 - 성공")
        void updateExistingToken_Success() {
            // given
            String newFcmToken = "new-fcm-token";
            FcmTokenRequest updateRequest = new FcmTokenRequest(newFcmToken, DeviceType.ANDROID);
            
            given(customerRepository.existsById(userId)).willReturn(true);
            given(userTokenRepository.findByUserIdAndUserTypeAndDeviceType(userId, userRole, DeviceType.ANDROID))
                .willReturn(Optional.of(userToken));

            // when
            FcmTokenResponse response = fcmTokenService.registerOrUpdateToken(userId, userRole, updateRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.fcmToken()).isEqualTo(newFcmToken);
            then(userTokenRepository).should(never()).save(any(UserToken.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 - 예외 발생")
        void registerToken_UserNotFound_ThrowsException() {
            // given
            given(customerRepository.existsById(userId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> fcmTokenService.registerOrUpdateToken(userId, userRole, fcmTokenRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("OWNER 사용자 토큰 등록 - 성공")
        void registerOwnerToken_Success() {
            // given
            UserRole ownerRole = UserRole.OWNER;
            given(ownerRepository.existsById(userId)).willReturn(true);
            given(userTokenRepository.findByUserIdAndUserTypeAndDeviceType(userId, ownerRole, DeviceType.ANDROID))
                .willReturn(Optional.empty());
            given(userTokenRepository.save(any(UserToken.class))).willReturn(userToken);

            // when
            FcmTokenResponse response = fcmTokenService.registerOrUpdateToken(userId, ownerRole, fcmTokenRequest);

            // then
            assertThat(response).isNotNull();
            then(ownerRepository).should().existsById(userId);
        }

        @Test
        @DisplayName("ADMIN 사용자 토큰 등록 - 성공")
        void registerAdminToken_Success() {
            // given
            UserRole adminRole = UserRole.ADMIN;
            given(userTokenRepository.findByUserIdAndUserTypeAndDeviceType(userId, adminRole, DeviceType.ANDROID))
                .willReturn(Optional.empty());
            given(userTokenRepository.save(any(UserToken.class))).willReturn(userToken);

            // when
            FcmTokenResponse response = fcmTokenService.registerOrUpdateToken(userId, adminRole, fcmTokenRequest);

            // then
            assertThat(response).isNotNull();
            then(customerRepository).should(never()).existsById(anyLong());
            then(ownerRepository).should(never()).existsById(anyLong());
        }
    }

    @Nested
    @DisplayName("토큰 비활성화 테스트")
    class DeactivateTokensTest {

        @Test
        @DisplayName("사용자 토큰 비활성화 - 성공")
        void deactivateUserTokens_Success() {
            // given
            List<UserToken> activeTokens = List.of(userToken, createTestUserToken());
            given(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole))
                .willReturn(activeTokens);

            // when
            fcmTokenService.deactivateUserTokens(userId, userRole);

            // then
            then(userTokenRepository).should().findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);
        }

        @Test
        @DisplayName("활성 토큰이 없는 경우 - 정상 처리")
        void deactivateUserTokens_NoActiveTokens_Success() {
            // given
            given(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole))
                .willReturn(List.of());

            // when
            fcmTokenService.deactivateUserTokens(userId, userRole);

            // then
            then(userTokenRepository).should().findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole);
        }
    }

    @Nested
    @DisplayName("토큰 삭제 테스트")
    class DeleteTokenTest {

        @Test
        @DisplayName("토큰 삭제 - 성공")
        void deleteToken_Success() {
            // given
            String fcmToken = "test-fcm-token";
            given(userTokenRepository.findByFcmToken(fcmToken)).willReturn(Optional.of(userToken));

            // when
            boolean result = fcmTokenService.deleteToken(fcmToken);

            // then
            assertThat(result).isTrue();
            then(userTokenRepository).should().delete(userToken);
        }

        @Test
        @DisplayName("존재하지 않는 토큰 삭제 - false 반환")
        void deleteToken_NotFound_ReturnsFalse() {
            // given
            String fcmToken = "non-existent-token";
            given(userTokenRepository.findByFcmToken(fcmToken)).willReturn(Optional.empty());

            // when
            boolean result = fcmTokenService.deleteToken(fcmToken);

            // then
            assertThat(result).isFalse();
            then(userTokenRepository).should(never()).delete(any(UserToken.class));
        }
    }

    @Nested
    @DisplayName("비활성 토큰 정리 테스트")
    class CleanupInactiveTokensTest {

        @Test
        @DisplayName("비활성 토큰 정리 - 성공")
        void cleanupInactiveTokens_Success() {
            // given
            int daysOld = 7;
            List<UserToken> inactiveTokens = List.of(userToken, createTestUserToken());
            given(userTokenRepository.findInactiveTokens(any(LocalDateTime.class)))
                .willReturn(inactiveTokens);

            // when
            int deletedCount = fcmTokenService.cleanupInactiveTokens(daysOld);

            // then
            assertThat(deletedCount).isEqualTo(2);
            then(userTokenRepository).should().deleteAll(inactiveTokens);
        }

        @Test
        @DisplayName("정리할 비활성 토큰이 없는 경우 - 0 반환")
        void cleanupInactiveTokens_NoInactiveTokens_ReturnsZero() {
            // given
            int daysOld = 7;
            given(userTokenRepository.findInactiveTokens(any(LocalDateTime.class)))
                .willReturn(List.of());

            // when
            int deletedCount = fcmTokenService.cleanupInactiveTokens(daysOld);

            // then
            assertThat(deletedCount).isEqualTo(0);
            then(userTokenRepository).should(never()).deleteAll(any());
        }
    }

    @Nested
    @DisplayName("사용자 활성 토큰 조회 테스트")
    class GetUserActiveTokensTest {

        @Test
        @DisplayName("사용자 활성 토큰 조회 - 성공")
        void getUserActiveTokens_Success() {
            // given
            List<UserToken> activeTokens = List.of(userToken, createTestUserToken());
            given(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole))
                .willReturn(activeTokens);

            // when
            List<FcmTokenResponse> responses = fcmTokenService.getUserActiveTokens(userId, userRole);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).fcmToken()).isEqualTo(userToken.getFcmToken());
        }

        @Test
        @DisplayName("활성 토큰이 없는 경우 - 빈 리스트 반환")
        void getUserActiveTokens_NoActiveTokens_ReturnsEmptyList() {
            // given
            given(userTokenRepository.findByUserIdAndUserTypeAndIsActiveTrue(userId, userRole))
                .willReturn(List.of());

            // when
            List<FcmTokenResponse> responses = fcmTokenService.getUserActiveTokens(userId, userRole);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("사용자 타입별 활성 토큰 조회 테스트")
    class GetActiveTokensByUserTypeTest {

        @Test
        @DisplayName("사용자 타입별 활성 토큰 조회 - 성공")
        void getActiveTokensByUserType_Success() {
            // given
            List<UserToken> activeTokens = List.of(userToken, createTestUserToken());
            given(userTokenRepository.findAllActiveTokensByUserType(userRole))
                .willReturn(activeTokens);

            // when
            List<UserToken> tokens = fcmTokenService.getActiveTokensByUserType(userRole);

            // then
            assertThat(tokens).hasSize(2);
            then(userTokenRepository).should().findAllActiveTokensByUserType(userRole);
        }
    }

    private UserToken createTestUserToken() {
        return UserToken.builder()
            .userId(userId)
            .userType(userRole)
            .fcmToken("test-fcm-token")
            .deviceType(DeviceType.ANDROID)
            .build();
    }
}