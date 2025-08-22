//package com.kkulddip.store.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kkulddip.common.response.ApiResponse;
//import com.kkulddip.common.security.exception.JwtAuthenticationErrorHandler;
//import com.kkulddip.common.security.jwt.JwtUtil;
//import com.kkulddip.store.common.Page;
//import com.kkulddip.store.dto.request.StoreListRequest;
//import com.kkulddip.store.dto.request.StoreSearchRequest;
//import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
//import com.kkulddip.store.dto.response.StoreDetailDto;
//import com.kkulddip.store.dto.response.StoreResponseDto;
//import com.kkulddip.store.exception.StoreNotFoundException;
//import com.kkulddip.store.exception.StoreValidationException;
//import com.kkulddip.store.service.StoreService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = StoreController.class, excludeAutoConfiguration = {
//    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
//    SecurityAutoConfiguration.class,
//    SecurityFilterAutoConfiguration.class
//})
//@AutoConfigureMockMvc(addFilters = false)
//@DisplayName("StoreController 테스트")
//@ActiveProfiles("test")
//class StoreControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private JwtAuthenticationErrorHandler jwtAuthenticationErrorHandler;
//
//    @MockBean
//    private StoreService storeService;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    private Page<StoreResponseDto> testStorePage;
//    private StoreDetailDto testStoreDetail;
//    private List<DdipBoxCardViewDto> testDdipBoxes;
//
//    @BeforeEach
//    void setUp() {
//        StoreResponseDto storeDto = StoreResponseDto.of(1L, "테스트 마트", "서울시 강남구");
//
//        testStorePage = Page.of(
//            Arrays.asList(storeDto),
//            10,
//            false,
//            null,
//            true
//        );
//
//        testStoreDetail = StoreDetailDto.of(1L, "테스트 마트", "서울시 강남구");
//
//        DdipBoxCardViewDto ddipBoxDto = DdipBoxCardViewDto.of(1L, "테스트 띱박스");
//
//        testDdipBoxes = Arrays.asList(ddipBoxDto);
//    }
//
//    @Test
//    @DisplayName("가게 목록을 조회한다")
//    void getStores() throws Exception {
//        // given
//        given(storeService.getStores(any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then
//        mockMvc.perform(get("/v1/store")
//                .param("userLatitude", "37.5665")
//                .param("userLongitude", "126.9780")
//                .param("sortBy", "id")
//                .param("size", "10")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.status").value(200))
//            .andExpect(jsonPath("$.body.content").isArray())
//            .andExpect(jsonPath("$.body.content[0].storeId").value(1))
//            .andExpect(jsonPath("$.body.content[0].storeName").value("테스트 마트"))
//            .andExpect(jsonPath("$.body.hasNext").value(false))
//            .andExpect(jsonPath("$.body.isFirst").value(true));
//
//        verify(storeService).getStores(any(StoreListRequest.class));
//    }
//
//    @Test
//    @DisplayName("필수 파라미터 없이 가게 목록을 조회한다")
//    void getStores_WithoutRequiredParams() throws Exception {
//        // given
//        given(storeService.getStores(any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then
//        mockMvc.perform(get("/v1/store")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.content").isArray());
//
//        verify(storeService).getStores(any(StoreListRequest.class));
//    }
//
//    @Test
//    @DisplayName("페이지 크기가 유효 범위를 벗어나면 400 에러가 발생한다")
//    void getStores_InvalidPageSize() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/store")
//                .param("size", "0")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        mockMvc.perform(get("/v1/store")
//                .param("size", "51")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("가게를 검색한다")
//    void searchStores() throws Exception {
//        // given
//        given(storeService.searchStores(any(StoreSearchRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then
//        mockMvc.perform(get("/v1/store/search")
//                .param("keyword", "마트")
//                .param("sortBy", "rating")
//                .param("size", "20")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.content").isArray())
//            .andExpect(jsonPath("$.body.content[0].storeName").value("테스트 마트"));
//
//        verify(storeService).searchStores(any(StoreSearchRequest.class));
//    }
//
//    @Test
//    @DisplayName("검색 키워드 없이 검색하면 400 에러가 발생한다")
//    void searchStores_WithoutKeyword() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/store/search")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("빈 검색 키워드로 검색하면 400 에러가 발생한다")
//    void searchStores_EmptyKeyword() throws Exception {
//        // given
//        given(storeService.searchStores(any(StoreSearchRequest.class)))
//            .willThrow(new StoreValidationException(
//                com.kkulddip.common.exception.ErrorCode.STORE_SEARCH_KEYWORD_EMPTY));
//
//        // when & then
//        mockMvc.perform(get("/v1/store/search")
//                .param("keyword", "")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("카테고리별 가게를 조회한다")
//    void getStoresByCategory() throws Exception {
//        // given
//        given(storeService.getStoresByCategory(eq("유기농"), any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then
//        mockMvc.perform(get("/v1/store/category/유기농")
//                .param("sortBy", "distance")
//                .param("userLatitude", "37.5665")
//                .param("userLongitude", "126.9780")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.content").isArray());
//
//        verify(storeService).getStoresByCategory(eq("유기농"), any(StoreListRequest.class));
//    }
//
//    @Test
//    @DisplayName("가게 상세 정보를 조회한다")
//    void getStoreDetail() throws Exception {
//        // given
//        given(storeService.getStoreDetail(1L))
//            .willReturn(testStoreDetail);
//
//        // when & then
//        mockMvc.perform(get("/v1/store/1")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.storeId").value(1))
//            .andExpect(jsonPath("$.body.storeName").value("테스트 마트"));
//
//        verify(storeService).getStoreDetail(1L);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 가게 조회 시 404 에러가 발생한다")
//    void getStoreDetail_NotFound() throws Exception {
//        // given
//        given(storeService.getStoreDetail(999L))
//            .willThrow(new StoreNotFoundException(999L));
//
//        // when & then
//        mockMvc.perform(get("/v1/store/999")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 가게 ID로 조회 시 400 에러가 발생한다")
//    void getStoreDetail_InvalidId() throws Exception {
//        // given
//        given(storeService.getStoreDetail(0L))
//            .willThrow(new StoreValidationException(
//                com.kkulddip.common.exception.ErrorCode.STORE_INVALID_ID, "가게 ID: 0"));
//
//        // when & then
//        mockMvc.perform(get("/v1/store/0")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("가게의 띱박스 목록을 조회한다")
//    void getStoreDdipBoxes() throws Exception {
//        // given
//        given(storeService.getStoreDdipBoxes(1L))
//            .willReturn(testDdipBoxes);
//
//        // when & then
//        mockMvc.perform(get("/v1/store/1/ddipboxes")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body").isArray())
//            .andExpect(jsonPath("$.body[0].ddipboxId").value(1))
//            .andExpect(jsonPath("$.body[0].ddipboxName").value("테스트 띱박스"));
//
//        verify(storeService).getStoreDdipBoxes(1L);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 가게의 띱박스 조회 시 404 에러가 발생한다")
//    void getStoreDdipBoxes_StoreNotFound() throws Exception {
//        // given
//        given(storeService.getStoreDdipBoxes(999L))
//            .willThrow(new StoreNotFoundException(999L));
//
//        // when & then
//        mockMvc.perform(get("/v1/store/999/ddipboxes")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("빈 띱박스 목록도 정상적으로 반환한다")
//    void getStoreDdipBoxes_EmptyList() throws Exception {
//        // given
//        given(storeService.getStoreDdipBoxes(1L))
//            .willReturn(Collections.emptyList());
//
//        // when & then
//        mockMvc.perform(get("/v1/store/1/ddipboxes")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body").isArray())
//            .andExpect(jsonPath("$.body").isEmpty());
//
//        verify(storeService).getStoreDdipBoxes(1L);
//    }
//
//    @Test
//    @DisplayName("커서를 포함한 가게 목록을 조회한다")
//    void getStores_WithCursor() throws Exception {
//        // given
//        String cursor = "eyJpZCI6MTAwfQ"; // Base64 encoded cursor
//        given(storeService.getStores(any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then
//        mockMvc.perform(get("/v1/store")
//                .param("cursor", cursor)
//                .param("size", "5")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true));
//
//        verify(storeService).getStores(any(StoreListRequest.class));
//    }
//
//    @Test
//    @DisplayName("다양한 정렬 옵션으로 가게 목록을 조회한다")
//    void getStores_DifferentSortOptions() throws Exception {
//        // given
//        given(storeService.getStores(any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then - rating으로 정렬
//        mockMvc.perform(get("/v1/store")
//                .param("sortBy", "rating")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//
//        // when & then - distance로 정렬
//        mockMvc.perform(get("/v1/store")
//                .param("sortBy", "distance")
//                .param("userLatitude", "37.5665")
//                .param("userLongitude", "126.9780")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//
//        // when & then - created_at으로 정렬
//        mockMvc.perform(get("/v1/store")
//                .param("sortBy", "created_at")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("경계값 테스트 - 최소/최대 페이지 크기")
//    void getStores_BoundaryPageSize() throws Exception {
//        // given
//        given(storeService.getStores(any(StoreListRequest.class)))
//            .willReturn(testStorePage);
//
//        // when & then - 최소 크기 (1)
//        mockMvc.perform(get("/v1/store")
//                .param("size", "1")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//
//        // when & then - 최대 크기 (50)
//        mockMvc.perform(get("/v1/store")
//                .param("size", "50")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("잘못된 경로 파라미터로 요청하면 400 에러가 발생한다")
//    void getStoreDetail_InvalidPathParameter() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/store/invalid")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("HTTP 메서드가 잘못된 경우 405 에러가 발생한다")
//    void wrongHttpMethod() throws Exception {
//        // when & then
//        mockMvc.perform(post("/v1/store")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isMethodNotAllowed());
//    }
//}