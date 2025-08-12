package com.kkulddip.owner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.common.exception.GlobalExceptionHandler;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.UpdateOwnerProfileRequest;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.exception.OwnerNotFoundException;
import com.kkulddip.owner.service.OwnerProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerProfileController 테스트")
class OwnerProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OwnerProfileService ownerProfileService;

    private ObjectMapper objectMapper;
    private final Long ownerId = 1L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        HandlerMethodArgumentResolver jwtUserInfoResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class) 
                    && parameter.getParameterType().equals(JwtUserInfo.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return new JwtUserInfo("1", "테스트 사장", "OWNER", "GOOGLE", "google123");
            }
        };

        mockMvc = MockMvcBuilders
            .standaloneSetup(new OwnerProfileController(ownerProfileService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(jwtUserInfoResolver)
            .build();
    }

    @Test
    @DisplayName("Owner 프로필 조회 성공")
    void getOwnerProfile_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        OwnerProfileResponse response = new OwnerProfileResponse(
            ownerId, "test@example.com", "테스트 사장", "https://example.com/profile.jpg",
            "GOOGLE", now.minusDays(1), now.minusMonths(1), now,
            "123-45-67890", "대표자명", 3, 2
        );

        given(ownerProfileService.getOwnerProfile(ownerId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.ownerId").value(1))
            .andExpect(jsonPath("$.body.email").value("test@example.com"))
            .andExpect(jsonPath("$.body.name").value("테스트 사장"))
            .andExpect(jsonPath("$.body.profileImageUrl").value("https://example.com/profile.jpg"))
            .andExpect(jsonPath("$.body.oauth2Provider").value("GOOGLE"))
            .andExpect(jsonPath("$.body.businessNumber").value("123-45-67890"))
            .andExpect(jsonPath("$.body.representativeName").value("대표자명"))
            .andExpect(jsonPath("$.body.totalStoreCount").value(3))
            .andExpect(jsonPath("$.body.activeStoreCount").value(2));

        then(ownerProfileService).should().getOwnerProfile(ownerId);
        then(ownerProfileService).should().updateLastActiveAt(ownerId);
    }

    @Test
    @DisplayName("Owner 프로필 조회 실패 - Owner 없음")
    void getOwnerProfile_OwnerNotFound() throws Exception {
        // given
        given(ownerProfileService.getOwnerProfile(ownerId))
            .willThrow(new OwnerNotFoundException(ownerId));

        // when & then
        mockMvc.perform(get("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        then(ownerProfileService).should().getOwnerProfile(ownerId);
    }

    @Test
    @DisplayName("Owner 프로필 수정 성공")
    void updateOwnerProfile_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "수정된 사장", "https://example.com/new-profile.jpg",
            "987-65-43210", "새 대표자명"
        );

        OwnerProfileResponse response = new OwnerProfileResponse(
            ownerId, "test@example.com", "수정된 사장", "https://example.com/new-profile.jpg",
            "GOOGLE", now, now.minusMonths(1), now,
            "987-65-43210", "새 대표자명", 3, 2
        );

        given(ownerProfileService.updateOwnerProfile(eq(ownerId), any(UpdateOwnerProfileRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(put("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.ownerId").value(1))
            .andExpect(jsonPath("$.body.name").value("수정된 사장"))
            .andExpect(jsonPath("$.body.profileImageUrl").value("https://example.com/new-profile.jpg"))
            .andExpect(jsonPath("$.body.businessNumber").value("987-65-43210"))
            .andExpect(jsonPath("$.body.representativeName").value("새 대표자명"));

        then(ownerProfileService).should().updateOwnerProfile(eq(ownerId), any(UpdateOwnerProfileRequest.class));
    }

    @Test
    @DisplayName("Owner 프로필 수정 실패 - 잘못된 요청 데이터")
    void updateOwnerProfile_InvalidRequest() throws Exception {
        // given
        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "", // 빈 이름 - 유효성 검사 실패
            "https://example.com/profile.jpg",
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        then(ownerProfileService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Owner 프로필 수정 실패 - 이름 길이 초과")
    void updateOwnerProfile_NameTooLong() throws Exception {
        // given
        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "a".repeat(51), // 51자 - 50자 제한 초과
            "https://example.com/profile.jpg",
            null,
            null
        );

        // when & then
        mockMvc.perform(put("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        then(ownerProfileService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Owner 프로필 수정 실패 - Owner 없음")
    void updateOwnerProfile_OwnerNotFound() throws Exception {
        // given
        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "수정된 사장", "https://example.com/profile.jpg", null, null
        );

        given(ownerProfileService.updateOwnerProfile(eq(ownerId), any(UpdateOwnerProfileRequest.class)))
            .willThrow(new OwnerNotFoundException(ownerId));

        // when & then
        mockMvc.perform(put("/v1/owners/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());

        then(ownerProfileService).should().updateOwnerProfile(eq(ownerId), any(UpdateOwnerProfileRequest.class));
    }
}