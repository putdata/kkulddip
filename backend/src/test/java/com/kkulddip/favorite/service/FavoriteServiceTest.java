package com.kkulddip.favorite.service;

import com.kkulddip.favorite.dto.request.AddFavoriteRequest;
import com.kkulddip.favorite.dto.request.GetFavoritesRequest;
import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.DeleteFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.favorite.enums.FavoriteSortType;
import com.kkulddip.favorite.exception.FavoriteAlreadyExistsException;
import com.kkulddip.favorite.exception.FavoriteNotFoundException;
import com.kkulddip.favorite.mapper.FavoriteMapper;
import com.kkulddip.favorite.repository.FavoriteRepository;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoriteService 테스트")
@ActiveProfiles("citest")
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteService favoriteService;

    private Favorite testFavorite;
    private AddFavoriteRequest testAddRequest;
    private GetFavoritesRequest testGetRequest;
    private AddFavoriteResponse testAddResponse;
    private GetFavoritesResponse testGetResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testFavorite = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        testAddRequest = new AddFavoriteRequest(100L, 200L);

        testGetRequest = GetFavoritesRequest.of(100L);

        testAddResponse = AddFavoriteResponse.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        testGetResponse = GetFavoritesResponse.builder()
            .favoriteId(1L)
            .storeId(200L)
            .storeName("테스트 가게")
            .storeProfileImage("store-image.jpg")
            .representationDdipboxName("테스트 띱박스")
            .representationDdipboxProfileImage(null) // DdipBox에 프로필 이미지 필드가 없음
            .reviewRating(4.5)
            .distanceFromCustomer(1.2)
            .category("한식")
            .createdAt(now)
            .build();
    }

    @Test
    @DisplayName("즐겨찾기 추가 성공")
    void addFavorite_Success() {
        // given
        given(storeRepository.existsActiveStore(testAddRequest.storeId())).willReturn(true);
        given(favoriteRepository.existsByCustomerIdAndStoreId(
            testAddRequest.customerId(), testAddRequest.storeId())).willReturn(false);
        given(favoriteRepository.save(any(Favorite.class))).willReturn(testFavorite);
        given(favoriteMapper.toAddFavoriteResponse(testFavorite)).willReturn(testAddResponse);

        // when
        AddFavoriteResponse result = favoriteService.addFavorite(testAddRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(1L);
        assertThat(result.customerId()).isEqualTo(100L);
        assertThat(result.storeId()).isEqualTo(200L);
        
        verify(storeRepository).existsActiveStore(testAddRequest.storeId());
        verify(favoriteRepository).existsByCustomerIdAndStoreId(
            testAddRequest.customerId(), testAddRequest.storeId());
        verify(favoriteRepository).save(any(Favorite.class));
        verify(favoriteMapper).toAddFavoriteResponse(testFavorite);
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 존재하지 않는 가게")
    void addFavorite_StoreNotFound() {
        // given
        given(storeRepository.existsActiveStore(testAddRequest.storeId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> favoriteService.addFavorite(testAddRequest))
            .isInstanceOf(StoreNotFoundException.class);

        verify(storeRepository).existsActiveStore(testAddRequest.storeId());
        verify(favoriteRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("즐겨찾기 추가 실패 - 이미 존재하는 즐겨찾기")
    void addFavorite_AlreadyExists() {
        // given
        given(storeRepository.existsActiveStore(testAddRequest.storeId())).willReturn(true);
        given(favoriteRepository.existsByCustomerIdAndStoreId(
            testAddRequest.customerId(), testAddRequest.storeId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> favoriteService.addFavorite(testAddRequest))
            .isInstanceOf(FavoriteAlreadyExistsException.class);

        verify(storeRepository).existsActiveStore(testAddRequest.storeId());
        verify(favoriteRepository).existsByCustomerIdAndStoreId(
            testAddRequest.customerId(), testAddRequest.storeId());
        verify(favoriteRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 성공")
    void getFavorites_Success() {
        // given
        List<Favorite> favorites = Arrays.asList(testFavorite);
        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(testGetRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class))).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, null, null))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(testGetRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).favoriteId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).storeName()).isEqualTo("테스트 가게");
        
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(testGetRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class));
        verify(favoriteMapper).toGetFavoritesResponse(testFavorite, null, null);
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 빈 결과")
    void getFavorites_EmptyResult() {
        // given
        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(testGetRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class))).willReturn(Collections.emptyList());

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(testGetRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getHasNext()).isFalse();
        
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(testGetRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class));
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 이름순 정렬")
    void getFavorites_SortByName() {
        // given
        GetFavoritesRequest nameRequest = GetFavoritesRequest.of(100L, null, 20, FavoriteSortType.NAME_ASC);
        List<Favorite> favorites = Arrays.asList(testFavorite);
        given(favoriteRepository.findFavoritesWithCursorByStoreNameAsc(
            eq(nameRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt())).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, null, null))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(nameRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(favoriteRepository).findFavoritesWithCursorByStoreNameAsc(
            eq(nameRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt());
    }

    @Test
    @DisplayName("즐겨찾기 삭제 성공")
    void deleteFavorite_Success() {
        // given
        Long favoriteId = 1L;
        given(favoriteRepository.findById(favoriteId)).willReturn(Optional.of(testFavorite));

        // when
        DeleteFavoriteResponse result = favoriteService.deleteFavorite(favoriteId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(favoriteId);
        assertThat(result.message()).isEqualTo("즐겨찾기가 성공적으로 삭제되었습니다.");
        
        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteRepository).delete(testFavorite);
    }

    @Test
    @DisplayName("즐겨찾기 삭제 실패 - 존재하지 않는 즐겨찾기")
    void deleteFavorite_NotFound() {
        // given
        Long favoriteId = 999L;
        given(favoriteRepository.findById(favoriteId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteService.deleteFavorite(favoriteId))
            .isInstanceOf(FavoriteNotFoundException.class);

        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("고객과 가게로 즐겨찾기 삭제 성공")
    void deleteFavoriteByCustomerAndStore_Success() {
        // given
        Long customerId = 100L;
        Long storeId = 200L;
        given(favoriteRepository.findByCustomerIdAndStoreId(customerId, storeId))
            .willReturn(Optional.of(testFavorite));

        // when
        DeleteFavoriteResponse result = favoriteService.deleteFavoriteByCustomerAndStore(customerId, storeId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(testFavorite.getFavoriteId());
        assertThat(result.message()).isEqualTo("즐겨찾기가 성공적으로 삭제되었습니다.");
        
        verify(favoriteRepository).findByCustomerIdAndStoreId(customerId, storeId);
        verify(favoriteRepository).delete(testFavorite);
    }

    @Test
    @DisplayName("고객과 가게로 즐겨찾기 삭제 실패 - 존재하지 않는 즐겨찾기")
    void deleteFavoriteByCustomerAndStore_NotFound() {
        // given
        Long customerId = 100L;
        Long storeId = 999L;
        given(favoriteRepository.findByCustomerIdAndStoreId(customerId, storeId))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteService.deleteFavoriteByCustomerAndStore(customerId, storeId))
            .isInstanceOf(FavoriteNotFoundException.class);

        verify(favoriteRepository).findByCustomerIdAndStoreId(customerId, storeId);
        verify(favoriteRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("즐겨찾기 여부 확인 - 존재함")
    void isFavorite_True() {
        // given
        Long customerId = 100L;
        Long storeId = 200L;
        given(favoriteRepository.existsByCustomerIdAndStoreId(customerId, storeId)).willReturn(true);

        // when
        boolean result = favoriteService.isFavorite(customerId, storeId);

        // then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByCustomerIdAndStoreId(customerId, storeId);
    }

    @Test
    @DisplayName("즐겨찾기 여부 확인 - 존재하지 않음")
    void isFavorite_False() {
        // given
        Long customerId = 100L;
        Long storeId = 999L;
        given(favoriteRepository.existsByCustomerIdAndStoreId(customerId, storeId)).willReturn(false);

        // when
        boolean result = favoriteService.isFavorite(customerId, storeId);

        // then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByCustomerIdAndStoreId(customerId, storeId);
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 평점순 정렬")
    void getFavorites_SortByRating() {
        // given
        GetFavoritesRequest ratingRequest = GetFavoritesRequest.of(100L, null, 20, FavoriteSortType.RATING_DESC);
        List<Favorite> favorites = Arrays.asList(testFavorite);
        given(favoriteRepository.findFavoritesWithCursorByRatingDesc(
            eq(ratingRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt())).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, null, null))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(ratingRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(favoriteRepository).findFavoritesWithCursorByRatingDesc(
            eq(ratingRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt());
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 거리순 정렬")
    void getFavorites_SortByDistance() {
        // given
        GetFavoritesRequest distanceRequest = GetFavoritesRequest.of(100L, null, 20, FavoriteSortType.DISTANCE_ASC);
        List<Favorite> favorites = Arrays.asList(testFavorite);
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        
        given(favoriteRepository.findFavoritesWithCursorByDistance(
            eq(distanceRequest.customerId()), 
            eq(userLat),
            eq(userLng),
            isNull(), 
            isNull(), 
            anyInt())).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, userLat, userLng))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(distanceRequest, userLat, userLng);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(favoriteRepository).findFavoritesWithCursorByDistance(
            eq(distanceRequest.customerId()), 
            eq(userLat),
            eq(userLng),
            isNull(), 
            isNull(), 
            anyInt());
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 거리순 정렬 (사용자 위치 없음)")
    void getFavorites_SortByDistanceWithoutUserLocation() {
        // given
        GetFavoritesRequest distanceRequest = GetFavoritesRequest.of(100L, null, 20, FavoriteSortType.DISTANCE_ASC);
        List<Favorite> favorites = Arrays.asList(testFavorite);
        
        // 사용자 위치가 없으면 기본 정렬(CREATED_DESC)로 fallback
        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(distanceRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class))).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, null, null))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(distanceRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        // 거리순 조회가 아닌 기본 정렬로 조회되었는지 확인
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(distanceRequest.customerId()), 
            isNull(), 
            isNull(), 
            any(Pageable.class));
    }

    @Test
    @DisplayName("즐겨찾기 목록 조회 - 카테고리순 정렬")
    void getFavorites_SortByCategory() {
        // given
        GetFavoritesRequest categoryRequest = GetFavoritesRequest.of(100L, null, 20, FavoriteSortType.CATEGORY);
        List<Favorite> favorites = Arrays.asList(testFavorite);
        given(favoriteRepository.findFavoritesWithCursorByCategory(
            eq(categoryRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt())).willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(testFavorite, null, null))
            .willReturn(testGetResponse);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(categoryRequest, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(favoriteRepository).findFavoritesWithCursorByCategory(
            eq(categoryRequest.customerId()), 
            isNull(), 
            isNull(), 
            anyInt());
    }
}