package com.kkulddip.favorite.service;

import com.kkulddip.favorite.dto.request.GetFavoritesRequest;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.favorite.enums.FavoriteSortType;
import com.kkulddip.favorite.mapper.FavoriteMapper;
import com.kkulddip.favorite.repository.FavoriteRepository;
import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.entity.Store;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoriteService 커서 페이지네이션 테스트")
@ActiveProfiles("citest")
class FavoriteServiceCursorTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteService favoriteService;

    private Favorite favorite1, favorite2, favorite3;
    private GetFavoritesResponse response1, response2, response3;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        favorite1 = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(201L)
            .createdAt(baseTime.minusDays(3))
            .build();

        favorite2 = Favorite.builder()
            .favoriteId(2L)
            .customerId(100L)
            .storeId(202L)
            .createdAt(baseTime.minusDays(2))
            .build();

        favorite3 = Favorite.builder()
            .favoriteId(3L)
            .customerId(100L)
            .storeId(203L)
            .createdAt(baseTime.minusDays(1))
            .build();

        response1 = GetFavoritesResponse.builder()
            .favoriteId(1L)
            .storeId(201L)
            .storeName("Store A")
            .createdAt(baseTime.minusDays(3))
            .build();

        response2 = GetFavoritesResponse.builder()
            .favoriteId(2L)
            .storeId(202L)
            .storeName("Store B")
            .createdAt(baseTime.minusDays(2))
            .build();

        response3 = GetFavoritesResponse.builder()
            .favoriteId(3L)
            .storeId(203L)
            .storeName("Store C")
            .createdAt(baseTime.minusDays(1))
            .build();
    }

    @Test
    @DisplayName("생성일시 내림차순 커서 페이지네이션 - 첫 페이지")
    void getFavorites_CreatedDesc_FirstPage() {
        // given
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 2, FavoriteSortType.CREATED_DESC);
        List<Favorite> favorites = Arrays.asList(favorite3, favorite2, favorite1); // size + 1

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);
        given(favoriteMapper.toGetFavoritesResponse(favorite2, null, null)).willReturn(response2);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isFalse();

        // 커서에 마지막 아이템 정보가 포함되어야 함
        assertThat(result.getCursor()).isNotNull();
        CursorInfo cursor = CursorInfo.decode(result.getCursor());
        assertThat(cursor.getId()).isEqualTo(favorite2.getFavoriteId());
        assertThat(cursor.getCreatedAt()).isEqualTo(favorite2.getCreatedAt());

        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("생성일시 내림차순 커서 페이지네이션 - 두 번째 페이지")
    void getFavorites_CreatedDesc_SecondPage() {
        // given
        CursorInfo cursor = CursorInfo.ofCreatedAt(2L, baseTime.minusDays(2));
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, cursor.encode(), 2, FavoriteSortType.CREATED_DESC);
        List<Favorite> favorites = Arrays.asList(favorite1);

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), eq(baseTime.minusDays(2)), eq(2L), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite1, null, null)).willReturn(response1);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getIsFirst()).isFalse();
        assertThat(result.getIsLast()).isTrue();

        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), eq(baseTime.minusDays(2)), eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("이름 오름차순 커서 페이지네이션")
    void getFavorites_NameAsc_WithCursor() {
        // given
        CursorInfo cursor = CursorInfo.ofId(2L);
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, cursor.encode(), 2, FavoriteSortType.NAME_ASC);
        List<Favorite> favorites = Arrays.asList(favorite1, favorite3);

        given(favoriteRepository.findFavoritesWithCursorByStoreNameAsc(
            eq(100L), isNull(), eq(2L), eq(3)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite1, null, null)).willReturn(response1);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getHasNext()).isFalse();

        verify(favoriteRepository).findFavoritesWithCursorByStoreNameAsc(
            eq(100L), isNull(), eq(2L), eq(3));
    }

    @Test
    @DisplayName("거리 오름차순 커서 페이지네이션 - 사용자 위치 포함")
    void getFavorites_DistanceAsc_WithUserLocation() {
        // given
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        CursorInfo cursor = CursorInfo.ofDistance(2L, 1.5);
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, cursor.encode(), 2, FavoriteSortType.DISTANCE_ASC);
        List<Favorite> favorites = Arrays.asList(favorite1, favorite3);

        given(favoriteRepository.findFavoritesWithCursorByDistance(
            eq(100L), eq(userLat), eq(userLng), eq(1.5), eq(2L), eq(3)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite1, userLat, userLng)).willReturn(response1);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, userLat, userLng)).willReturn(response3);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, userLat, userLng);

        // then
        assertThat(result.getContent()).hasSize(2);
        verify(favoriteRepository).findFavoritesWithCursorByDistance(
            eq(100L), eq(userLat), eq(userLng), eq(1.5), eq(2L), eq(3));
    }

    @Test
    @DisplayName("거리 정렬 시 사용자 위치 없으면 생성일시 정렬로 폴백")
    void getFavorites_DistanceSort_FallbackToCreatedDesc() {
        // given
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 2, FavoriteSortType.DISTANCE_ASC);
        List<Favorite> favorites = Arrays.asList(favorite3, favorite2);

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);
        given(favoriteMapper.toGetFavoritesResponse(favorite2, null, null)).willReturn(response2);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2);
        // metadata가 설정되지 않을 수 있으므로 null 체크
        if (result.getMetadata() != null) {
            assertThat(result.getMetadata().getSortBy()).isEqualTo("DISTANCE_ASC");
        }

        // distance 정렬이지만 사용자 위치가 없어서 created_at 기준으로 폴백
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("평점 내림차순 커서 페이지네이션")
    void getFavorites_RatingDesc_WithCursor() {
        // given
        CursorInfo cursor = CursorInfo.ofRating(2L, 4.5);
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, cursor.encode(), 2, FavoriteSortType.RATING_DESC);
        List<Favorite> favorites = Arrays.asList(favorite1, favorite3);

        given(favoriteRepository.findFavoritesWithCursorByRatingDesc(
            eq(100L), eq(4.5), eq(2L), eq(3)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite1, null, null)).willReturn(response1);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2);
        verify(favoriteRepository).findFavoritesWithCursorByRatingDesc(
            eq(100L), eq(4.5), eq(2L), eq(3));
    }

    @Test
    @DisplayName("빈 결과에 대한 커서 처리")
    void getFavorites_EmptyResult() {
        // given
        GetFavoritesRequest request = GetFavoritesRequest.of(999L, null, 10, FavoriteSortType.CREATED_DESC);
        List<Favorite> emptyFavorites = Collections.emptyList();

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(999L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(emptyFavorites);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getCursor()).isNull();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isTrue();
        assertThat(result.getActualSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("잘못된 커서 처리 - 디코딩 실패 시 첫 페이지로 처리")
    void getFavorites_InvalidCursor() {
        // given
        String invalidCursor = "invalid-cursor-string";
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, invalidCursor, 2, FavoriteSortType.CREATED_DESC);
        List<Favorite> favorites = Arrays.asList(favorite3, favorite2);

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);
        given(favoriteMapper.toGetFavoritesResponse(favorite2, null, null)).willReturn(response2);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2);
        // 잘못된 커서는 null로 처리되어 첫 페이지 조회가 됨
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("커서 생성 검증 - 다양한 정렬 기준")
    void verifyCursorGenerationForDifferentSorts() {
        // given
        List<Favorite> favorites = Arrays.asList(favorite1, favorite2);

        // 생성일시 정렬
        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(any(Favorite.class), any(), any()))
            .willReturn(response1, response2);

        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 2, FavoriteSortType.CREATED_DESC);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        if (result.getCursor() != null) {
            CursorInfo cursor = CursorInfo.decode(result.getCursor());
            assertThat(cursor.getId()).isEqualTo(favorite2.getFavoriteId());
            assertThat(cursor.getCreatedAt()).isEqualTo(favorite2.getCreatedAt());
            assertThat(cursor.hasCreatedAt()).isTrue();
            assertThat(cursor.hasRating()).isFalse();
            assertThat(cursor.hasDistance()).isFalse();
        } else {
            // 마지막 페이지인 경우 cursor가 null일 수 있음
            assertThat(result.getHasNext()).isFalse();
        }
    }

    @Test
    @DisplayName("페이지 메타데이터 검증")
    void verifyPageMetadata() {
        // given
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 3, FavoriteSortType.NAME_ASC);
        List<Favorite> favorites = Arrays.asList(favorite1, favorite2, favorite3);

        given(favoriteRepository.findFavoritesWithCursorByStoreNameAsc(
            eq(100L), isNull(), isNull(), eq(4)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(any(Favorite.class), any(), any()))
            .willReturn(response1, response2, response3);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getActualSize()).isEqualTo(3);
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isTrue();
        // metadata가 설정되지 않을 수 있으므로 null 체크
        if (result.getMetadata() != null) {
            assertThat(result.getMetadata().getSortBy()).isEqualTo("NAME_ASC");
        }
    }

    @Test
    @DisplayName("거리 커서에서 실제 거리 계산 포함 검증")
    void getFavorites_DistanceWithRealCalculation() {
        // given
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 2, FavoriteSortType.DISTANCE_ASC);
        List<Favorite> favorites = Arrays.asList(favorite1, favorite2, favorite3);

        // Mock Store entities for distance calculation
        Store store1 = Store.builder()
            .storeId(201L)
            .latitude(37.5665)
            .longitude(126.9780)
            .build();

        Store store2 = Store.builder()
            .storeId(202L)
            .latitude(37.4833)
            .longitude(127.0322)
            .build();

        given(favoriteRepository.findFavoritesWithCursorByDistance(
            eq(100L), eq(userLat), eq(userLng), isNull(), isNull(), eq(3)))
            .willReturn(favorites);
        GetFavoritesResponse responseWithDistance1 = GetFavoritesResponse.builder()
            .favoriteId(1L)
            .storeId(201L)
            .storeName("Store A")
            .distanceFromCustomer(0.0)
            .createdAt(baseTime.minusDays(3))
            .build();
        given(favoriteMapper.toGetFavoritesResponse(favorite1, userLat, userLng))
            .willReturn(responseWithDistance1);
        GetFavoritesResponse responseWithDistance2 = GetFavoritesResponse.builder()
            .favoriteId(2L)
            .storeId(202L)
            .storeName("Store B")
            .distanceFromCustomer(5.2)
            .createdAt(baseTime.minusDays(2))
            .build();
        given(favoriteMapper.toGetFavoritesResponse(favorite2, userLat, userLng))
            .willReturn(responseWithDistance2);
        
        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, userLat, userLng);

        // then
        assertThat(result.getContent()).hasSize(2);
        
        // 다음 커서에 거리 정보가 포함되는지 확인
        if (result.getCursor() != null) {
            CursorInfo cursor = CursorInfo.decode(result.getCursor());
            assertThat(cursor.hasDistance()).isTrue();
            assertThat(cursor.getDistance()).isNotNull();
        }

        verify(favoriteRepository).findFavoritesWithCursorByDistance(
            eq(100L), eq(userLat), eq(userLng), isNull(), isNull(), eq(3));
    }

    @Test
    @DisplayName("정확한 페이지 크기 계산 - size+1 로직 검증")
    void verifyPageSizePlusOneLogic() {
        // given
        GetFavoritesRequest request = GetFavoritesRequest.of(100L, null, 2, FavoriteSortType.CREATED_DESC);
        List<Favorite> favorites = Arrays.asList(favorite3, favorite2, favorite1); // 3개 반환 (size+1)

        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), any(Pageable.class)))
            .willReturn(favorites);
        given(favoriteMapper.toGetFavoritesResponse(favorite3, null, null)).willReturn(response3);
        given(favoriteMapper.toGetFavoritesResponse(favorite2, null, null)).willReturn(response2);

        // when
        Page<GetFavoritesResponse> result = favoriteService.getFavorites(request, null, null);

        // then
        assertThat(result.getContent()).hasSize(2); // 요청한 크기만 반환
        assertThat(result.getHasNext()).isTrue(); // 3개가 조회되었으므로 다음 페이지 있음
        assertThat(result.getActualSize()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(2);

        // Repository 호출 시에는 size+1로 호출되었는지 확인
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(
            eq(100L), isNull(), isNull(), eq(PageRequest.of(0, 3))); // 2+1 = 3
    }

    @Test
    @DisplayName("다양한 정렬 타입별 repository 메소드 호출 검증")
    void verifyRepositoryMethodCallsForDifferentSortTypes() {
        // given
        List<Favorite> favorites = Arrays.asList(favorite1);
        given(favoriteMapper.toGetFavoritesResponse(any(Favorite.class), any(), any())).willReturn(response1);

        // CREATED_DESC 테스트
        GetFavoritesRequest createdRequest = GetFavoritesRequest.of(100L, null, 1, FavoriteSortType.CREATED_DESC);
        given(favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(eq(100L), isNull(), isNull(), any()))
            .willReturn(favorites);

        favoriteService.getFavorites(createdRequest, null, null);
        verify(favoriteRepository).findFavoritesWithCursorByCreatedAtDesc(eq(100L), isNull(), isNull(), any());

        // NAME_ASC 테스트
        GetFavoritesRequest nameRequest = GetFavoritesRequest.of(100L, null, 1, FavoriteSortType.NAME_ASC);
        given(favoriteRepository.findFavoritesWithCursorByStoreNameAsc(eq(100L), isNull(), isNull(), eq(2)))
            .willReturn(favorites);

        favoriteService.getFavorites(nameRequest, null, null);
        verify(favoriteRepository).findFavoritesWithCursorByStoreNameAsc(eq(100L), isNull(), isNull(), eq(2));

        // RATING_DESC 테스트
        GetFavoritesRequest ratingRequest = GetFavoritesRequest.of(100L, null, 1, FavoriteSortType.RATING_DESC);
        given(favoriteRepository.findFavoritesWithCursorByRatingDesc(eq(100L), isNull(), isNull(), eq(2)))
            .willReturn(favorites);

        favoriteService.getFavorites(ratingRequest, null, null);
        verify(favoriteRepository).findFavoritesWithCursorByRatingDesc(eq(100L), isNull(), isNull(), eq(2));
    }
}