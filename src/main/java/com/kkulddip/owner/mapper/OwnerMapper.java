package com.kkulddip.owner.mapper;

import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.owner.dto.response.MonthlySettlementResponse;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.MonthlySettlementProjection;
import com.kkulddip.store.entity.Store;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Component
public class OwnerMapper {

    public OwnerProfileResponse toOwnerProfileResponse(Owner owner, Integer totalStoreCount, Integer activeStoreCount) {
        return new OwnerProfileResponse(
            owner.getOwnerId(),
            owner.getEmail(),
            owner.getName(),
            owner.getProfileImageUrl(),
            owner.getOauth2Provider() != null ? owner.getOauth2Provider().name() : null,
            owner.getLastActiveAt(),
            owner.getCreatedAt(),
            owner.getUpdatedAt(),
            null, // businessNumber - Owner 엔티티에 추가 필요
            null, // representativeName - Owner 엔티티에 추가 필요
            totalStoreCount,
            activeStoreCount
        );
    }

    public OwnerStoreResponse toOwnerStoreResponse(Store store) {
        return new OwnerStoreResponse(
            store.getStoreId(),
            store.getStoreName(),
            store.getPhone(),
            store.getDescription(),
            store.getOperatingHours(),
            store.getIsActive(),
            store.getRatingAverage(),
            store.getReviewCount(),
            store.getBusinessNumber(),
            store.getStoreAddress(),
            store.getStoreProfileImage(),
            store.getLatitude(),
            store.getLongitude(),
            store.getCreatedAt(),
            store.getUpdatedAt(),
            0L, // totalOrderCount - 별도 조회 필요
            0.0  // totalRevenue - 별도 조회 필요
        );
    }

    public OwnerStoreResponse toOwnerStoreResponseWithStats(Store store, Long totalOrderCount, Double totalRevenue) {
        return new OwnerStoreResponse(
            store.getStoreId(),
            store.getStoreName(),
            store.getPhone(),
            store.getDescription(),
            store.getOperatingHours(),
            store.getIsActive(),
            store.getRatingAverage(),
            store.getReviewCount(),
            store.getBusinessNumber(),
            store.getStoreAddress(),
            store.getStoreProfileImage(),
            store.getLatitude(),
            store.getLongitude(),
            store.getCreatedAt(),
            store.getUpdatedAt(),
            totalOrderCount,
            totalRevenue
        );
    }

    public SettlementResponse toSettlementResponse(
        Long storeId,
        String storeName,
        YearMonth period,
        Long totalRevenue,
        Long orderCount,
        Long previousMonthRevenue,
        Long previousMonthOrderCount) {

        Long avgOrderAmount = orderCount > 0 ? totalRevenue / orderCount : 0L;

        Long revenueGrowthRate = calculateGrowthRate(totalRevenue, previousMonthRevenue);
        Long orderCountGrowthRate = calculateGrowthRate(orderCount, previousMonthOrderCount);

        return new SettlementResponse(
            storeId,
            storeName,
            period,
            totalRevenue,
            orderCount,
            avgOrderAmount,
            previousMonthRevenue,
            revenueGrowthRate,
            previousMonthOrderCount,
            orderCountGrowthRate
        );
    }

    public SettlementSummaryResponse toSettlementSummaryResponse(
        YearMonth period,
        List<SettlementResponse> storeSettlements) {

        Long totalRevenue = storeSettlements.stream()
            .mapToLong(SettlementResponse::totalRevenue)
            .sum();

        Long totalOrderCount = storeSettlements.stream()
            .mapToLong(SettlementResponse::orderCount)
            .sum();

        Long avgOrderAmount = totalOrderCount > 0 ? totalRevenue / totalOrderCount : 0L;

        return new SettlementSummaryResponse(
            period,
            totalRevenue,
            totalOrderCount,
            avgOrderAmount,
            storeSettlements.size(),
            storeSettlements
        );
    }

    public MonthlySettlementResponse toMonthlySettlementResponse(
        Long storeId,
        String storeName,
        YearMonth startPeriod,
        YearMonth endPeriod,
        List<MonthlySettlementProjection> monthlyData) {

        List<MonthlySettlementResponse.MonthlySettlementData> settlementDataList = 
            monthlyData.stream()
                .map(data -> {
                    YearMonth period = YearMonth.of(data.getYear(), data.getMonth());
                    
                    // 전월 데이터 찾기
                    YearMonth previousMonth = period.minusMonths(1);
                    MonthlySettlementProjection previousData = monthlyData.stream()
                        .filter(d -> d.getYear().equals(previousMonth.getYear()) 
                                && d.getMonth().equals(previousMonth.getMonthValue()))
                        .findFirst()
                        .orElse(null);
                    
                    Long previousRevenue = previousData != null ? previousData.getTotalRevenue() : 0L;
                    Long previousOrderCount = previousData != null ? previousData.getOrderCount() : 0L;
                    
                    Double avgOrderAmount = data.getOrderCount() > 0 
                        ? (double) data.getTotalRevenue() / data.getOrderCount() 
                        : 0.0;
                    
                    Integer revenueGrowthRate = calculateGrowthRateAsInteger(data.getTotalRevenue(), previousRevenue);
                    Integer orderCountGrowthRate = calculateGrowthRateAsInteger(data.getOrderCount(), previousOrderCount);
                    
                    return new MonthlySettlementResponse.MonthlySettlementData(
                        period,
                        data.getTotalRevenue(),
                        data.getOrderCount(),
                        avgOrderAmount,
                        previousRevenue,
                        previousOrderCount,
                        revenueGrowthRate,
                        orderCountGrowthRate
                    );
                })
                .toList();

        Long totalRevenue = settlementDataList.stream()
            .mapToLong(MonthlySettlementResponse.MonthlySettlementData::totalRevenue)
            .sum();

        Long totalOrderCount = settlementDataList.stream()
            .mapToLong(MonthlySettlementResponse.MonthlySettlementData::orderCount)
            .sum();

        Double averageMonthlyRevenue = settlementDataList.isEmpty() 
            ? 0.0 
            : (double) totalRevenue / settlementDataList.size();

        // 전체 기간의 성장률 계산 (첫 달과 마지막 달 비교)
        Double overallGrowthRate = 0.0;
        if (settlementDataList.size() >= 2) {
            Long firstMonthRevenue = settlementDataList.get(0).totalRevenue();
            Long lastMonthRevenue = settlementDataList.get(settlementDataList.size() - 1).totalRevenue();
            overallGrowthRate = firstMonthRevenue > 0 
                ? ((double) (lastMonthRevenue - firstMonthRevenue) / firstMonthRevenue) * 100 
                : 0.0;
        }

        return new MonthlySettlementResponse(
            storeId,
            storeName,
            settlementDataList,
            startPeriod,
            endPeriod,
            settlementDataList.size(),
            totalRevenue,
            totalOrderCount,
            averageMonthlyRevenue,
            overallGrowthRate
        );
    }

    private Long calculateGrowthRate(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100L : 0L;
        }
        
        return ((current - previous) * 100) / previous;
    }

    private Integer calculateGrowthRateAsInteger(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100 : 0;
        }
        
        return Math.toIntExact(((current - previous) * 100) / previous);
    }
}