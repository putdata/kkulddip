package com.kkulddip.owner.service;

import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.StoreListResponse;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerStoreService 테스트")
@ActiveProfiles("citest")
class OwnerStoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @InjectMocks
    private OwnerStoreService ownerStoreService;

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 전체 가게")
    void getOwnerStores_AllStores_Success() {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        List<Store> stores = List.of(
            createStore(1L, ownerId, "가게1", true, now),
            createStore(2L, ownerId, "가게2", false, now),
            createStore(3L, ownerId, "가게3", true, now)
        );

        List<OwnerStoreResponse> storeResponses = List.of(
            createOwnerStoreResponse(1L, "가게1", true, 10L, 500000.0),
            createOwnerStoreResponse(2L, "가게2", false, 0L, 0.0),
            createOwnerStoreResponse(3L, "가게3", true, 15L, 750000.0)
        );

        given(storeRepository.findAllByOwnerId(ownerId)).willReturn(stores);
        given(orderJpaRepository.countConfirmedOrdersByStoreId(1L)).willReturn(10L);
        given(orderJpaRepository.sumFinalPriceByStoreId(1L)).willReturn(500000L);
        given(orderJpaRepository.countConfirmedOrdersByStoreId(2L)).willReturn(0L);
        given(orderJpaRepository.sumFinalPriceByStoreId(2L)).willReturn(0L);
        given(orderJpaRepository.countConfirmedOrdersByStoreId(3L)).willReturn(15L);
        given(orderJpaRepository.sumFinalPriceByStoreId(3L)).willReturn(750000L);
        
        given(ownerMapper.toOwnerStoreResponseWithStats(stores.get(0), 10L, 500000.0))
            .willReturn(storeResponses.get(0));
        given(ownerMapper.toOwnerStoreResponseWithStats(stores.get(1), 0L, 0.0))
            .willReturn(storeResponses.get(1));
        given(ownerMapper.toOwnerStoreResponseWithStats(stores.get(2), 15L, 750000.0))
            .willReturn(storeResponses.get(2));

        // when
        StoreListResponse result = ownerStoreService.getOwnerStores(ownerId, false);

        // then
        assertThat(result.stores()).hasSize(3);
        assertThat(result.totalCount()).isEqualTo(3);
        assertThat(result.activeCount()).isEqualTo(2);
        assertThat(result.inactiveCount()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();

        then(storeRepository).should().findAllByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 활성 가게만")
    void getOwnerStores_ActiveOnly_Success() {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        List<Store> activeStores = List.of(
            createStore(1L, ownerId, "가게1", true, now),
            createStore(3L, ownerId, "가게3", true, now)
        );

        List<OwnerStoreResponse> storeResponses = List.of(
            createOwnerStoreResponse(1L, "가게1", true, 10L, 500000.0),
            createOwnerStoreResponse(3L, "가게3", true, 15L, 750000.0)
        );

        given(storeRepository.findActiveStoresByOwnerId(ownerId)).willReturn(activeStores);
        given(orderJpaRepository.countConfirmedOrdersByStoreId(1L)).willReturn(10L);
        given(orderJpaRepository.sumFinalPriceByStoreId(1L)).willReturn(500000L);
        given(orderJpaRepository.countConfirmedOrdersByStoreId(3L)).willReturn(15L);
        given(orderJpaRepository.sumFinalPriceByStoreId(3L)).willReturn(750000L);
        
        given(ownerMapper.toOwnerStoreResponseWithStats(activeStores.get(0), 10L, 500000.0))
            .willReturn(storeResponses.get(0));
        given(ownerMapper.toOwnerStoreResponseWithStats(activeStores.get(1), 15L, 750000.0))
            .willReturn(storeResponses.get(1));

        // when
        StoreListResponse result = ownerStoreService.getOwnerStores(ownerId, true);

        // then
        assertThat(result.stores()).hasSize(2);
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.activeCount()).isEqualTo(2);
        assertThat(result.inactiveCount()).isEqualTo(0);

        then(storeRepository).should().findActiveStoresByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Owner 가게 목록 조회 성공 - 빈 목록")
    void getOwnerStores_EmptyList_Success() {
        // given
        Long ownerId = 1L;
        given(storeRepository.findAllByOwnerId(ownerId)).willReturn(List.of());

        // when
        StoreListResponse result = ownerStoreService.getOwnerStores(ownerId, false);

        // then
        assertThat(result.stores()).isEmpty();
        assertThat(result.totalCount()).isEqualTo(0);
        assertThat(result.activeCount()).isEqualTo(0);
        assertThat(result.inactiveCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 가게 조회 성공")
    void getOwnerStore_Success() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Store store = createStore(storeId, ownerId, "테스트 가게", true, now);
        OwnerStoreResponse expectedResponse = createOwnerStoreResponse(storeId, "테스트 가게", true, 25L, 1250000.0);

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(ownerId));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(orderJpaRepository.countConfirmedOrdersByStoreId(storeId)).willReturn(25L);
        given(orderJpaRepository.sumFinalPriceByStoreId(storeId)).willReturn(1250000L);
        given(ownerMapper.toOwnerStoreResponseWithStats(store, 25L, 1250000.0))
            .willReturn(expectedResponse);

        // when
        OwnerStoreResponse result = ownerStoreService.getOwnerStore(ownerId, storeId);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).should().findById(storeId);
        then(orderJpaRepository).should().countConfirmedOrdersByStoreId(storeId);
        then(orderJpaRepository).should().sumFinalPriceByStoreId(storeId);
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 권한 없음")
    void getOwnerStore_UnauthorizedAccess() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;
        Long actualOwnerId = 2L; // 다른 Owner의 가게

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(actualOwnerId));

        // when & then
        assertThatThrownBy(() -> ownerStoreService.getOwnerStore(ownerId, storeId))
            .isInstanceOf(UnauthorizedStoreAccessException.class);

        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).shouldHaveNoMoreInteractions();
        then(orderJpaRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 가게 없음")
    void getOwnerStore_StoreNotFound_InOwnershipCheck() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerStoreService.getOwnerStore(ownerId, storeId))
            .isInstanceOf(StoreNotFoundException.class);

        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("특정 가게 조회 실패 - 가게 없음 (소유권 검증 후)")
    void getOwnerStore_StoreNotFound_AfterOwnershipCheck() {
        // given
        Long ownerId = 1L;
        Long storeId = 1L;

        given(storeRepository.findOwnerIdByStoreId(storeId)).willReturn(Optional.of(ownerId));
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerStoreService.getOwnerStore(ownerId, storeId))
            .isInstanceOf(StoreNotFoundException.class);

        then(storeRepository).should().findOwnerIdByStoreId(storeId);
        then(storeRepository).should().findById(storeId);
    }

    private Store createStore(Long storeId, Long ownerId, String storeName, Boolean isActive, LocalDateTime createdAt) {
        return Store.builder()
            .storeId(storeId)
            .ownerId(ownerId)
            .storeName(storeName)
            .phone("010-1234-5678")
            .description("테스트 가게 설명")
            .operatingHours("09:00-18:00")
            .isActive(isActive)
            .ratingAverage(4.5)
            .reviewCount(10L)
            .businessNumber("123-45-67890")
            .storeAddress("서울시 강남구")
            .storeProfileImage("https://example.com/store.jpg")
            .latitude(37.123)
            .longitude(127.456)
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .build();
    }

    private OwnerStoreResponse createOwnerStoreResponse(Long storeId, String storeName, Boolean isActive, 
                                                       Long totalOrderCount, Double totalRevenue) {
        return new OwnerStoreResponse(
            storeId, storeName, "010-1234-5678", "테스트 가게 설명", "09:00-18:00",
            isActive, 4.5, 10L, "123-45-67890", "서울시 강남구",
            "https://example.com/store.jpg", 37.123, 127.456,
            LocalDateTime.now(), LocalDateTime.now(),
            totalOrderCount, totalRevenue
        );
    }
}