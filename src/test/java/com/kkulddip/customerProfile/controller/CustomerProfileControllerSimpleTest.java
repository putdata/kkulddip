package com.kkulddip.customerProfile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.exception.GlobalExceptionHandler;
import com.kkulddip.customerProfile.controller.CustomerProfileController;
import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.*;
import com.kkulddip.customerProfile.service.CustomerProfileService;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Customer Profile Controller 단순화된 테스트
 * Security를 완전히 비활성화하여 테스트합니다.
 */
@ActiveProfiles("citest") 
@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerSimpleTest {
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @Mock
    private CustomerProfileService customerProfileService;
    
    @Mock
    private com.kkulddip.customerProfile.location.service.CustomerLocationService customerLocationService;
    
    @InjectMocks
    private CustomerProfileController controller;
    
    private MockedStatic<SecurityContextHolder> mockedSecurityContext;
    private final Long customerId = 1L;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }
    
    private void setupSecurityContext() {
        // SecurityContextHolder를 Mock하여 getCurrentCustomerId()가 동작하도록 설정
        mockedSecurityContext = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        com.kkulddip.common.security.jwt.JwtUserInfo jwtUserInfo = 
            new com.kkulddip.common.security.jwt.JwtUserInfo(
                customerId.toString(),
                "test@example.com",
                "CUSTOMER",
                "GOOGLE",
                "google123"
            );
        
        when(authentication.getPrincipal()).thenReturn(jwtUserInfo);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }
    
    @AfterEach
    void tearDown() {
        if (mockedSecurityContext != null) {
            mockedSecurityContext.close();
        }
    }
    
    @Test
    @DisplayName("프로필 조회 성공")
    void getMyProfile() throws Exception {
        setupSecurityContext();
        
        // Given
        CustomerProfileResponse response = CustomerProfileResponse.builder()
            .customerId(customerId)
            .email("test@example.com")
            .name("테스트 고객")
            .profileImageUrl("https://example.com/profile.jpg")
            .address("서울시 강남구")
            .latitude(37.5665)
            .longitude(126.9780)
            .level(CustomerLevel.SPROUT_BEE)
            .createdAt(LocalDateTime.now())
            .lastActiveAt(LocalDateTime.now())
            .build();
        
        when(customerProfileService.getProfileWithUpdatedStats(anyLong())).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/v1/customers/profile")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.customerId").value(customerId.intValue()))
            .andExpect(jsonPath("$.body.email").value("test@example.com"))
            .andExpect(jsonPath("$.body.name").value("테스트 고객"))
            .andExpect(jsonPath("$.body.level").value("SPROUT_BEE"));
    }
    
    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile() throws Exception {
        setupSecurityContext();
        
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
            "새로운 이름",
            "https://example.com/new-profile.jpg"
        );
        
        UpdateProfileResponse response = UpdateProfileResponse.builder()
            .customerId(customerId)
            .name("새로운 이름")
            .profileImageUrl("https://example.com/new-profile.jpg")
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerProfileService.updateProfile(anyLong(), any(UpdateProfileRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(put("/v1/customers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.body.name").value("새로운 이름"))
            .andExpect(jsonPath("$.body.profileImageUrl").value("https://example.com/new-profile.jpg"));
    }
    
    @Test
    @DisplayName("위치 정보 업데이트 성공")
    void updateLocation() throws Exception {
        setupSecurityContext();
        
        // Given
        UpdateLocationRequest request = new UpdateLocationRequest(
            "서울시 강남구 테헤란로 123",
            37.5012,
            127.0396
        );
        
        UpdateLocationResponse response = UpdateLocationResponse.builder()
            .customerId(customerId)
            .address("서울시 강남구 테헤란로 123")
            .latitude(37.5012)
            .longitude(127.0396)
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerProfileService.updateLocation(anyLong(), any(UpdateLocationRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(put("/v1/customers/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.body.address").value("서울시 강남구 테헤란로 123"))
            .andExpect(jsonPath("$.body.latitude").value(37.5012))
            .andExpect(jsonPath("$.body.longitude").value(127.0396));
    }
    
    @Test
    @DisplayName("통계 조회 성공")
    void getMyStats() throws Exception {
        setupSecurityContext();
        
        // Given
        CustomerStatsResponse response = CustomerStatsResponse.builder()
            .customerId(customerId)
            .level(CustomerLevel.SPROUT_BEE)
            .totalOrder(5)
            .totalMoneySaved(10000L)
            .totalCo2Saved(5.5)
            .ordersUntilNextLevel(5)
            .nextLevel(CustomerLevel.WORKER_BEE)
            .build();
        
        when(customerProfileService.getUpdatedStats(anyLong())).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/v1/customers/stats")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.customerId").value(customerId.intValue()))
            .andExpect(jsonPath("$.body.totalOrder").value(5))
            .andExpect(jsonPath("$.body.totalMoneySaved").value(10000))
            .andExpect(jsonPath("$.body.totalCo2Saved").value(5.5));
    }
    
    @Test
    @DisplayName("잘못된 위치 정보 업데이트 시 400 에러")
    void updateLocation_InvalidCoordinates() throws Exception {
        // Given
        UpdateLocationRequest request = new UpdateLocationRequest(
            "Invalid Location",
            200.0,  // 유효하지 않은 위도
            200.0   // 유효하지 않은 경도
        );
        
        // When & Then
        mockMvc.perform(put("/v1/customers/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}