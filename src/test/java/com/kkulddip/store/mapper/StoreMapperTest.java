package com.kkulddip.store.mapper;

import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.DdipBoxItemDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreMapper 테스트")
@ActiveProfiles("citest")
class StoreMapperTest {

    @Mock
    private DdipBoxRepository ddipBoxRepository;
    
    @Mock
    private DdipBoxItemRepository ddipBoxItemRepository;
    
    private StoreMapper storeMapper;
    private Store testStore;
    private DdipBox testDdipBox;
    private DdipBoxItem testDdipBoxItem;

    @BeforeEach
    void setUp() {
        storeMapper = new StoreMapper(ddipBoxRepository, ddipBoxItemRepository);
        
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
                .build();

        testDdipBoxItem.setDdipBox(testDdipBox);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 사용자 위치 없음")
    void toStoreResponseDto_WithoutUserLocation() {
        // given - 대표 띱박스 정보를 위한 Mock 설정
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Arrays.asList(testDdipBox));
        
        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.storeId()).isEqualTo(1L);
        assertThat(result.ownerId()).isEqualTo(100L);
        assertThat(result.storeName()).isEqualTo("테스트 마트");
        assertThat(result.storeAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(result.description()).isEqualTo("신선한 식품을 판매하는 마트입니다");
        assertThat(result.operatingHours()).isEqualTo("09:00-22:00");
        assertThat(result.phoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.ratingAverage()).isEqualTo(4.5);
        assertThat(result.reviewNum()).isEqualTo(128L);
        assertThat(result.storeProfileImage()).isEqualTo("profile.jpg");
        assertThat(result.active()).isTrue();
        assertThat(result.distanceFromUser()).isNull();
        assertThat(result.representativeDdipboxName()).isEqualTo("유기농 세트");
        assertThat(result.representativeOriginalPrice()).isEqualTo(20000L);
        assertThat(result.representativeSalePrice()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 사용자 위치 있음")
    void toStoreResponseDto_WithUserLocation() {
        // given
        Double userLat = 37.5651; // 사용자 위치 (강남역 근처)
        Double userLng = 126.9895;
        
        // 대표 띱박스 정보를 위한 Mock 설정
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Arrays.asList(testDdipBox));

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, userLat, userLng);

        // then
        assertThat(result.storeId()).isEqualTo(1L);
        assertThat(result.storeName()).isEqualTo("테스트 마트");
        assertThat(result.distanceFromUser()).isNotNull();
        assertThat(result.distanceFromUser()).isGreaterThan(0.0);
        assertThat(result.distanceFromUser()).isLessThan(10.0); // 서울 내 거리
        
        // 대표 띱박스 정보 검증
        assertThat(result.representativeDdipboxName()).isEqualTo("유기농 세트");
        assertThat(result.representativeOriginalPrice()).isEqualTo(20000L);
        assertThat(result.representativeSalePrice()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 유효하지 않은 사용자 위치")
    void toStoreResponseDto_WithInvalidUserLocation() {
        // given
        Double invalidLat = 200.0; // 유효하지 않은 위도
        Double invalidLng = 300.0; // 유효하지 않은 경도
        
        // 대표 띱박스 정보를 위한 Mock 설정
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Arrays.asList(testDdipBox));

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, invalidLat, invalidLng);

        // then
        assertThat(result.distanceFromUser()).isNull();
        
        // 대표 띱박스 정보 검증
        assertThat(result.representativeDdipboxName()).isEqualTo("유기농 세트");
        assertThat(result.representativeOriginalPrice()).isEqualTo(20000L);
        assertThat(result.representativeSalePrice()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - DdipBox가 없는 경우")
    void toStoreResponseDto_WithoutDdipBoxes() {
        // given - 빈 리스트 반환하도록 Mock 설정
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Collections.emptyList());

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.representativeDdipboxName()).isNull();
        assertThat(result.representativeOriginalPrice()).isNull();
        assertThat(result.representativeSalePrice()).isNull();
    }

    @Test
    @DisplayName("Store를 StoreResponseDto로 변환한다 - 비활성화된 DdipBox가 있는 경우")
    void toStoreResponseDto_WithInactiveDdipBoxes() {
        // given
        testDdipBox.setIsActive(false);

        // when
        StoreResponseDto result = storeMapper.toStoreResponseDto(testStore, null, null);

        // then
        assertThat(result.representativeDdipboxName()).isNull();
        assertThat(result.representativeOriginalPrice()).isNull();
        assertThat(result.representativeSalePrice()).isNull();
    }

    @Test
    @DisplayName("Store를 StoreDetailDto로 변환한다")
    void toStoreDetailDto() {
        // given
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Arrays.asList(testDdipBox));
        
        // when
        StoreDetailDto result = storeMapper.toStoreDetailDto(testStore);

