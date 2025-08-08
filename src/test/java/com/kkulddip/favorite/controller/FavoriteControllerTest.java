//package com.kkulddip.favorite.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kkulddip.favorite.dto.request.AddFavoriteRequest;
//import com.kkulddip.favorite.dto.request.GetFavoritesRequest;
//import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
//import com.kkulddip.favorite.dto.response.DeleteFavoriteResponse;
//import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
//import com.kkulddip.favorite.exception.FavoriteAlreadyExistsException;
//import com.kkulddip.favorite.exception.FavoriteNotFoundException;
//import com.kkulddip.favorite.service.FavoriteService;
//import com.kkulddip.store.common.Page;
//import com.kkulddip.store.exception.StoreNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = FavoriteController.class, excludeAutoConfiguration = {
//    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
//})
//@AutoConfigureMockMvc(addFilters = false)
//@DisplayName("FavoriteController 테스트")
//@ActiveProfiles("citest")
//class FavoriteControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private FavoriteService favoriteService;
//
//    private AddFavoriteRequest testAddRequest;
//    private AddFavoriteResponse testAddResponse;
//    private GetFavoritesResponse testGetResponse;
//    private DeleteFavoriteResponse testDeleteResponse;
//    private Page<GetFavoritesResponse> testPage;
//    private LocalDateTime testDateTime;
//
//    @BeforeEach
//    void setUp() {
//        testDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
//
//        testAddRequest = new AddFavoriteRequest(100L, 200L);
//
//        testAddResponse = AddFavoriteResponse.builder()
//            .favoriteId(1L)
//            .customerId(100L)
//            .storeId(200L)
//            .createdAt(testDateTime)
//            .build();
//
//        testGetResponse = GetFavoritesResponse.builder()
//            .favoriteId(1L)
//            .storeId(200L)
//            .storeName("테스트 가게")
//            .storeProfileImage("store-image.jpg")
//            .representationDdipboxName("테스트 띱박스")
//            .representationDdipboxProfileImage(null) // DdipBox에 프로필 이미지 필드가 없음
//            .reviewRating(4.5)
//            .distanceFromCustomer(1.2)
//            .category("한식")
//            .createdAt(testDateTime)
//            .build();
//
//        testDeleteResponse = DeleteFavoriteResponse.builder()
//            .favoriteId(1L)
//            .message("즐겨찾기가 성공적으로 삭제되었습니다.")
//            .build();
//
//        testPage = Page.of(Arrays.asList(testGetResponse), 20, false, null, true);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 추가 성공")
//    void addFavorite_Success() throws Exception {
//        // given
//        given(favoriteService.addFavorite(any(AddFavoriteRequest.class))).willReturn(testAddResponse);
//
//        // when & then
//        mockMvc.perform(post("/v1/favorites")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testAddRequest)))
//            .andDo(print())
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.status").value(201))
//            .andExpect(jsonPath("$.body.favoriteId").value(1L))
//            .andExpect(jsonPath("$.body.customerId").value(100L))
//            .andExpect(jsonPath("$.body.storeId").value(200L));
//
//        verify(favoriteService).addFavorite(any(AddFavoriteRequest.class));
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 추가 실패 - 유효하지 않은 요청")
//    void addFavorite_InvalidRequest() throws Exception {
//        // given
//        AddFavoriteRequest invalidRequest = new AddFavoriteRequest(null, 200L);
//
//        // when & then
//        mockMvc.perform(post("/v1/favorites")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(invalidRequest)))
//            .andDo(print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 추가 실패 - 가게를 찾을 수 없음")
//    void addFavorite_StoreNotFound() throws Exception {
//        // given
//        given(favoriteService.addFavorite(any(AddFavoriteRequest.class)))
//            .willThrow(new StoreNotFoundException(200L));
//
//        // when & then
//        mockMvc.perform(post("/v1/favorites")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testAddRequest)))
//            .andDo(print())
//            .andExpect(status().isNotFound());
//
//        verify(favoriteService).addFavorite(any(AddFavoriteRequest.class));
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 추가 실패 - 이미 존재하는 즐겨찾기")
//    void addFavorite_AlreadyExists() throws Exception {
//        // given
//        given(favoriteService.addFavorite(any(AddFavoriteRequest.class)))
//            .willThrow(new FavoriteAlreadyExistsException(100L, 200L));
//
//        // when & then
//        mockMvc.perform(post("/v1/favorites")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testAddRequest)))
//            .andDo(print())
//            .andExpect(status().isConflict());
//
//        verify(favoriteService).addFavorite(any(AddFavoriteRequest.class));
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 목록 조회 성공")
//    void getFavorites_Success() throws Exception {
//        // given
//        given(favoriteService.getFavorites(any(GetFavoritesRequest.class), isNull(), isNull()))
//            .willReturn(testPage);
//
//        // when & then
//        mockMvc.perform(get("/v1/favorites")
//                .param("customerId", "100")
//                .param("size", "20")
//                .param("sortBy", "CREATED_DESC"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.content").isArray())
//            .andExpect(jsonPath("$.body.content[0].favoriteId").value(1L))
//            .andExpect(jsonPath("$.body.content[0].storeName").value("테스트 가게"));
//
//        verify(favoriteService).getFavorites(any(GetFavoritesRequest.class), isNull(), isNull());
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 목록 조회 - 사용자 위치 포함")
//    void getFavorites_WithUserLocation() throws Exception {
//        // given
//        given(favoriteService.getFavorites(any(GetFavoritesRequest.class), eq(37.5665), eq(126.9780)))
//            .willReturn(testPage);
//
//        // when & then
//        mockMvc.perform(get("/v1/favorites")
//                .param("customerId", "100")
//                .param("userLatitude", "37.5665")
//                .param("userLongitude", "126.9780")
//                .param("size", "20")
//                .param("sortBy", "DISTANCE_ASC"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.content").isArray());
//
//        verify(favoriteService).getFavorites(any(GetFavoritesRequest.class), eq(37.5665), eq(126.9780));
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 목록 조회 실패 - customerId 누락")
//    void getFavorites_MissingCustomerId() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/favorites")
//                .param("size", "20"))
//            .andDo(print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 목록 조회 실패 - 잘못된 페이지 크기")
//    void getFavorites_InvalidPageSize() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/favorites")
//                .param("customerId", "100")
//                .param("size", "200")) // 최대값 초과
//            .andDo(print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 삭제 성공 - ID로")
//    void deleteFavorite_Success() throws Exception {
//        // given
//        given(favoriteService.deleteFavorite(1L)).willReturn(testDeleteResponse);
//
//        // when & then
//        mockMvc.perform(delete("/v1/favorites/1"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.favoriteId").value(1L))
//            .andExpect(jsonPath("$.body.message").value("즐겨찾기가 성공적으로 삭제되었습니다."));
//
//        verify(favoriteService).deleteFavorite(1L);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 삭제 실패 - 존재하지 않는 즐겨찾기")
//    void deleteFavorite_NotFound() throws Exception {
//        // given
//        given(favoriteService.deleteFavorite(999L))
//            .willThrow(new FavoriteNotFoundException(999L));
//
//        // when & then
//        mockMvc.perform(delete("/v1/favorites/999"))
//            .andDo(print())
//            .andExpect(status().isNotFound());
//
//        verify(favoriteService).deleteFavorite(999L);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 삭제 성공 - 고객과 가게로")
//    void deleteFavoriteByCustomerAndStore_Success() throws Exception {
//        // given
//        given(favoriteService.deleteFavoriteByCustomerAndStore(100L, 200L))
//            .willReturn(testDeleteResponse);
//
//        // when & then
//        mockMvc.perform(delete("/v1/favorites/by-customer-store")
//                .param("customerId", "100")
//                .param("storeId", "200"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body.favoriteId").value(1L))
//            .andExpect(jsonPath("$.body.message").value("즐겨찾기가 성공적으로 삭제되었습니다."));
//
//        verify(favoriteService).deleteFavoriteByCustomerAndStore(100L, 200L);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 삭제 실패 - 파라미터 누락")
//    void deleteFavoriteByCustomerAndStore_MissingParameters() throws Exception {
//        // when & then
//        mockMvc.perform(delete("/v1/favorites/by-customer-store")
//                .param("customerId", "100"))
//            .andDo(print())
//            .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 여부 확인 성공 - 존재함")
//    void isFavorite_True() throws Exception {
//        // given
//        given(favoriteService.isFavorite(100L, 200L)).willReturn(true);
//
//        // when & then
//        mockMvc.perform(get("/v1/favorites/check")
//                .param("customerId", "100")
//                .param("storeId", "200"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body").value(true));
//
//        verify(favoriteService).isFavorite(100L, 200L);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 여부 확인 성공 - 존재하지 않음")
//    void isFavorite_False() throws Exception {
//        // given
//        given(favoriteService.isFavorite(100L, 999L)).willReturn(false);
//
//        // when & then
//        mockMvc.perform(get("/v1/favorites/check")
//                .param("customerId", "100")
//                .param("storeId", "999"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.success").value(true))
//            .andExpect(jsonPath("$.body").value(false));
//
//        verify(favoriteService).isFavorite(100L, 999L);
//    }
//
//    @Test
//    @DisplayName("즐겨찾기 여부 확인 실패 - 파라미터 누락")
//    void isFavorite_MissingParameters() throws Exception {
//        // when & then
//        mockMvc.perform(get("/v1/favorites/check")
//                .param("customerId", "100"))
//            .andDo(print())
//            .andExpect(status().isBadRequest());
//    }
//}