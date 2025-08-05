package com.kkulddip.store.mapper;

import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.DdipBoxItemDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.entity.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StoreMapper 테스트")
class StoreMapperTest {

    private StoreMapper storeMapper;
    private Store testStore;
    private DdipBox testDdipBox;
    private DdipBoxItem testDdipBoxItem;

    @BeforeEach
    void setUp() {
        storeMapper = new StoreMapper();
        
        // 테스트용 Store 생성
        testStore = Store.builder()
                .storeId(1L)
                .ownerId(100L)
                .storeName("테스트 마트")
                .storeAddress("서울시 강남구 테헤란로 123")
                .description("신선한 식품을 판매하는 마트입니다")
                .operatingHours("09:00-22:00")
                .phone("02-1234-5678")
                .ratingAverage(4.5)
                .reviewCount(128L)
                .businessNumber("123-45-67890")
                .storeProfileImage("profile.jpg")
                .latitude(37.5665)
                .longitude(126.9780)
                .isActive(true)
                .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2024, 6, 1, 12, 0))
                .build();

        // 테스트용 DdipBoxItem 생성
        testDdipBoxItem = DdipBoxItem.builder()
                .itemId(1L)
                .ddipboxItemName("유기농 토마토")
                .originalPrice(5000)
                .itemQuantity(2)
                .build();

        // 테스트용 DdipBox 생성
        testDdipBox = DdipBox.builder()
                .ddipboxId(1L)
                .store(testStore)
                .ddipboxName("유기농 세트")
                .description("신선한 유기농 채소 세트")
                .category("유기농")
                .originalPrice(20000L)
                .salePrice(15000L)
                .dailyQuantity(50L)
                .remainingQuantity(30L)
                .maxPerCustomer(2L)
                .isActive(true)
                .ddipBoxItems(Arrays.asList(testDdipBoxItem))
                .build();

        testStore.setDdipBoxes(Arrays.asList(testDdipBox));
        testDdipBoxItem.setDdipBox(testDdipBox);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 사용자 위치 없음")
    void toStoreResponseDto_WithoutUserLocation() {
        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getOwnerId()).isEqualTo(100L);
        assertThat(result.getStoreName()).isEqualTo("테스트 마트");
        assertThat(result.getStoreAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(result.getDescription()).isEqualTo("신선한 식품을 판매하는 마트입니다");
        assertThat(result.getOperatingHours()).isEqualTo("09:00-22:00");
        assertThat(result.getPhoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.getRatingAverage()).isEqualTo(4.5);
        assertThat(result.getReviewNum()).isEqualTo(128L);
        assertThat(result.getStoreProfileImage()).isEqualTo("profile.jpg");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getDistanceFromUser()).isNull();
        assertThat(result.getRepresentativeDdipboxName()).isEqualTo("유기농 세트");
        assertThat(result.getRepresentativeOriginalPrice()).isEqualTo(20000L);
        assertThat(result.getRepresentativeSalePrice()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 사용자 위치 있음")
    void toStoreResponseDto_WithUserLocation() {
        // given
        Double userLat = 37.5651; // 사용자 위치 (강남역 근처)
        Double userLng = 126.9895;

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, userLat, userLng);

        // then
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getStoreName()).isEqualTo("테스트 마트");
        assertThat(result.getDistanceFromUser()).isNotNull();
        assertThat(result.getDistanceFromUser()).isGreaterThan(0.0);
        assertThat(result.getDistanceFromUser()).isLessThan(10.0); // 서울 내 거리
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 유효하지 않은 사용자 위치")
    void toStoreResponseDto_WithInvalidUserLocation() {
        // given
        Double invalidLat = 200.0; // 유효하지 않은 위도
        Double invalidLng = 300.0; // 유효하지 않은 경도

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, invalidLat, invalidLng);

        // then
        assertThat(result.getDistanceFromUser()).isNull();
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - DdipBox가 없는 경우")
    void toStoreResponseDto_WithoutDdipBoxes() {
        // given
        testStore.setDdipBoxes(Collections.emptyList());

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.getRepresentativeDdipboxName()).isNull();
        assertThat(result.getRepresentativeOriginalPrice()).isNull();
        assertThat(result.getRepresentativeSalePrice()).isNull();
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 비활성화된 DdipBox가 있는 경우")
    void toStoreResponseDto_WithInactiveDdipBoxes() {
        // given
        testDdipBox.setIsActive(false);

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.getRepresentativeDdipboxName()).isNull();
        assertThat(result.getRepresentativeOriginalPrice()).isNull();
        assertThat(result.getRepresentativeSalePrice()).isNull();
    }

    @Test
    @DisplayName("Store를 StoreDetailDto로 변환한다")
    void toStoreDetailDto() {
        // when
        StoreDetailDto result = storeMapper.toStoreDetailDto(testStore);

        // then
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getOwnerId()).isEqualTo(100L);
        assertThat(result.getStoreName()).isEqualTo("테스트 마트");
        assertThat(result.getStoreAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(result.getDescription()).isEqualTo("신선한 식품을 판매하는 마트입니다");
        assertThat(result.getOperatingHours()).isEqualTo("09:00-22:00");
        assertThat(result.getPhoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.getRatingAverage()).isEqualTo(4.5);
        assertThat(result.getReviewCount()).isEqualTo(128L);
        assertThat(result.getBusinessNumber()).isEqualTo("123-45-67890");
        assertThat(result.getStoreProfileImage()).isEqualTo("profile.jpg");
        assertThat(result.getLatitude()).isEqualTo(37.5665);
        assertThat(result.getLongitude()).isEqualTo(126.9780);
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
        assertThat(result.getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 0));
        
        // DdipBox 정보 확인
        assertThat(result.getDdipBoxes()).hasSize(1);
        StoreDetailDto.DdipBoxSummaryDto ddipBoxSummary = result.getDdipBoxes().get(0);
        assertThat(ddipBoxSummary.getDdipboxId()).isEqualTo(1L);
        assertThat(ddipBoxSummary.getDdipboxName()).isEqualTo("유기농 세트");
        assertThat(ddipBoxSummary.getCategory()).isEqualTo("유기농");
        assertThat(ddipBoxSummary.getOriginalPrice()).isEqualTo(20000L);
        assertThat(ddipBoxSummary.getSalePrice()).isEqualTo(15000L);
        assertThat(ddipBoxSummary.getRemainingQuantity()).isEqualTo(30L);
        assertThat(ddipBoxSummary.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Store를 StoreDetailDto로 변환한다 - 비활성화된 DdipBox 제외")
    void toStoreDetailDto_ExcludeInactiveDdipBoxes() {
        // given
        DdipBox inactiveDdipBox = DdipBox.builder()
                .ddipboxId(2L)
                .ddipboxName("비활성화된 세트")
                .isActive(false)
                .build();
        testStore.setDdipBoxes(Arrays.asList(testDdipBox, inactiveDdipBox));

        // when
        StoreDetailDto result = storeMapper.toStoreDetailDto(testStore);

        // then
        assertThat(result.getDdipBoxes()).hasSize(1); // 활성화된 것만 포함
        assertThat(result.getDdipBoxes().get(0).getDdipboxName()).isEqualTo("유기농 세트");
    }

    @Test
    @DisplayName("DdipBox를 DdipBoxCardViewDto로 변환한다")
    void toDdipBoxCardViewDto() {
        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.getDdipboxId()).isEqualTo(1L);
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getDdipboxName()).isEqualTo("유기농 세트");
        assertThat(result.getDescription()).isEqualTo("신선한 유기농 채소 세트");
        assertThat(result.getCategory()).isEqualTo("유기농");
        assertThat(result.getOriginalPrice()).isEqualTo(20000L);
        assertThat(result.getSalePrice()).isEqualTo(15000L);
        assertThat(result.getDailyQuantity()).isEqualTo(50L);
        assertThat(result.getRemainingQuantity()).isEqualTo(30L);
        assertThat(result.getMaxPerCustomer()).isEqualTo(2L);
        assertThat(result.getIsActive()).isTrue();
        
        // 구성상품 확인
        assertThat(result.getItems()).hasSize(1);
        DdipBoxItemDto item = result.getItems().get(0);
        assertThat(item.getItemId()).isEqualTo(1L);
        assertThat(item.getDdipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(item.getOriginalPrice()).isEqualTo(5000);
        assertThat(item.getItemQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("DdipBoxItem을 DdipBoxItemDto로 변환한다")
    void toDdipBoxItemDto() {
        // when
        DdipBoxItemDto result = storeMapper.toDdipBoxItemDto(testDdipBoxItem);

        // then
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getDdipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(result.getOriginalPrice()).isEqualTo(5000);
        assertThat(result.getItemQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 DdipBoxItems 리스트도 정상적으로 변환한다")
    void toDdipBoxCardViewDto_EmptyItems() {
        // given
        testDdipBox.setDdipBoxItems(Collections.emptyList());

        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("여러 DdipBoxItems를 정상적으로 변환한다")
    void toDdipBoxCardViewDto_MultipleItems() {
        // given
        DdipBoxItem item2 = DdipBoxItem.builder()
                .itemId(2L)
                .ddipboxItemName("유기농 양상추")
                .originalPrice(3000)
                .itemQuantity(1)
                .build();
        testDdipBox.setDdipBoxItems(Arrays.asList(testDdipBoxItem, item2));

        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getDdipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(result.getItems().get(1).getDdipboxItemName()).isEqualTo("유기농 양상추");
    }

    @Test
    @DisplayName("Store 좌표가 null인 경우 거리 계산하지 않는다")
    void toStoreResponseDto_StoreLocationNull() {
        // given
        testStore.setLatitude(null);
        testStore.setLongitude(null);

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, 37.5665, 126.9780);

        // then
        assertThat(result.getDistanceFromUser()).isNull();
    }
}