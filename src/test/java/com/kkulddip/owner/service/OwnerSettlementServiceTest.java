package com.kkulddip.owner.service;

import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.SettlementProjection;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.StoreSettlementProjection;
import com.kkulddip.owner.dto.request.SettlementQueryRequest;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.owner.exception.UnauthorizedStoreAccessException;
import com.kkulddip.owner.mapper.OwnerMapper;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerSettlementService 테스트")
@ActiveProfiles("citest")
class OwnerSettlementServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @InjectMocks
    private OwnerSettlementService ownerSettlementService;

    @Test
    @DisplayName("가게 정산 조회 성공")
    void getStoreSettlement_Success() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);
        
        Store store = createStore(storeId, ownerId, "테스트 가게");
        SettlementProjection currentSettlement = createSettlementProjection(1000000L, 100L);
        SettlementProjection previousSettlement = createSettlementProjection(800000L, 80L);
        
        SettlementResponse expectedResponse = createSettlementResponse(
            storeId, "테스트 가게", YearMonth.of(2024, 3),
            1000000L, 100L, 800000L, 80L
        );

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(ownerId));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(orderJpaRepository.findSettlementByStoreIdAndMonth(storeId, 2024, 3))
            .willReturn(currentSettlement);
        given(orderJpaRepository.findSettlementByStoreIdAndMonth(storeId, 2024, 2))
            .willReturn(previousSettlement);
        given(ownerMapper.toSettlementResponse(
            eq(storeId), eq("테스트 가게"), eq(YearMonth.of(2024, 3)),
            eq(1000000L), eq(100L), eq(800000L), eq(80L)
        )).willReturn(expectedResponse);

        // when
        SettlementResponse result = ownerSettlementService.getStoreSettlement(ownerId, storeId, request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).should().findById(storeId);
        then(orderJpaRepository).should().findSettlementByStoreIdAndMonth(storeId, 2024, 3);
        then(orderJpaRepository).should().findSettlementByStoreIdAndMonth(storeId, 2024, 2);
    }

    @Test
    @DisplayName("가게 정산 조회 성공 - 이전 월 데이터 없음")
    void getStoreSettlement_Success_NoPreviousData() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);
        
        Store store = createStore(storeId, ownerId, "테스트 가게");
        SettlementProjection currentSettlement = createSettlementProjection(500000L, 50L);
        
        SettlementResponse expectedResponse = createSettlementResponse(
            storeId, "테스트 가게", YearMonth.of(2024, 3),
            0L, 0L, 0L, 0L
        );

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(ownerId));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(orderJpaRepository.findSettlementByStoreIdAndMonth(storeId, 2024, 3))
            .willReturn(currentSettlement);
        given(orderJpaRepository.findSettlementByStoreIdAndMonth(storeId, 2024, 2))
            .willReturn(null);
        given(ownerMapper.toSettlementResponse(
            eq(storeId), eq("테스트 가게"), eq(YearMonth.of(2024, 3)),
            eq(500000L), eq(50L), eq(0L), eq(0L)
        )).willReturn(expectedResponse);

        // when
        SettlementResponse result = ownerSettlementService.getStoreSettlement(ownerId, storeId, request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 권한 없음")
    void getStoreSettlement_UnauthorizedAccess() {
        // given
        Long ownerId = 1L;
        Long otherOwnerId = 2L;
        Long storeId = 1L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(otherOwnerId));

        // when & then
        assertThatThrownBy(() -> ownerSettlementService.getStoreSettlement(ownerId, storeId, request))
            .isInstanceOf(UnauthorizedStoreAccessException.class);

        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).shouldHaveNoMoreInteractions();
        then(orderJpaRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("가게 정산 조회 실패 - 가게 없음")
    void getStoreSettlement_StoreNotFound() {
        // given
        Long ownerId = 1L;
        Long storeId = 999L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(ownerId));
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerSettlementService.getStoreSettlement(ownerId, storeId, request))
            .isInstanceOf(StoreNotFoundException.class);
    }

    @Test
    @DisplayName("Owner 전체 정산 요약 조회 성공")
    void getOwnerSettlementSummary_Success() {
        // given
        Long ownerId = 1L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);
        
        List<Long> storeIds = List.of(1L, 2L);
        List<StoreSettlementProjection> currentSettlements = List.of(
            createStoreSettlementProjection(1L, 500000L, 50L),
            createStoreSettlementProjection(2L, 300000L, 30L)
        );
        
        List<SettlementResponse> storeSettlements = List.of(
            createSettlementResponse(1L, "가게1", YearMonth.of(2024, 3), 500000L, 50L, 400000L, 40L),
            createSettlementResponse(2L, "가게2", YearMonth.of(2024, 3), 300000L, 30L, 250000L, 25L)
        );
        
        SettlementSummaryResponse expectedResponse = new SettlementSummaryResponse(
            YearMonth.of(2024, 3),
            800000L,
            80L,
            10000L,
            2,
            storeSettlements
        );

        given(storeRepository.findStoreIdsByOwnerId(ownerId)).willReturn(storeIds);
        given(orderJpaRepository.findSettlementByStoreIdsAndMonth(storeIds, 2024, 3))
            .willReturn(currentSettlements);
        given(orderJpaRepository.findSettlementByStoreIdsAndMonth(storeIds, 2024, 2))
            .willReturn(List.of());
        given(storeRepository.findActiveStoresByOwnerId(ownerId))
            .willReturn(List.of(
                createStore(1L, ownerId, "가게1"),
                createStore(2L, ownerId, "가게2")
            ));
        given(ownerMapper.toSettlementResponse(any(), any(), any(), any(), any(), any(), any()))
            .willReturn(storeSettlements.get(0), storeSettlements.get(1));
        given(ownerMapper.toSettlementSummaryResponse(YearMonth.of(2024, 3), storeSettlements))
            .willReturn(expectedResponse);

        // when
        SettlementSummaryResponse result = ownerSettlementService.getOwnerSettlementSummary(ownerId, request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.totalRevenue()).isEqualTo(800000L);
        assertThat(result.totalOrderCount()).isEqualTo(80L);
        assertThat(result.storeCount()).isEqualTo(2);
        assertThat(result.storeSettlements()).hasSize(2);
    }

    @Test
    @DisplayName("Owner 전체 정산 요약 조회 성공 - 가게 없음")
    void getOwnerSettlementSummary_NoStores() {
        // given
        Long ownerId = 1L;
        SettlementQueryRequest request = new SettlementQueryRequest(2024, 3);

        given(storeRepository.findStoreIdsByOwnerId(ownerId)).willReturn(List.of());

        // when
        SettlementSummaryResponse result = ownerSettlementService.getOwnerSettlementSummary(ownerId, request);

        // then
        assertThat(result.period()).isEqualTo(YearMonth.of(2024, 3));
        assertThat(result.totalRevenue()).isEqualTo(0L);
        assertThat(result.totalOrderCount()).isEqualTo(0L);
        assertThat(result.avgOrderAmount()).isEqualTo(0L);
        assertThat(result.storeCount()).isEqualTo(0);
        assertThat(result.storeSettlements()).isEmpty();
    }

    private Store createStore(Long storeId, Long ownerId, String storeName) {
        return Store.builder()
            .storeId(storeId)
            .ownerId(ownerId)
            .storeName(storeName)
            .phone("010-1234-5678")
            .description("테스트 가게")
            .operatingHours("09:00-18:00")
            .isActive(true)
            .ratingAverage(4.5)
            .reviewCount(10L)
            .businessNumber("123-45-67890")
            .storeAddress("서울시 강남구")
            .storeProfileImage("profile.jpg")
            .latitude(37.123)
            .longitude(127.456)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private SettlementProjection createSettlementProjection(Long totalRevenue, Long orderCount) {
        return new SettlementProjection() {
            @Override
            public Long getTotalRevenue() {
                return totalRevenue;
            }

            @Override
            public Long getOrderCount() {
                return orderCount;
            }
        };
    }

    private StoreSettlementProjection createStoreSettlementProjection(Long storeId, Long totalRevenue, Long orderCount) {
        return new StoreSettlementProjection() {
            @Override
            public Long getStoreId() {
                return storeId;
            }

            @Override
            public Long getTotalRevenue() {
                return totalRevenue;
            }

            @Override
            public Long getOrderCount() {
                return orderCount;
            }
        };
    }

    private SettlementResponse createSettlementResponse(Long storeId, String storeName, YearMonth period,
                                                       Long totalRevenue, Long orderCount,
                                                       Long previousRevenue, Long previousOrderCount) {
        return new SettlementResponse(
            storeId, storeName, period, totalRevenue, orderCount,
            orderCount > 0 ? totalRevenue / orderCount : 0L,
            previousRevenue, 0L, previousOrderCount, 0L
        );
    }
}