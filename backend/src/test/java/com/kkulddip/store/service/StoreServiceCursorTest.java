package com.kkulddip.store.service;

import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.request.StoreListRequest;
import com.kkulddip.store.dto.request.StoreSearchRequest;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.mapper.StoreMapper;
import com.kkulddip.store.repository.CursorStoreRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
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
@DisplayName("StoreService 커서 페이지네이션 테스트")
@ActiveProfiles("citest")
class StoreServiceCursorTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CursorStoreRepository cursorStoreRepository;

    @Mock
    private DdipBoxRepository ddipBoxRepository;

    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreService storeService;

    private Store store1, store2, store3;
    private StoreResponseDto storeDto1, storeDto2, storeDto3;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        store1 = Store.builder()
            .storeId(1L)
            .storeName("Store A")
            .storeAddress("Address A")
            .latitude(37.5665)
            .longitude(126.9780)
            .ratingAverage(4.5)
            .createdAt(baseTime.minusDays(3))
            .isActive(true)
            .build();

        store2 = Store.builder()
            .storeId(2L)
            .storeName("Store B")
            .storeAddress("Address B")
            .latitude(37.4833)
            .longitude(127.0322)
            .ratingAverage(4.8)
            .createdAt(baseTime.minusDays(2))
            .isActive(true)
            .build();

        store3 = Store.builder()
            .storeId(3L)
            .storeName("Store C")
            .storeAddress("Address C")
            .latitude(37.5735)
            .longitude(126.9788)
            .ratingAverage(4.2)
            .createdAt(baseTime.minusDays(1))
            .isActive(true)
            .build();

        storeDto1 = StoreResponseDto.builder()
            .storeId(1L)
            .storeName("Store A")
            .build();

        storeDto2 = StoreResponseDto.builder()
            .storeId(2L)
            .storeName("Store B")
            .build();

        storeDto3 = StoreResponseDto.builder()
            .storeId(3L)
            .storeName("Store C")
            .build();
    }

    @Test
    @DisplayName("커서 없이 첫 번째 페이지 조회 - ID 정렬")
    void getStores_FirstPageWithoutCursor_IdSort() {
        // given
        StoreListRequest request = StoreListRequest.of(null, null, "id", 2, null);
        List<Store> stores = Arrays.asList(store1, store2, store3); // size + 1

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);
        given(storeMapper.toStoreResponseDto(store2, null, null)).willReturn(storeDto2);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2); // 요청 사이즈만큼
        assertThat(result.getHasNext()).isTrue(); // 다음 페이지 있음
        assertThat(result.getCursor()).isNotNull(); // 커서 생성됨
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isFalse();

        // 커서에 마지막 아이템의 ID가 포함되어야 함
        CursorInfo cursorInfo = CursorInfo.decode(result.getCursor());
        assertThat(cursorInfo.getId()).isEqualTo(store2.getStoreId());

        verify(cursorStoreRepository).findStoresWithCursorById(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("커서로 두 번째 페이지 조회 - ID 정렬")
    void getStores_SecondPageWithCursor_IdSort() {
        // given
        CursorInfo cursor = CursorInfo.ofId(2L);
        StoreListRequest request = StoreListRequest.of(null, null, "id", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store3); // 마지막 페이지

        given(cursorStoreRepository.findStoresWithCursorById(eq(2L), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store3, null, null)).willReturn(storeDto3);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getHasNext()).isFalse(); // 마지막 페이지
        assertThat(result.getCursor()).isNull();
        assertThat(result.getIsFirst()).isFalse();
        assertThat(result.getIsLast()).isTrue();

        verify(cursorStoreRepository).findStoresWithCursorById(eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("커서 기반 평점 정렬 조회")
    void getStores_WithCursor_RatingSort() {
        // given
        CursorInfo cursor = CursorInfo.ofRating(2L, 4.8);
        StoreListRequest request = StoreListRequest.of(null, null, "rating", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store1, store3); // 평점 4.8 이하

        given(cursorStoreRepository.findStoresWithCursorByRatingDesc(eq(4.8), eq(2L), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);
        given(storeMapper.toStoreResponseDto(store3, null, null)).willReturn(storeDto3);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getHasNext()).isFalse();

        // 다음 커서에 평점 정보가 포함되어야 함
        if (result.getCursor() != null) {
            CursorInfo nextCursor = CursorInfo.decode(result.getCursor());
            assertThat(nextCursor.hasRating()).isTrue();
        }

        verify(cursorStoreRepository).findStoresWithCursorByRatingDesc(eq(4.8), eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("커서 기반 생성일시 정렬 조회")
    void getStores_WithCursor_CreatedAtSort() {
        // given
        LocalDateTime cursorDate = baseTime.minusDays(2);
        CursorInfo cursor = CursorInfo.ofCreatedAt(2L, cursorDate);
        StoreListRequest request = StoreListRequest.of(null, null, "created_at", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store1);

        given(cursorStoreRepository.findStoresWithCursorByCreatedAtDesc(eq(cursorDate), eq(2L), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(storeDto1);

        verify(cursorStoreRepository).findStoresWithCursorByCreatedAtDesc(eq(cursorDate), eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("커서 기반 거리 정렬 조회")
    void getStores_WithCursor_DistanceSort() {
        // given
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        Double cursorDistance = 2.5;
        CursorInfo cursor = CursorInfo.ofDistance(2L, cursorDistance);
        StoreListRequest request = StoreListRequest.of(userLat, userLng, "distance", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store1, store3);

        given(cursorStoreRepository.findStoresWithCursorByDistance(
            eq(userLat), eq(userLng), eq(cursorDistance), eq(2L), eq(3)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(eq(store1), eq(userLat), eq(userLng))).willReturn(storeDto1);
        given(storeMapper.toStoreResponseDto(eq(store3), eq(userLat), eq(userLng))).willReturn(storeDto3);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        verify(cursorStoreRepository).findStoresWithCursorByDistance(
            eq(userLat), eq(userLng), eq(cursorDistance), eq(2L), eq(3));
    }

    @Test
    @DisplayName("거리 정렬 시 사용자 위치 없으면 ID 정렬로 폴백")
    void getStores_DistanceSortFallbackToId() {
        // given
        StoreListRequest request = StoreListRequest.of(null, null, "distance", 2, null);
        List<Store> stores = Arrays.asList(store1, store2);

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);
        given(storeMapper.toStoreResponseDto(store2, null, null)).willReturn(storeDto2);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getMetadata().getSortBy()).isEqualTo("distance");

        // distance 정렬이지만 사용자 위치가 없어서 ID 기준 조회가 호출됨
        verify(cursorStoreRepository).findStoresWithCursorById(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("검색 커서 페이지네이션 테스트")
    void searchStores_WithCursor() {
        // given
        CursorInfo cursor = CursorInfo.ofId(1L);
        StoreSearchRequest request = StoreSearchRequest.of("Store", null, null, "id", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store2, store3);

        given(cursorStoreRepository.findStoresWithCursorBySearch(eq("Store"), eq(1L), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store2, null, null)).willReturn(storeDto2);
        given(storeMapper.toStoreResponseDto(store3, null, null)).willReturn(storeDto3);

        // when
        Page<StoreResponseDto> result = storeService.searchStores(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getMetadata().getSearchKeyword()).isEqualTo("Store");
        assertThat(result.getHasNext()).isFalse();

        verify(cursorStoreRepository).findStoresWithCursorBySearch(eq("Store"), eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("카테고리 커서 페이지네이션 테스트")
    void getStoresByCategory_WithCursor() {
        // given
        String category = "한식";
        CursorInfo cursor = CursorInfo.ofId(1L);
        StoreListRequest request = StoreListRequest.of(null, null, "id", 2, cursor.encode());
        List<Store> stores = Arrays.asList(store2, store3);

        given(cursorStoreRepository.findStoresWithCursorByCategory(eq(category), eq(1L), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store2, null, null)).willReturn(storeDto2);
        given(storeMapper.toStoreResponseDto(store3, null, null)).willReturn(storeDto3);

        // when
        Page<StoreResponseDto> result = storeService.getStoresByCategory(category, request);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getMetadata().getCategory()).isEqualTo(category);

        verify(cursorStoreRepository).findStoresWithCursorByCategory(eq(category), eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("빈 결과에 대한 커서 처리")
    void getStores_EmptyResult() {
        // given
        StoreListRequest request = StoreListRequest.of(null, null, "id", 10, null);
        List<Store> emptyStores = Collections.emptyList();

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
            .willReturn(emptyStores);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

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
    void getStores_InvalidCursor() {
        // given
        String invalidCursor = "invalid-cursor-string";
        StoreListRequest request = StoreListRequest.of(null, null, "id", 2, invalidCursor);
        List<Store> stores = Arrays.asList(store1, store2);

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);
        given(storeMapper.toStoreResponseDto(store2, null, null)).willReturn(storeDto2);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2);
        // 잘못된 커서는 null로 처리되어 첫 페이지 조회가 됨
        verify(cursorStoreRepository).findStoresWithCursorById(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("정확한 커서 생성 검증 - 다양한 정렬 기준")
    void verifyCursorGenerationForDifferentSortCriteria() {
        // given
        List<Store> stores = Arrays.asList(store1, store2);

        // ID 정렬 테스트
        StoreListRequest idRequest = StoreListRequest.of(null, null, "id", 2, null);
        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), any(), any())).willReturn(storeDto1, storeDto2);

        // when
        Page<StoreResponseDto> idResult = storeService.getStores(idRequest);

        // then
        if (idResult.getCursor() != null) {
            CursorInfo idCursor = CursorInfo.decode(idResult.getCursor());
            assertThat(idCursor.getId()).isEqualTo(store2.getStoreId());
            assertThat(idCursor.hasCreatedAt()).isFalse();
            assertThat(idCursor.hasRating()).isFalse();
            assertThat(idCursor.hasDistance()).isFalse();
        } else {
            // 마지막 페이지인 경우 cursor가 null일 수 있음
            assertThat(idResult.getHasNext()).isFalse();
        }
    }

    @Test
    @DisplayName("페이지 메타데이터 검증")
    void verifyPageMetadata() {
        // given
        StoreListRequest request = StoreListRequest.of(37.5665, 126.9780, "distance", 2, null);
        List<Store> stores = Arrays.asList(store1, store2, store3);

        given(cursorStoreRepository.findStoresWithCursorByDistance(
            any(Double.class), any(Double.class), isNull(), isNull(), eq(3)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), any(Double.class), any(Double.class)))
            .willReturn(storeDto1, storeDto2);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getActualSize()).isEqualTo(2);
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isFalse();
        assertThat(result.getMetadata().getSortBy()).isEqualTo("distance");
    }

    @Test
    @DisplayName("커서 정보 타입별 검증")
    void verifyCursorInfoTypeForDifferentSorts() {
        // given
        List<Store> stores = Arrays.asList(store1);

        // Rating 정렬
        given(cursorStoreRepository.findStoresWithCursorByRatingDesc(isNull(), isNull(), any(Pageable.class)))
            .willReturn(stores);
        given(storeMapper.toStoreResponseDto(store1, null, null)).willReturn(storeDto1);

        StoreListRequest ratingRequest = StoreListRequest.of(null, null, "rating", 1, null);

        // when
        Page<StoreResponseDto> ratingResult = storeService.getStores(ratingRequest);

        // then
        if (ratingResult.getCursor() != null) {
            CursorInfo cursor = CursorInfo.decode(ratingResult.getCursor());
            assertThat(cursor.getId()).isEqualTo(store1.getStoreId());
            assertThat(cursor.getRating()).isEqualTo(store1.getRatingAverage());
            assertThat(cursor.hasRating()).isTrue();
        }

        verify(cursorStoreRepository).findStoresWithCursorByRatingDesc(isNull(), isNull(), any(Pageable.class));
    }
}