        // then
        assertThat(result.storeId()).isEqualTo(1L);
        assertThat(result.ownerId()).isEqualTo(100L);
        assertThat(result.storeName()).isEqualTo("테스트 마트");
        assertThat(result.storeAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(result.description()).isEqualTo("신선한 식품을 판매하는 마트입니다");
        assertThat(result.operatingHours()).isEqualTo("09:00-22:00");
        assertThat(result.phoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.ratingAverage()).isEqualTo(4.5);
        assertThat(result.reviewCount()).isEqualTo(128L);
        assertThat(result.businessNumber()).isEqualTo("123-45-67890");
        assertThat(result.storeProfileImage()).isEqualTo("profile.jpg");
        assertThat(result.latitude()).isEqualTo(37.5665);
        assertThat(result.longitude()).isEqualTo(126.9780);
        assertThat(result.active()).isTrue();
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
        assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 0));
        
        // DdipBox 정보 확인
        assertThat(result.ddipBoxes()).hasSize(1);
        StoreDetailDto.DdipBoxSummaryDto ddipBoxSummary = result.ddipBoxes().get(0);
        assertThat(ddipBoxSummary.ddipboxId()).isEqualTo(1L);
        assertThat(ddipBoxSummary.ddipboxName()).isEqualTo("유기농 세트");
        assertThat(ddipBoxSummary.category()).isEqualTo("유기농");
        assertThat(ddipBoxSummary.originalPrice()).isEqualTo(20000L);
        assertThat(ddipBoxSummary.salePrice()).isEqualTo(15000L);
        assertThat(ddipBoxSummary.remainingQuantity()).isEqualTo(30L);
        assertThat(ddipBoxSummary.active()).isTrue();
    }

    @Test
    @DisplayName("Store를 StoreDetailDto로 변환한다 - 비활성화된 DdipBox 제외")
    void toStoreDetailDto_ExcludeInactiveDdipBoxes() {
        // given - repository에서 이미 활성화된 것만 반환
        given(ddipBoxRepository.findActiveByStoreId(1L))
            .willReturn(Arrays.asList(testDdipBox)); // 활성화된 것만 반환

        // when
        StoreDetailDto result = storeMapper.toStoreDetailDto(testStore);

        // then
        assertThat(result.ddipBoxes()).hasSize(1); // 활성화된 것만 포함
        assertThat(result.ddipBoxes().get(0).ddipboxName()).isEqualTo("유기농 세트");
    }

    @Test
    @DisplayName("DdipBox를 DdipBoxCardViewDto로 변환한다")
    void toDdipBoxCardViewDto() {
        // given
        given(ddipBoxItemRepository.findByDdipBoxId(1L))
            .willReturn(Arrays.asList(testDdipBoxItem));
        
        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.ddipboxId()).isEqualTo(1L);
        assertThat(result.storeId()).isEqualTo(1L);
        assertThat(result.ddipboxName()).isEqualTo("유기농 세트");
        assertThat(result.description()).isEqualTo("신선한 유기농 채소 세트");
        assertThat(result.category()).isEqualTo("유기농");
        assertThat(result.originalPrice()).isEqualTo(20000L);
        assertThat(result.salePrice()).isEqualTo(15000L);
        assertThat(result.dailyQuantity()).isEqualTo(50L);
        assertThat(result.remainingQuantity()).isEqualTo(30L);
        assertThat(result.maxPerCustomer()).isEqualTo(2L);
        assertThat(result.active()).isTrue();
        
        // 구성상품 확인
        assertThat(result.items()).hasSize(1);
        DdipBoxItemDto item = result.items().get(0);
        assertThat(item.itemId()).isEqualTo(1L);
        assertThat(item.ddipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(item.originalPrice()).isEqualTo(5000);
        assertThat(item.itemQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("DdipBoxItem을 DdipBoxItemDto로 변환한다")
    void toDdipBoxItemDto() {
        // when
        DdipBoxItemDto result = storeMapper.toDdipBoxItemDto(testDdipBoxItem);

        // then
        assertThat(result.itemId()).isEqualTo(1L);
        assertThat(result.ddipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(result.originalPrice()).isEqualTo(5000);
        assertThat(result.itemQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 DdipBoxItems 리스트도 정상적으로 변환한다")
    void toDdipBoxCardViewDto_EmptyItems() {
        // given - 빈 리스트 반환하도록 Mock 설정
        given(ddipBoxItemRepository.findByDdipBoxId(1L))
            .willReturn(Collections.emptyList());

        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.items()).isEmpty();
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
        
        // Mock 설정 - 여러 아이템 반환
        given(ddipBoxItemRepository.findByDdipBoxId(1L))
            .willReturn(Arrays.asList(testDdipBoxItem, item2));

        // when
        DdipBoxCardViewDto result = storeMapper.toDdipBoxCardViewDto(testDdipBox);

        // then
        assertThat(result.items()).hasSize(2);
        assertThat(result.items().get(0).ddipboxItemName()).isEqualTo("유기농 토마토");
        assertThat(result.items().get(1).ddipboxItemName()).isEqualTo("유기농 양상추");
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
        assertThat(result.distanceFromUser()).isNull();
    }
}