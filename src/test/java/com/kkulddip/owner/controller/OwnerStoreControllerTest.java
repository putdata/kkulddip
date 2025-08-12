package com.kkulddip.owner.controller;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.StoreListResponse;
import com.kkulddip.common.exception.GlobalExceptionHandler;
import com.kkulddip.owner.exception.UnauthorizedStoreAccessException;
import com.kkulddip.owner.service.OwnerStoreService;
import com.kkulddip.store.exception.StoreNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerStoreController 테스트")
class OwnerStoreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OwnerStoreService ownerStoreService;

    private final Long ownerId = 1L;
    private final Long storeId = 1L;

    @BeforeEach
    void setUp() {
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
            .standaloneSetup(new OwnerStoreController(ownerStoreService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(jwtUserInfoResolver)
            .build();
    }

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 전체 가게")
    void getOwnerStores_AllStores_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<OwnerStoreResponse> stores = List.of(
            createOwnerStoreResponse(1L, "가게1", true, 10L, 500000.0, now),
            createOwnerStoreResponse(2L, "가게2", false, 0L, 0.0, now),
            createOwnerStoreResponse(3L, "가게3", true, 15L, 750000.0, now)
        );

        StoreListResponse response = new StoreListResponse(
            stores, 3, 2, 1, false, null
        );

        given(ownerStoreService.getOwnerStores(ownerId, null)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/stores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.stores").isArray())
            .andExpect(jsonPath("$.body.stores").value(hasSize(3)))
            .andExpect(jsonPath("$.body.totalCount").value(3))
            .andExpect(jsonPath("$.body.activeCount").value(2))
            .andExpect(jsonPath("$.body.inactiveCount").value(1))
            .andExpect(jsonPath("$.body.hasNext").value(false))
            .andExpect(jsonPath("$.body.stores[0].storeId").value(1))
            .andExpect(jsonPath("$.body.stores[0].storeName").value("가게1"))
            .andExpect(jsonPath("$.body.stores[0].isActive").value(true))
            .andExpect(jsonPath("$.body.stores[0].totalOrderCount").value(10))
            .andExpect(jsonPath("$.body.stores[0].totalRevenue").value(500000.0));

        then(ownerStoreService).should().getOwnerStores(ownerId, null);
    }

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 활성 가게만")
    void getOwnerStores_ActiveOnly_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<OwnerStoreResponse> activeStores = List.of(
            createOwnerStoreResponse(1L, "가게1", true, 10L, 500000.0, now),
            createOwnerStoreResponse(3L, "가게3", true, 15L, 750000.0, now)
        );

        StoreListResponse response = new StoreListResponse(
            activeStores, 2, 2, 0, false, null
        );

        given(ownerStoreService.getOwnerStores(ownerId, true)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/stores")
                .param("activeOnly", "true")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.body.stores").value(hasSize(2)))
            .andExpect(jsonPath("$.body.activeCount").value(2))
            .andExpect(jsonPath("$.body.inactiveCount").value(0));

        then(ownerStoreService).should().getOwnerStores(ownerId, true);
    }

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 빈 목록")
    void getOwnerStores_EmptyList_Success() throws Exception {
        // given
        StoreListResponse response = new StoreListResponse(
            List.of(), 0, 0, 0, false, null
        );

        given(ownerStoreService.getOwnerStores(ownerId, null)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/stores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.body.stores").value(hasSize(0)))
            .andExpect(jsonPath("$.body.totalCount").value(0))
            .andExpect(jsonPath("$.body.activeCount").value(0))
            .andExpect(jsonPath("$.body.inactiveCount").value(0));

        then(ownerStoreService).should().getOwnerStores(ownerId, null);
    }

    @Test
    @DisplayName("특정 가게 조회 성공")
    void getOwnerStore_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        OwnerStoreResponse response = createOwnerStoreResponse(
            storeId, "테스트 가게", true, 25L, 1250000.0, now
        );

        given(ownerStoreService.getOwnerStore(ownerId, storeId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.storeId").value(1))
            .andExpect(jsonPath("$.body.storeName").value("테스트 가게"))
            .andExpect(jsonPath("$.body.isActive").value(true))
            .andExpect(jsonPath("$.body.totalOrderCount").value(25))
            .andExpect(jsonPath("$.body.totalRevenue").value(1250000.0));

        then(ownerStoreService).should().getOwnerStore(ownerId, storeId);
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 권한 없음")
    void getOwnerStore_UnauthorizedAccess() throws Exception {
        // given
        given(ownerStoreService.getOwnerStore(ownerId, storeId))
            .willThrow(new UnauthorizedStoreAccessException(storeId, ownerId));

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        then(ownerStoreService).should().getOwnerStore(ownerId, storeId);
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 가게 없음")
    void getOwnerStore_StoreNotFound() throws Exception {
        // given
        Long testStoreId = 999L;
        given(ownerStoreService.getOwnerStore(ownerId, testStoreId))
            .willThrow(new StoreNotFoundException("Store not found with ID: " + testStoreId));

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}", testStoreId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        then(ownerStoreService).should().getOwnerStore(ownerId, testStoreId);
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 잘못된 storeId 타입")
    void getOwnerStore_InvalidStoreIdType() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        then(ownerStoreService).shouldHaveNoInteractions();
    }

    private OwnerStoreResponse createOwnerStoreResponse(Long storeId, String storeName, Boolean isActive,
                                                       Long totalOrderCount, Double totalRevenue,
                                                       LocalDateTime createdAt) {
        return new OwnerStoreResponse(
            storeId, storeName, "010-1234-5678", "테스트 가게 설명", "09:00-18:00",
            isActive, 4.5, 10L, "123-45-67890", "서울시 강남구",
            "https://example.com/store.jpg", 37.123, 127.456,
            createdAt, createdAt, totalOrderCount, totalRevenue
        );
    }

}