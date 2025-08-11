package com.kkulddip.owner.mapper;

import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
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

    private Long calculateGrowthRate(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100L : 0L;
        }
        
        return ((current - previous) * 100) / previous;
    }
}