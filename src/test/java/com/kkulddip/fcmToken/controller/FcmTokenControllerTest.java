package com.kkulddip.fcmToken.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.controller.FcmTokenController;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.dto.response.FcmTokenResponse;
import com.kkulddip.domain.userToken.enums.DeviceType;
import com.kkulddip.domain.userToken.service.FcmTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FcmTokenController.class)
@DisplayName("FcmTokenController 테스트")
class FcmTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FcmTokenService fcmTokenService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private OwnerRepository ownerRepository;

    private JwtUserInfo customerUserInfo;
    private JwtUserInfo ownerUserInfo;
    private Customer customer;
    private Owner owner;
    private FcmTokenRequest fcmTokenRequest;
    private FcmTokenResponse fcmTokenResponse;

    @BeforeEach
    void setUp() {
        customerUserInfo = new JwtUserInfo(
            "customer@test.com",
            "CUSTOMER",
            "google",
            "google-id-123"
        );

        ownerUserInfo = new JwtUserInfo(
            "owner@test.com",
            "OWNER",
            "google",
            "google-id-456"
        );

        customer = Customer.builder()
            .email("customer@test.com")
            .name("Customer User")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google-id-123")
            .build();

        owner = Owner.builder()
            .email("owner@test.com")
            .name("Owner User")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google-id-456")
            .build();

        fcmTokenRequest = new FcmTokenRequest("test-fcm-token", DeviceType.ANDROID);
        fcmTokenResponse = FcmTokenResponse.builder()
            .tokenId(1L)
            .fcmToken("test-fcm-token")
            .deviceType(DeviceType.ANDROID)
            .isActive(true)
            .registeredAt(LocalDateTime.now())
            .build();
    }

    @Nested
    @DisplayName("토큰 등록 API 테스트")
    class RegisterTokenTest {

        @Test
        @DisplayName("고객 토큰 등록 - 성공")
        @WithMockUser
        void registerCustomerToken_Success() throws Exception {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));
            given(fcmTokenService.registerOrUpdateToken(anyLong(), any(UserRole.class), any(FcmTokenRequest.class)))
                .willReturn(fcmTokenResponse);

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens")
                    .with(authentication(createAuthentication(customerUserInfo)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fcmTokenRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            then(fcmTokenService).should().registerOrUpdateToken(eq(1L), eq(UserRole.CUSTOMER), any(FcmTokenRequest.class));
        }

        @Test
        @DisplayName("사업자 토큰 등록 - 성공")
        @WithMockUser
        void registerOwnerToken_Success() throws Exception {
            // given
            given(ownerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-456")).willReturn(Optional.of(owner));
            given(fcmTokenService.registerOrUpdateToken(anyLong(), any(UserRole.class), any(FcmTokenRequest.class)))
                .willReturn(fcmTokenResponse);

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens")
                    .with(authentication(createAuthentication(ownerUserInfo)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fcmTokenRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            then(fcmTokenService).should().registerOrUpdateToken(eq(2L), eq(UserRole.OWNER), any(FcmTokenRequest.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 토큰 등록 - 실패")
        @WithMockUser
        void registerToken_UserNotFound_Failure() throws Exception {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.empty());

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens")
                    .with(authentication(createAuthentication(customerUserInfo)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fcmTokenRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("잘못된 요청 데이터 - 실패")
        @WithMockUser
        void registerToken_InvalidRequest_Failure() throws Exception {
            // given
            FcmTokenRequest invalidRequest = new FcmTokenRequest("", null);

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens")
                    .with(authentication(createAuthentication(customerUserInfo)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("토큰 비활성화 API 테스트")
    class DeactivateTokenTest {

        @Test
        @DisplayName("현재 사용자 토큰 비활성화 - 성공")
        @WithMockUser
        void deactivateCurrentUserTokens_Success() throws Exception {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens/deactivate")
                    .with(authentication(createAuthentication(customerUserInfo))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            then(fcmTokenService).should().deactivateUserTokens(1L, UserRole.CUSTOMER);
        }

        @Test
        @DisplayName("특정 사용자 토큰 비활성화 - 성공")
        @WithMockUser
        void deactivateSpecificUserTokens_Success() throws Exception {
            // given
            Long targetUserId = 1L;
            given(customerRepository.existsById(targetUserId)).willReturn(true);

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens/users/{userId}/deactivate", targetUserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            then(fcmTokenService).should().deactivateUserTokens(targetUserId, UserRole.CUSTOMER);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 토큰 비활성화 - 실패")
        @WithMockUser
        void deactivateTokens_UserNotFound_Failure() throws Exception {
            // given
            Long targetUserId = 999L;
            given(customerRepository.existsById(targetUserId)).willReturn(false);
            given(ownerRepository.existsById(targetUserId)).willReturn(false);

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens/users/{userId}/deactivate", targetUserId))
                .andDo(print())
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("토큰 삭제 API 테스트")
    class DeleteTokenTest {

        @Test
        @DisplayName("토큰 삭제 - 성공")
        @WithMockUser
        void deleteToken_Success() throws Exception {
            // given
            String fcmToken = "test-fcm-token";
            given(fcmTokenService.deleteToken(fcmToken)).willReturn(true);

            // when & then
            mockMvc.perform(delete("/api/v1/fcm-tokens/{fcmToken}", fcmToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            then(fcmTokenService).should().deleteToken(fcmToken);
        }

        @Test
        @DisplayName("존재하지 않는 토큰 삭제 - 404 응답")
        @WithMockUser
        void deleteToken_NotFound_Returns404() throws Exception {
            // given
            String fcmToken = "non-existent-token";
            given(fcmTokenService.deleteToken(fcmToken)).willReturn(false);

            // when & then
            mockMvc.perform(delete("/api/v1/fcm-tokens/{fcmToken}", fcmToken))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

            then(fcmTokenService).should().deleteToken(fcmToken);
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("서비스 예외 발생 시 적절한 응답")
        @WithMockUser
        void handleServiceException() throws Exception {
            // given
            given(customerRepository.findByOauth2ProviderAndOauth2ProviderId(
                OAuth2Provider.GOOGLE, "google-id-123")).willReturn(Optional.of(customer));
            doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(fcmTokenService).registerOrUpdateToken(anyLong(), any(UserRole.class), any(FcmTokenRequest.class));

            // when & then
            mockMvc.perform(post("/api/v1/fcm-tokens")
                    .with(authentication(createAuthentication(customerUserInfo)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fcmTokenRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
        }
    }

    private Authentication createAuthentication(JwtUserInfo userInfo) {
        return new TestingAuthenticationToken(userInfo.username(), null, userInfo.role());
    }
}