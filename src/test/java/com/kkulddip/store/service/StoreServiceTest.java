package com.kkulddip.store.service;

import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.request.StoreListRequest;
import com.kkulddip.store.dto.request.StoreSearchRequest;
import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.exception.StoreValidationException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService 테스트")
class StoreServiceTest {

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

    private Store testStore;
    private StoreResponseDto testStoreResponseDto;
    private StoreDetailDto testStoreDetailDto;
    private DdipBox testDdipBox;
    private DdipBoxCardViewDto testDdipBoxCardViewDto;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
                .storeId(1L)
                .storeName("테스트 마트")
                .storeAddress("서울시 강남구")
                .isActive(true)
                .build();

        testStoreResponseDto = StoreResponseDto.builder()
                .storeId(1L)
                .storeName("테스트 마트")
                .storeAddress("서울시 강남구")
                .build();

        testStoreDetailDto = StoreDetailDto.builder()
                .storeId(1L)
                .storeName("테스트 마트")
                .storeAddress("서울시 강남구")
                .build();

        testDdipBox = DdipBox.builder()
                .ddipboxId(1L)
                .ddipboxName("테스트 띱박스")
                .isActive(true)
                .build();

        testDdipBoxCardViewDto = DdipBoxCardViewDto.builder()
                .ddipboxId(1L)
                .ddipboxName("테스트 띱박스")
                .build();
    }

    @Test
    @DisplayName("가게 목록을 조회한다")
    void getStores() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("id")
                .size(10)
                .cursor(null)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testStoreResponseDto);
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isTrue();

        verify(cursorStoreRepository).findStoresWithCursorById(isNull(), any(Pageable.class));
        verify(storeMapper).toStoreResponseDto(testStore, null, null);
    }

    @Test
    @DisplayName("가게 목록 조회 시 다음 페이지가 있으면 hasNext가 true이다")
    void getStores_HasNextPage() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("id")
                .size(2)
                .build();

        // 3개의 store를 반환하도록 설정 (size + 1)
        Store store1 = Store.builder().storeId(1L).build();
        Store store2 = Store.builder().storeId(2L).build();
        Store store3 = Store.builder().storeId(3L).build();
        List<Store> stores = Arrays.asList(store1, store2, store3);

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).hasSize(2); // 요청한 size만큼만 반환
        assertThat(result.getHasNext()).isTrue(); // 다음 페이지 있음
        assertThat(result.getIsLast()).isFalse();
        assertThat(result.getCursor()).isNotNull(); // 커서 존재
    }

    @Test
    @DisplayName("가게 검색을 수행한다")
    void searchStores() {
        // given
        StoreSearchRequest request = StoreSearchRequest.builder()
                .keyword("마트")
                .size(10)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorBySearch(eq("마트"), isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.searchStores(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getMetadata().getSearchKeyword()).isEqualTo("마트");

        verify(cursorStoreRepository).findStoresWithCursorBySearch("마트", null, PageRequest.of(0, 11));
    }

    @Test
    @DisplayName("카테고리별 가게를 조회한다")
    void getStoresByCategory() {
        // given
        String category = "유기농";
        StoreListRequest request = StoreListRequest.builder()
                .size(10)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorByCategory(eq(category), isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStoresByCategory(category, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getMetadata().getCategory()).isEqualTo(category);

        verify(cursorStoreRepository).findStoresWithCursorByCategory(category, null, PageRequest.of(0, 11));
    }

    @Test
    @DisplayName("가게 상세 정보를 조회한다")
    void getStoreDetail() {
        // given
        Long storeId = 1L;
        given(storeRepository.findActiveStoreWithDdipBoxes(storeId))
                .willReturn(Optional.of(testStore));
        given(storeMapper.toStoreDetailDto(testStore))
                .willReturn(testStoreDetailDto);

        // when
        StoreDetailDto result = storeService.getStoreDetail(storeId);

        // then
        assertThat(result).isEqualTo(testStoreDetailDto);

        verify(storeRepository).findActiveStoreWithDdipBoxes(storeId);
        verify(storeMapper).toStoreDetailDto(testStore);
    }

    @Test
    @DisplayName("존재하지 않는 가게 상세 조회 시 예외가 발생한다")
    void getStoreDetail_NotFound() {
        // given
        Long storeId = 999L;
        given(storeRepository.findActiveStoreWithDdipBoxes(storeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStoreDetail(storeId))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    @DisplayName("유효하지 않은 가게 ID로 상세 조회 시 예외가 발생한다")
    void getStoreDetail_InvalidId() {
        // when & then
        assertThatThrownBy(() -> storeService.getStoreDetail(null))
                .isInstanceOf(StoreValidationException.class);

        assertThatThrownBy(() -> storeService.getStoreDetail(0L))
                .isInstanceOf(StoreValidationException.class);

        assertThatThrownBy(() -> storeService.getStoreDetail(-1L))
                .isInstanceOf(StoreValidationException.class);
    }

    @Test
    @DisplayName("가게의 띱박스 목록을 조회한다")
    void getStoreDdipBoxes() {
        // given
        Long storeId = 1L;
        List<DdipBox> ddipBoxes = Arrays.asList(testDdipBox);

        given(storeRepository.existsActiveStore(storeId)).willReturn(true);
        given(ddipBoxRepository.findActiveByStoreIdWithItems(storeId))
                .willReturn(ddipBoxes);
        given(storeMapper.toDdipBoxCardViewDto(testDdipBox))
                .willReturn(testDdipBoxCardViewDto);

        // when
        List<DdipBoxCardViewDto> result = storeService.getStoreDdipBoxes(storeId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDdipBoxCardViewDto);

        verify(storeRepository).existsActiveStore(storeId);
        verify(ddipBoxRepository).findActiveByStoreIdWithItems(storeId);
        verify(storeMapper).toDdipBoxCardViewDto(testDdipBox);
    }

    @Test
    @DisplayName("존재하지 않는 가게의 띱박스 조회 시 예외가 발생한다")
    void getStoreDdipBoxes_StoreNotFound() {
        // given
        Long storeId = 999L;
        given(storeRepository.existsActiveStore(storeId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> storeService.getStoreDdipBoxes(storeId))
                .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    @DisplayName("유효하지 않은 가게 ID로 띱박스 조회 시 예외가 발생한다")
    void getStoreDdipBoxes_InvalidId() {
        // when & then
        assertThatThrownBy(() -> storeService.getStoreDdipBoxes(null))
                .isInstanceOf(StoreValidationException.class);

        assertThatThrownBy(() -> storeService.getStoreDdipBoxes(0L))
                .isInstanceOf(StoreValidationException.class);
    }

    @Test
    @DisplayName("페이지 크기가 유효하지 않으면 예외가 발생한다")
    void getStores_InvalidPageSize() {
        // given
        StoreListRequest invalidRequest1 = StoreListRequest.builder().size(0).build();
        StoreListRequest invalidRequest2 = StoreListRequest.builder().size(51).build();
        StoreListRequest invalidRequest3 = StoreListRequest.builder().size(null).build();

        // when & then
        assertThatThrownBy(() -> storeService.getStores(invalidRequest1))
                .isInstanceOf(StoreValidationException.class);

        assertThatThrownBy(() -> storeService.getStores(invalidRequest2))
                .isInstanceOf(StoreValidationException.class);

        assertThatThrownBy(() -> storeService.getStores(invalidRequest3))
                .isInstanceOf(StoreValidationException.class);
    }

    @Test
    @DisplayName("커서가 있는 가게 목록을 조회한다")
    void getStores_WithCursor() {
        // given
        CursorInfo cursorInfo = CursorInfo.ofId(100L);
        String encodedCursor = cursorInfo.encode();

        StoreListRequest request = StoreListRequest.builder()
                .sortBy("id")
                .size(10)
                .cursor(encodedCursor)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorById(eq(100L), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIsFirst()).isFalse(); // 커서가 있으므로 첫 페이지가 아님

        verify(cursorStoreRepository).findStoresWithCursorById(100L, PageRequest.of(0, 11));
    }

    @Test
    @DisplayName("평점 기준으로 가게 목록을 조회한다")
    void getStores_SortByRating() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("rating")
                .size(10)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorByRatingDesc(isNull(), isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result).isNotNull();
        verify(cursorStoreRepository).findStoresWithCursorByRatingDesc(null, null, PageRequest.of(0, 11));
    }

    @Test
    @DisplayName("거리 기준으로 가게 목록을 조회한다")
    void getStores_SortByDistance() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("distance")
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .size(10)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorByDistance(
                eq(37.5665), eq(126.9780), isNull(), isNull(), eq(11)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), eq(37.5665), eq(126.9780)))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result).isNotNull();
        verify(cursorStoreRepository).findStoresWithCursorByDistance(37.5665, 126.9780, null, null, 11);
    }

    @Test
    @DisplayName("거리 기준 정렬이지만 사용자 위치가 없으면 ID 기준으로 조회한다")
    void getStores_SortByDistance_NoUserLocation() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("distance")
                .userLatitude(null)
                .userLongitude(null)
                .size(10)
                .build();

        List<Store> stores = Arrays.asList(testStore);
        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
                .willReturn(stores);
        given(storeMapper.toStoreResponseDto(any(Store.class), isNull(), isNull()))
                .willReturn(testStoreResponseDto);

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result).isNotNull();
        verify(cursorStoreRepository).findStoresWithCursorById(null, PageRequest.of(0, 11));
    }

    @Test
    @DisplayName("빈 결과에 대해서도 올바른 페이지를 반환한다")
    void getStores_EmptyResult() {
        // given
        StoreListRequest request = StoreListRequest.builder()
                .sortBy("id")
                .size(10)
                .build();

        given(cursorStoreRepository.findStoresWithCursorById(isNull(), any(Pageable.class)))
                .willReturn(Collections.emptyList());

        // when
        Page<StoreResponseDto> result = storeService.getStores(request);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getIsLast()).isTrue();
        assertThat(result.getCursor()).isNull();
    }
}