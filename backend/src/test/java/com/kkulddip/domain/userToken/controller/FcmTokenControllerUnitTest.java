package com.kkulddip.domain.userToken.controller;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
import com.kkulddip.domain.userToken.enums.DeviceType;
import com.kkulddip.domain.userToken.service.FcmTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)  // Unnecessary stubbings 경고 해결
@DisplayName("FcmTokenController 단위 테스트")
class FcmTokenControllerUnitTest {

    @Mock
    private FcmTokenService fcmTokenService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private FcmTokenController fcmTokenController;

    private Customer customer;
    private Owner owner;
    private FcmTokenRequest fcmTokenRequest;
    private FcmTokenResponse fcmTokenResponse;
    private JwtUserInfo customerUserInfo;
    private JwtUserInfo ownerUserInfo;

    @BeforeEach
    void setUp() {
        Customer tempCustomer = Customer.builder()
            .email("customer@test.com")
            .name("Customer User")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google-id-123")
            .build();
        customer = spy(tempCustomer);
        given(customer.getId()).willReturn(1L);

        Owner tempOwner = Owner.builder()
            .email("owner@test.com")
            .name("Owner User")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google-id-456")
            .build();
        owner = spy(tempOwner);
        given(owner.getId()).willReturn(2L);

        fcmTokenRequest = new FcmTokenRequest("test-fcm-token", DeviceType.ANDROID);
        fcmTokenResponse = FcmTokenResponse.builder()
            .tokenId(1L)
            .fcmToken("test-fcm-token")
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .registeredAt(LocalDateTime.now())
            .build();

        customerUserInfo = new JwtUserInfo(
            "1",
            "customer@test.com",
            "CUSTOMER",
            "GOOGLE",
            "google-id-123"
        );

        ownerUserInfo = new JwtUserInfo(
            "2",
            "owner@test.com",
            "OWNER",
            "GOOGLE",
            "google-id-456"
        );
    }

    @Nested
    @DisplayName("토큰 등록 API 테스트")
    class RegisterTokenTest {

        @Test
        @DisplayName("고객 토큰 등록 - 성공")
        void registerCustomerToken_Success() {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));
            given(fcmTokenService.registerOrUpdateToken(eq(1L), eq(UserRole.CUSTOMER), eq(fcmTokenRequest)))
                .willReturn(fcmTokenResponse);

            // when
            var result = fcmTokenController.registerToken(customerUserInfo, fcmTokenRequest);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            then(fcmTokenService).should().registerOrUpdateToken(eq(1L), eq(UserRole.CUSTOMER), eq(fcmTokenRequest));
        }

        @Test
        @DisplayName("사업자 토큰 등록 - 성공")
        void registerOwnerToken_Success() {
            // given
            given(ownerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-456")).willReturn(Optional.of(owner));
            given(fcmTokenService.registerOrUpdateToken(eq(2L), eq(UserRole.OWNER), eq(fcmTokenRequest)))
                .willReturn(fcmTokenResponse);

            // when
            var result = fcmTokenController.registerToken(ownerUserInfo, fcmTokenRequest);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            then(fcmTokenService).should().registerOrUpdateToken(eq(2L), eq(UserRole.OWNER), eq(fcmTokenRequest));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 토큰 등록 - 실패")
        void registerToken_UserNotFound_Failure() {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.empty());

            // when & then
            assertThrows(BusinessException.class, () ->
                fcmTokenController.registerToken(customerUserInfo, fcmTokenRequest));
        }
    }

    @Nested
    @DisplayName("토큰 비활성화 API 테스트")
    class DeactivateTokenTest {

        @Test
        @DisplayName("현재 사용자 토큰 비활성화 - 성공")
        void deactivateCurrentUserTokens_Success() {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));

            // when
            var result = fcmTokenController.deactivateCurrentUserTokens(customerUserInfo);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            then(fcmTokenService).should().deactivateUserTokens(eq(1L), eq(UserRole.CUSTOMER));
        }

        @Test
        @DisplayName("특정 사용자 토큰 비활성화 - 성공")
        void deactivateSpecificUserTokens_Success() {
            // given
            Long targetUserId = 1L;
            given(customerRepository.existsById(targetUserId)).willReturn(true);

            // when
            var result = fcmTokenController.deactivateToken(targetUserId);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            then(fcmTokenService).should().deactivateUserTokens(eq(targetUserId), eq(UserRole.CUSTOMER));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 토큰 비활성화 - 실패")
        void deactivateTokens_UserNotFound_Failure() {
            // given
            Long targetUserId = 999L;
            given(customerRepository.existsById(targetUserId)).willReturn(false);
            given(ownerRepository.existsById(targetUserId)).willReturn(false);

            // when & then
            assertThrows(BusinessException.class, () ->
                fcmTokenController.deactivateToken(targetUserId));
        }
    }

    @Nested
    @DisplayName("토큰 삭제 API 테스트")
    class DeleteTokenTest {

        @Test
        @DisplayName("토큰 삭제 - 성공")
        void deleteToken_Success() {
            // given
            String fcmToken = "test-fcm-token";
            given(fcmTokenService.deleteToken(fcmToken)).willReturn(true);

            // when
            var result = fcmTokenController.deleteToken(fcmToken);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            then(fcmTokenService).should().deleteToken(fcmToken);
        }

        @Test
        @DisplayName("존재하지 않는 토큰 삭제 - 404 응답")
        void deleteToken_NotFound_Returns404() {
            // given
            String fcmToken = "non-existent-token";
            given(fcmTokenService.deleteToken(fcmToken)).willReturn(false);

            // when
            var result = fcmTokenController.deleteToken(fcmToken);

            // then
            assertNotNull(result);
            assertTrue(result.success());
            assertEquals(404, result.status());
            then(fcmTokenService).should().deleteToken(fcmToken);
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("서비스 예외 발생 시 적절한 응답")
        void handleServiceException() {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));
            doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(fcmTokenService).registerOrUpdateToken(eq(1L), eq(UserRole.CUSTOMER), eq(fcmTokenRequest));

            // when & then
            assertThrows(BusinessException.class, () ->
                fcmTokenController.registerToken(customerUserInfo, fcmTokenRequest));
        }
    }
}