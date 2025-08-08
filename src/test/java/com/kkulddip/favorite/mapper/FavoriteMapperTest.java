package com.kkulddip.favorite.mapper;

import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoriteMapper 테스트")
@ActiveProfiles("citest")
class FavoriteMapperTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private DdipBoxRepository ddipBoxRepository;

    @InjectMocks
    private FavoriteMapper favoriteMapper;

    private Favorite testFavorite;
    private Store testStore;
    private DdipBox testDdipBox;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        testFavorite = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(testDateTime)
            .build();

        testStore = Store.builder()
            .storeId(200L)
            .ownerId(300L)
            .storeName("테스트 가게")
            .storeAddress("서울시 강남구")
            .storeProfileImage("store-image.jpg")
            .ratingAverage(4.5)
            .latitude(37.5665)
            .longitude(126.9780)
            .isActive(true)
            .build();

        testDdipBox = DdipBox.builder()
            .ddipboxId(1L)
            .store(testStore)
            .ddipboxName("테스트 띱박스")
            .category("한식")
            .originalPrice(10000L)
            .salePrice(8000L)
            .dailyQuantity(10L)
            .remainingQuantity(10L)
            .maxPerCustomer(1L)
            .isActive(true)
            .build();
    }

    @Test
    @DisplayName("Favorite을 AddFavoriteResponse로 변환")
    void toAddFavoriteResponse_Success() {
        // when
        AddFavoriteResponse result = favoriteMapper.toAddFavoriteResponse(testFavorite);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(1L);
        assertThat(result.customerId()).isEqualTo(100L);
        assertThat(result.storeId()).isEqualTo(200L);
        assertThat(result.createdAt()).isEqualTo(testDateTime);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 성공")
    void toGetFavoritesResponse_Success() {
        // given
        given(storeRepository.findActiveStore(200L)).willReturn(Optional.of(testStore));
        given(ddipBoxRepository.findActiveByStoreId(200L))
            .willReturn(List.of(testDdipBox));

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(1L);
        assertThat(result.storeId()).isEqualTo(200L);
        assertThat(result.storeName()).isEqualTo("테스트 가게");
        assertThat(result.storeProfileImage()).isEqualTo("store-image.jpg");
        assertThat(result.representationDdipboxName()).isEqualTo("테스트 띱박스");
        assertThat(result.representationDdipboxProfileImage()).isNull(); // DdipBox에 프로필 이미지 필드가 없음
        assertThat(result.reviewRating()).isEqualTo(4.5);
        assertThat(result.distanceFromCustomer()).isNull();
        assertThat(result.category()).isEqualTo("한식");
        assertThat(result.createdAt()).isEqualTo(testDateTime);

        verify(storeRepository).findActiveStore(200L);
        verify(ddipBoxRepository).findActiveByStoreId(200L);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 거리 계산 포함")
    void toGetFavoritesResponse_WithDistance() {
        // given
        Double userLat = 37.5700; // 사용자 위치
        Double userLng = 126.9800;
        given(storeRepository.findActiveStore(200L)).willReturn(Optional.of(testStore));
        given(ddipBoxRepository.findActiveByStoreId(200L))
            .willReturn(List.of(testDdipBox));

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, userLat, userLng);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(1L);
        assertThat(result.distanceFromCustomer()).isNotNull();
        assertThat(result.distanceFromCustomer()).isGreaterThan(0.0);

        verify(storeRepository).findActiveStore(200L);
        verify(ddipBoxRepository).findActiveByStoreId(200L);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 비활성화된 가게")
    void toGetFavoritesResponse_InactiveStore() {
        // given
        given(storeRepository.findActiveStore(200L)).willReturn(Optional.empty());

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, null, null);

        // then
        assertThat(result).isNull();

        verify(storeRepository).findActiveStore(200L);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 대표 띱박스 없음")
    void toGetFavoritesResponse_NoDdipBox() {
        // given
        given(storeRepository.findActiveStore(200L)).willReturn(Optional.of(testStore));
        given(ddipBoxRepository.findActiveByStoreId(200L))
            .willReturn(List.of());

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.favoriteId()).isEqualTo(1L);
        assertThat(result.storeId()).isEqualTo(200L);
        assertThat(result.storeName()).isEqualTo("테스트 가게");
        assertThat(result.representationDdipboxName()).isNull();
        assertThat(result.representationDdipboxProfileImage()).isNull();
        assertThat(result.category()).isNull();

        verify(storeRepository).findActiveStore(200L);
        verify(ddipBoxRepository).findActiveByStoreId(200L);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 잘못된 사용자 위치")
    void toGetFavoritesResponse_InvalidUserLocation() {
        // given
        Double invalidLat = null;
        Double invalidLng = 126.9780;
        given(storeRepository.findActiveStore(200L)).willReturn(Optional.of(testStore));
        given(ddipBoxRepository.findActiveByStoreId(200L))
            .willReturn(List.of(testDdipBox));

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, invalidLat, invalidLng);

        // then
        assertThat(result).isNotNull();
        assertThat(result.distanceFromCustomer()).isNull(); // 거리 계산 불가

        verify(storeRepository).findActiveStore(200L);
        verify(ddipBoxRepository).findActiveByStoreId(200L);
    }

    @Test
    @DisplayName("Favorite을 GetFavoritesResponse로 변환 - 잘못된 가게 위치")
    void toGetFavoritesResponse_InvalidStoreLocation() {
        // given
        Store storeWithoutLocation = Store.builder()
            .storeId(200L)
            .ownerId(300L)
            .storeName("테스트 가게")
            .storeAddress("서울시 강남구")
            .storeProfileImage("store-image.jpg")
            .ratingAverage(4.5)
            .latitude(null) // 위도 없음
            .longitude(126.9780)
            .isActive(true)
            .build();

        given(storeRepository.findActiveStore(200L)).willReturn(Optional.of(storeWithoutLocation));
        given(ddipBoxRepository.findActiveByStoreId(200L))
            .willReturn(List.of(testDdipBox));

        // when
        GetFavoritesResponse result = favoriteMapper.toGetFavoritesResponse(testFavorite, 37.5700, 126.9800);

        // then
        assertThat(result).isNotNull();
        assertThat(result.distanceFromCustomer()).isNull(); // 거리 계산 불가

        verify(storeRepository).findActiveStore(200L);
        verify(ddipBoxRepository).findActiveByStoreId(200L);
    }
}