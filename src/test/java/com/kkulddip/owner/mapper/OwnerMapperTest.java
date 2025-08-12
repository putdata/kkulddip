package com.kkulddip.owner.mapper;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.store.entity.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerMapper 테스트")
class OwnerMapperTest {

    private OwnerMapper ownerMapper;

    @BeforeEach
    void setUp() {
        ownerMapper = new OwnerMapper();
    }

    @Test
    @DisplayName("Owner를 OwnerProfileResponse로 매핑")
    void toOwnerProfileResponse() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Owner owner = Owner.builder()
            .ownerId(1L)
            .email("test@example.com")
            .name("테스트 사장")
            .profileImageUrl("https://example.com/profile.jpg")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google123")
            .lastActiveAt(now.minusDays(1))
            .createdAt(now.minusMonths(1))
            .updatedAt(now)
            .build();
        
        Integer totalStoreCount = 3;
        Integer activeStoreCount = 2;

        // when
        OwnerProfileResponse response = ownerMapper.toOwnerProfileResponse(
            owner, totalStoreCount, activeStoreCount);

        // then
        assertThat(response.ownerId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("테스트 사장");
        assertThat(response.profileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        assertThat(response.oauth2Provider()).isEqualTo("GOOGLE");
        assertThat(response.lastActiveAt()).isEqualTo(now.minusDays(1));
        assertThat(response.createdAt()).isEqualTo(now.minusMonths(1));
        assertThat(response.updatedAt()).isEqualTo(now);
        assertThat(response.totalStoreCount()).isEqualTo(3);
        assertThat(response.activeStoreCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Store를 OwnerStoreResponse로 매핑 (기본)")
    void toOwnerStoreResponse() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Store store = Store.builder()
            .storeId(1L)
            .ownerId(1L)
            .storeName("테스트 가게")
            .phone("010-1234-5678")
            .description("테스트 가게 설명")
            .operatingHours("09:00-18:00")
            .isActive(true)
            .ratingAverage(4.5)
            .reviewCount(10L)
            .businessNumber("123-45-67890")
            .storeAddress("서울시 강남구")
            .storeProfileImage("https://example.com/store.jpg")
            .latitude(37.123)
            .longitude(127.456)
            .createdAt(now.minusMonths(1))
            .updatedAt(now)
            .build();

        // when
        OwnerStoreResponse response = ownerMapper.toOwnerStoreResponse(store);

        // then
        assertThat(response.storeId()).isEqualTo(1L);
        assertThat(response.storeName()).isEqualTo("테스트 가게");
        assertThat(response.phone()).isEqualTo("010-1234-5678");
        assertThat(response.description()).isEqualTo("테스트 가게 설명");
        assertThat(response.operatingHours()).isEqualTo("09:00-18:00");
        assertThat(response.isActive()).isTrue();
        assertThat(response.ratingAverage()).isEqualTo(4.5);
        assertThat(response.reviewCount()).isEqualTo(10L);
        assertThat(response.businessNumber()).isEqualTo("123-45-67890");
        assertThat(response.storeAddress()).isEqualTo("서울시 강남구");
        assertThat(response.storeProfileImage()).isEqualTo("https://example.com/store.jpg");
        assertThat(response.latitude()).isEqualTo(37.123);
        assertThat(response.longitude()).isEqualTo(127.456);
        assertThat(response.createdAt()).isEqualTo(now.minusMonths(1));
        assertThat(response.updatedAt()).isEqualTo(now);
        assertThat(response.totalOrderCount()).isEqualTo(0L);
        assertThat(response.totalRevenue()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Store를 OwnerStoreResponse로 매핑 (통계 포함)")
    void toOwnerStoreResponseWithStats() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Store store = Store.builder()
            .storeId(1L)
            .ownerId(1L)
            .storeName("테스트 가게")
            .phone("010-1234-5678")
            .isActive(true)
            .createdAt(now)
            .build();
        
        Long totalOrderCount = 50L;
        Double totalRevenue = 1500000.0;

        // when
        OwnerStoreResponse response = ownerMapper.toOwnerStoreResponseWithStats(
            store, totalOrderCount, totalRevenue);

        // then
        assertThat(response.storeId()).isEqualTo(1L);
        assertThat(response.storeName()).isEqualTo("테스트 가게");
        assertThat(response.totalOrderCount()).isEqualTo(50L);
        assertThat(response.totalRevenue()).isEqualTo(1500000.0);
    }

    @Test
    @DisplayName("SettlementResponse 매핑")
    void toSettlementResponse() {
        // given
        YearMonth period = YearMonth.of(2024, 3);
        Long totalRevenue = 1000000L;
        Long orderCount = 100L;
        Long previousMonthRevenue = 800000L;
        Long previousMonthOrderCount = 80L;

        // when
        SettlementResponse response = ownerMapper.toSettlementResponse(
            1L, "테스트 가게", period, totalRevenue, orderCount, 
            previousMonthRevenue, previousMonthOrderCount
        );

        // then
        assertThat(response.storeId()).isEqualTo(1L);
        assertThat(response.storeName()).isEqualTo("테스트 가게");
        assertThat(response.period()).isEqualTo(period);
        assertThat(response.totalRevenue()).isEqualTo(1000000L);
        assertThat(response.orderCount()).isEqualTo(100L);
        assertThat(response.avgOrderAmount()).isEqualTo(10000L);
        assertThat(response.previousMonthRevenue()).isEqualTo(800000L);
        assertThat(response.revenueGrowthRate()).isEqualTo(25L);
        assertThat(response.previousMonthOrderCount()).isEqualTo(80L);
        assertThat(response.orderCountGrowthRate()).isEqualTo(25L);
    }

    @Test
    @DisplayName("SettlementResponse 매핑 - 주문 수가 0인 경우")
    void toSettlementResponse_ZeroOrders() {
        // given
        YearMonth period = YearMonth.of(2024, 3);
        Long totalRevenue = 0L;
        Long orderCount = 0L;
        Long previousMonthRevenue = 0L;
        Long previousMonthOrderCount = 0L;

        // when
        SettlementResponse response = ownerMapper.toSettlementResponse(
            1L, "테스트 가게", period, totalRevenue, orderCount, 
            previousMonthRevenue, previousMonthOrderCount
        );

        // then
        assertThat(response.avgOrderAmount()).isEqualTo(0L);
        assertThat(response.revenueGrowthRate()).isEqualTo(0L);
        assertThat(response.orderCountGrowthRate()).isEqualTo(0L);
    }

    @Test
    @DisplayName("SettlementResponse 매핑 - 이전 월 데이터가 없는 경우")
    void toSettlementResponse_NoPreviousData() {
        // given
        YearMonth period = YearMonth.of(2024, 3);
        Long totalRevenue = 500000L;
        Long orderCount = 50L;
        Long previousMonthRevenue = null;
        Long previousMonthOrderCount = null;

        // when
        SettlementResponse response = ownerMapper.toSettlementResponse(
            1L, "테스트 가게", period, totalRevenue, orderCount, 
            previousMonthRevenue, previousMonthOrderCount
        );

        // then
        assertThat(response.avgOrderAmount()).isEqualTo(10000L);
        assertThat(response.revenueGrowthRate()).isEqualTo(100L);
        assertThat(response.orderCountGrowthRate()).isEqualTo(100L);
    }

    @Test
    @DisplayName("SettlementSummaryResponse 매핑")
    void toSettlementSummaryResponse() {
        // given
        YearMonth period = YearMonth.of(2024, 3);
        
        List<SettlementResponse> storeSettlements = List.of(
            createSettlementResponse(1L, "가게1", 500000L, 50L),
            createSettlementResponse(2L, "가게2", 300000L, 30L)
        );

        // when
        SettlementSummaryResponse response = ownerMapper.toSettlementSummaryResponse(
            period, storeSettlements);

        // then
        assertThat(response.period()).isEqualTo(period);
        assertThat(response.totalRevenue()).isEqualTo(800000L);
        assertThat(response.totalOrderCount()).isEqualTo(80L);
        assertThat(response.avgOrderAmount()).isEqualTo(10000L);
        assertThat(response.storeCount()).isEqualTo(2);
        assertThat(response.storeSettlements()).hasSize(2);
    }

    @Test
    @DisplayName("SettlementSummaryResponse 매핑 - 빈 리스트")
    void toSettlementSummaryResponse_EmptyList() {
        // given
        YearMonth period = YearMonth.of(2024, 3);
        List<SettlementResponse> storeSettlements = List.of();

        // when
        SettlementSummaryResponse response = ownerMapper.toSettlementSummaryResponse(
            period, storeSettlements);

        // then
        assertThat(response.period()).isEqualTo(period);
        assertThat(response.totalRevenue()).isEqualTo(0L);
        assertThat(response.totalOrderCount()).isEqualTo(0L);
        assertThat(response.avgOrderAmount()).isEqualTo(0L);
        assertThat(response.storeCount()).isEqualTo(0);
        assertThat(response.storeSettlements()).isEmpty();
    }

    private SettlementResponse createSettlementResponse(Long storeId, String storeName, 
                                                      Long revenue, Long orderCount) {
        return new SettlementResponse(
            storeId,
            storeName,
            YearMonth.of(2024, 3),
            revenue,
            orderCount,
            orderCount > 0 ? revenue / orderCount : 0L,
            0L,
            0L,
            0L,
            0L
        );
    }
}