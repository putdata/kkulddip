package com.kkulddip.owner.controller;

import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.SettlementQueryRequest;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.common.exception.GlobalExceptionHandler;
import com.kkulddip.owner.exception.UnauthorizedStoreAccessException;
import com.kkulddip.owner.service.OwnerSettlementService;
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

import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerSettlementController 테스트")
class OwnerSettlementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OwnerSettlementService ownerSettlementService;

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
            .standaloneSetup(new OwnerSettlementController(ownerSettlementService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(jwtUserInfoResolver)
            .build();
    }

    @Test
    @DisplayName("가게 정산 조회 성공")
    void getStoreSettlement_Success() throws Exception {
        // given
        Integer year = 2024;
        Integer month = 3;
        
        SettlementResponse response = new SettlementResponse(
            storeId, "테스트 가게", YearMonth.of(2024, 3),
            1000000L, 100L, 10000L,
            800000L, 25L,
            80L, 25L
        );

        given(ownerSettlementService.getStoreSettlement(eq(ownerId), eq(storeId), any(SettlementQueryRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("year", year.toString())
                .param("month", month.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.storeId").value(1))
            .andExpect(jsonPath("$.body.storeName").value("테스트 가게"))
            .andExpect(jsonPath("$.body.period[0]").value(2024))
            .andExpect(jsonPath("$.body.period[1]").value(3))
            .andExpect(jsonPath("$.body.totalRevenue").value(1000000))
            .andExpect(jsonPath("$.body.orderCount").value(100))
            .andExpect(jsonPath("$.body.avgOrderAmount").value(10000))
            .andExpect(jsonPath("$.body.previousMonthRevenue").value(800000))
            .andExpect(jsonPath("$.body.revenueGrowthRate").value(25))
            .andExpect(jsonPath("$.body.previousMonthOrderCount").value(80))
            .andExpect(jsonPath("$.body.orderCountGrowthRate").value(25));

        then(ownerSettlementService).should().getStoreSettlement(eq(ownerId), eq(storeId), any(SettlementQueryRequest.class));
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 권한 없음")
    void getStoreSettlement_UnauthorizedAccess() throws Exception {
        // given
        given(ownerSettlementService.getStoreSettlement(eq(ownerId), eq(storeId), any(SettlementQueryRequest.class)))
            .willThrow(new UnauthorizedStoreAccessException(storeId, ownerId));

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("year", "2024")
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        then(ownerSettlementService).should().getStoreSettlement(eq(ownerId), eq(storeId), any(SettlementQueryRequest.class));
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 가게 없음")
    void getStoreSettlement_StoreNotFound() throws Exception {
        // given
        Long testStoreId = 999L;
        
        given(ownerSettlementService.getStoreSettlement(eq(ownerId), eq(testStoreId), any(SettlementQueryRequest.class)))
            .willThrow(new StoreNotFoundException("Store not found with ID: " + testStoreId));

        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", testStoreId)
                .param("year", "2024")
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 잘못된 년도")
    void getStoreSettlement_InvalidYear() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("year", "1999") // 2020년 미만
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        then(ownerSettlementService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 잘못된 월")
    void getStoreSettlement_InvalidMonth() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("year", "2024")
                .param("month", "13") // 12 초과
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        then(ownerSettlementService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 필수 파라미터 누락")
    void getStoreSettlement_MissingParams() throws Exception {
        // when & then - year 파라미터 누락
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // when & then - month 파라미터 누락
        mockMvc.perform(get("/v1/owners/stores/{storeId}/settlement", storeId)
                .param("year", "2024")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        then(ownerSettlementService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("전체 정산 요약 조회 성공")
    void getSettlementSummary_Success() throws Exception {
        // given
        List<SettlementResponse> storeSettlements = List.of(
            new SettlementResponse(1L, "가게1", YearMonth.of(2024, 3),
                500000L, 50L, 10000L,
                400000L, 25L, 40L, 25L),
            new SettlementResponse(2L, "가게2", YearMonth.of(2024, 3),
                300000L, 30L, 10000L,
                250000L, 20L, 25L, 20L)
        );

        SettlementSummaryResponse response = new SettlementSummaryResponse(
            YearMonth.of(2024, 3),
            800000L,
            80L,
            10000L,
            2,
            storeSettlements
        );

        given(ownerSettlementService.getOwnerSettlementSummary(eq(ownerId), any(SettlementQueryRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/settlement/summary")
                .param("year", "2024")
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.body.period[0]").value(2024))
            .andExpect(jsonPath("$.body.period[1]").value(3))
            .andExpect(jsonPath("$.body.totalRevenue").value(800000))
            .andExpect(jsonPath("$.body.totalOrderCount").value(80))
            .andExpect(jsonPath("$.body.avgOrderAmount").value(10000))
            .andExpect(jsonPath("$.body.storeCount").value(2))
            .andExpect(jsonPath("$.body.storeSettlements").value(hasSize(2)))
            .andExpect(jsonPath("$.body.storeSettlements[0].storeId").value(1))
            .andExpect(jsonPath("$.body.storeSettlements[0].storeName").value("가게1"))
            .andExpect(jsonPath("$.body.storeSettlements[1].storeId").value(2))
            .andExpect(jsonPath("$.body.storeSettlements[1].storeName").value("가게2"));

        then(ownerSettlementService).should().getOwnerSettlementSummary(eq(ownerId), any(SettlementQueryRequest.class));
    }

    @Test
    @DisplayName("전체 정산 요약 조회 성공 - 가게 없음")
    void getSettlementSummary_NoStores_Success() throws Exception {
        // given

        SettlementSummaryResponse response = new SettlementSummaryResponse(
            YearMonth.of(2024, 3),
            0L,
            0L,
            0L,
            0,
            List.of()
        );

        given(ownerSettlementService.getOwnerSettlementSummary(eq(ownerId), any(SettlementQueryRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/owners/settlement/summary")
                .param("year", "2024")
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.body.totalRevenue").value(0))
            .andExpect(jsonPath("$.body.totalOrderCount").value(0))
            .andExpect(jsonPath("$.body.storeCount").value(0))
            .andExpect(jsonPath("$.body.storeSettlements").value(hasSize(0)));

        then(ownerSettlementService).should().getOwnerSettlementSummary(eq(ownerId), any(SettlementQueryRequest.class));
    }

    @Test
    @DisplayName("전체 정산 요약 조회 실패 - 잘못된 파라미터")
    void getSettlementSummary_InvalidParams() throws Exception {

        // when & then
        mockMvc.perform(get("/v1/owners/settlement/summary")
                .param("year", "invalid")
                .param("month", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        then(ownerSettlementService).shouldHaveNoInteractions();
    }

}