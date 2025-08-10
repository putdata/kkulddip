package com.kkulddip.customerProfile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.exception.GlobalExceptionHandler;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.*;
import com.kkulddip.customerProfile.service.CustomerProfileService;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("citest")
@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerTest {
    
    @InjectMocks
    private CustomerProfileController controller;
    
    @Mock
    private CustomerProfileService customerProfileService;
    
    @Mock
    private com.kkulddip.customerProfile.location.service.CustomerLocationService customerLocationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JwtUserInfo jwtUserInfo;
    private Long customerId = 1L;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
            
        jwtUserInfo = new JwtUserInfo(
            "1",
            "test@example.com",
            "CUSTOMER",
            "GOOGLE",
            "google123"
        );
    }
    
    private void setupSecurityContext() {
        // SecurityContext 모킹 설정 (필요한 테스트에서만 호출)
        SecurityContext securityContext = mock(SecurityContext.class);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(jwtUserInfo, null, null);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    
    @Test
    @DisplayName("프로필 조회 API 테스트")
    void getMyProfile() throws Exception {
        setupSecurityContext();
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
        
        when(customerProfileService.getProfile(customerId)).thenReturn(response);
        
        mockMvc.perform(get("/v1/customers/profile")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.customerId").value(customerId))
            .andExpect(jsonPath("$.body.email").value("test@example.com"))
            .andExpect(jsonPath("$.body.name").value("테스트 고객"))
            .andExpect(jsonPath("$.body.level").value("SPROUT_BEE"));
    }
    
    @Test
    @DisplayName("프로필 수정 API 테스트")
    void updateProfile() throws Exception {
        setupSecurityContext();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .name("새로운 이름")
            .profileImageUrl("https://example.com/new-profile.jpg")
            .build();
        
        UpdateProfileResponse response = UpdateProfileResponse.builder()
            .customerId(customerId)
            .name("새로운 이름")
            .profileImageUrl("https://example.com/new-profile.jpg")
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerProfileService.updateProfile(eq(customerId), any(UpdateProfileRequest.class)))
            .thenReturn(response);
        
        mockMvc.perform(put("/v1/customers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.customerId").value(customerId))
            .andExpect(jsonPath("$.body.name").value("새로운 이름"));
    }
    
    @Test
    @DisplayName("위치 정보 업데이트 API 테스트")
    void updateLocation() throws Exception {
        setupSecurityContext();
        UpdateLocationRequest request = UpdateLocationRequest.builder()
            .address("서울시 송파구")
            .latitude(37.5145)
            .longitude(127.1058)
            .build();
        
        UpdateLocationResponse response = UpdateLocationResponse.builder()
            .customerId(customerId)
            .address("서울시 송파구")
            .latitude(37.5145)
            .longitude(127.1058)
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(customerProfileService.updateLocation(eq(customerId), any(UpdateLocationRequest.class)))
            .thenReturn(response);
        
        mockMvc.perform(put("/v1/customers/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.address").value("서울시 송파구"))
            .andExpect(jsonPath("$.body.latitude").value(37.5145))
            .andExpect(jsonPath("$.body.longitude").value(127.1058));
    }
    
    @Test
    @DisplayName("통계 조회 API 테스트")
    void getMyStats() throws Exception {
        setupSecurityContext();
        CustomerStatsResponse response = CustomerStatsResponse.builder()
            .customerId(customerId)
            .level(CustomerLevel.SPROUT_BEE)
            .totalOrder(5)
            .totalMoneySaved(10000L)
            .totalCo2Saved(5.5)
            .ordersUntilNextLevel(5)
            .nextLevel(CustomerLevel.WORKER_BEE)
            .build();
        
        when(customerProfileService.getStats(customerId)).thenReturn(response);
        
        mockMvc.perform(get("/v1/customers/stats")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.customerId").value(customerId))
            .andExpect(jsonPath("$.body.level").value("SPROUT_BEE"))
            .andExpect(jsonPath("$.body.totalOrder").value(5))
            .andExpect(jsonPath("$.body.ordersUntilNextLevel").value(5))
            .andExpect(jsonPath("$.body.nextLevel").value("WORKER_BEE"));
    }
    
    @Test
    @DisplayName("잘못된 프로필 수정 요청 시 400 에러")
    void updateProfile_InvalidRequest() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .name("")  // 빈 이름은 유효하지 않음
            .profileImageUrl("https://example.com/new-profile.jpg")
            .build();
        
        mockMvc.perform(put("/v1/customers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    }
    
    @Test
    @DisplayName("잘못된 위치 정보 업데이트 요청 시 400 에러")
    void updateLocation_InvalidRequest() throws Exception {
        UpdateLocationRequest request = UpdateLocationRequest.builder()
            .address("서울시 송파구")
            .latitude(91.0)  // 위도는 90 이하여야 함
            .longitude(127.1058)
            .build();
        
        mockMvc.perform(put("/v1/customers/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"))
            .andExpect(jsonPath("$.body").isArray())
            .andExpect(jsonPath("$.body[0].field").value("latitude"))
            .andExpect(jsonPath("$.body[0].message").value("위도는 90 이하여야 합니다"));
    }
}