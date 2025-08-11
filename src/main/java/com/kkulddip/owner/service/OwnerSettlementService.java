package com.kkulddip.owner.service;

import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.SettlementProjection;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.StoreSettlementProjection;
import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository.MonthlySettlementProjection;
import com.kkulddip.owner.dto.request.SettlementQueryRequest;
import com.kkulddip.owner.dto.request.MonthlySettlementRangeRequest;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import com.kkulddip.owner.dto.response.MonthlySettlementResponse;
import com.kkulddip.owner.exception.UnauthorizedStoreAccessException;
import com.kkulddip.owner.mapper.OwnerMapper;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OwnerSettlementService {

    private final StoreRepository storeRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OwnerMapper ownerMapper;

    public SettlementResponse getStoreSettlement(Long ownerId, Long storeId, SettlementQueryRequest request) {
        log.info("가게 정산 조회 요청 - ownerId: {}, storeId: {}, year: {}, month: {}", 
            ownerId, storeId, request.year(), request.month());

        validateStoreOwnership(ownerId, storeId);
        
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));

        YearMonth currentPeriod = YearMonth.of(request.year(), request.month());
        YearMonth previousPeriod = currentPeriod.minusMonths(1);

        SettlementProjection currentSettlement = orderJpaRepository.findSettlementByStoreIdAndMonth(
            storeId, request.year(), request.month()
        );

        SettlementProjection previousSettlement = orderJpaRepository.findSettlementByStoreIdAndMonth(
            storeId, previousPeriod.getYear(), previousPeriod.getMonthValue()
        );

        Long currentRevenue = currentSettlement != null 
            ? currentSettlement.getTotalRevenue()
            : 0L;
        
        Long currentOrderCount = currentSettlement != null 
            ? currentSettlement.getOrderCount()
            : 0L;

        Long previousRevenue = previousSettlement != null 
            ? previousSettlement.getTotalRevenue()
            : 0L;
        
        Long previousOrderCount = previousSettlement != null 
            ? previousSettlement.getOrderCount()
            : 0L;

        SettlementResponse response = ownerMapper.toSettlementResponse(
            storeId,
            store.getStoreName(),
            currentPeriod,
            currentRevenue,
            currentOrderCount,
            previousRevenue,
            previousOrderCount
        );

        log.info("가게 정산 조회 완료 - storeId: {}, 매출: {}, 주문 수: {}", 
            storeId, currentRevenue, currentOrderCount);

        return response;
    }

    public SettlementSummaryResponse getOwnerSettlementSummary(Long ownerId, SettlementQueryRequest request) {
        log.info("Owner 전체 정산 요약 조회 요청 - ownerId: {}, year: {}, month: {}", 
            ownerId, request.year(), request.month());

        List<Long> storeIds = storeRepository.findStoreIdsByOwnerId(ownerId);
        
        if (storeIds.isEmpty()) {
            log.info("Owner {}의 활성 가게가 없음", ownerId);
            return createEmptySettlementSummary(YearMonth.of(request.year(), request.month()));
        }

        YearMonth currentPeriod = YearMonth.of(request.year(), request.month());
        YearMonth previousPeriod = currentPeriod.minusMonths(1);

        List<StoreSettlementProjection> currentSettlements = orderJpaRepository.findSettlementByStoreIdsAndMonth(
            storeIds, request.year(), request.month()
        );

        List<StoreSettlementProjection> previousSettlements = orderJpaRepository.findSettlementByStoreIdsAndMonth(
            storeIds, previousPeriod.getYear(), previousPeriod.getMonthValue()
        );

        Map<Long, StoreSettlementProjection> previousSettlementMap = previousSettlements.stream()
            .collect(Collectors.toMap(
                StoreSettlementProjection::getStoreId,
                settlement -> settlement
            ));

        Map<Long, Store> storeMap = storeRepository.findActiveStoresByOwnerId(ownerId).stream()
            .collect(Collectors.toMap(Store::getStoreId, store -> store));

        List<SettlementResponse> storeSettlements = currentSettlements.stream()
            .map(current -> {
                StoreSettlementProjection previous = previousSettlementMap.get(current.getStoreId());
                Store store = storeMap.get(current.getStoreId());
                
                return ownerMapper.toSettlementResponse(
                    current.getStoreId(),
                    store != null ? store.getStoreName() : "Unknown Store",
                    currentPeriod,
                    current.getTotalRevenue(),
                    current.getOrderCount(),
                    previous != null ? previous.getTotalRevenue() : 0L,
                    previous != null ? previous.getOrderCount() : 0L
                );
            })
            .toList();

        SettlementSummaryResponse response = ownerMapper.toSettlementSummaryResponse(
            currentPeriod,
            storeSettlements
        );

        log.info("Owner 전체 정산 요약 조회 완료 - ownerId: {}, 총 매출: {}, 총 주문 수: {}, 가게 수: {}", 
            ownerId, response.totalRevenue(), response.totalOrderCount(), response.storeCount());

        return response;
    }

    public MonthlySettlementResponse getStoreMonthlySettlement(Long ownerId, Long storeId, MonthlySettlementRangeRequest request) {
        log.info("가게 월별 정산 조회 요청 - ownerId: {}, storeId: {}, 시작: {}/{}, 종료: {}/{}", 
            ownerId, storeId, request.startYear(), request.startMonth(), request.endYear(), request.endMonth());

        validateStoreOwnership(ownerId, storeId);
        validateDateRange(request);

        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));

        YearMonth startPeriod = YearMonth.of(request.startYear(), request.startMonth());
        YearMonth endPeriod = YearMonth.of(request.endYear(), request.endMonth());

        List<MonthlySettlementProjection> monthlyData = orderJpaRepository.findMonthlySettlementByStoreIdAndPeriod(
            storeId, 
            request.startYear(), 
            request.startMonth(),
            request.endYear(),
            request.endMonth()
        );

        MonthlySettlementResponse response = ownerMapper.toMonthlySettlementResponse(
            storeId,
            store.getStoreName(),
            startPeriod,
            endPeriod,
            monthlyData
        );

        log.info("가게 월별 정산 조회 완료 - storeId: {}, 조회 월 수: {}, 총 매출: {}", 
            storeId, response.totalMonths(), response.totalRevenue());

        return response;
    }

    private void validateDateRange(MonthlySettlementRangeRequest request) {
        YearMonth startPeriod = YearMonth.of(request.startYear(), request.startMonth());
        YearMonth endPeriod = YearMonth.of(request.endYear(), request.endMonth());

        if (startPeriod.isAfter(endPeriod)) {
            throw new IllegalArgumentException("시작 날짜가 종료 날짜보다 늦을 수 없습니다.");
        }

        long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(startPeriod, endPeriod) + 1;
        if (monthsBetween > 12) {
            throw new IllegalArgumentException("조회 가능한 최대 기간은 12개월입니다.");
        }

        YearMonth currentMonth = YearMonth.now();
        if (endPeriod.isAfter(currentMonth)) {
            throw new IllegalArgumentException("미래 날짜의 정산 데이터는 조회할 수 없습니다.");
        }
    }

    private SettlementSummaryResponse createEmptySettlementSummary(YearMonth period) {
        return new SettlementSummaryResponse(
            period,
            0L,
            0L,
            0L,
            0,
            List.of()
        );
    }

    private void validateStoreOwnership(Long ownerId, Long storeId) {
        Long storeOwnerId = storeRepository.findOwnerIdByStoreId(storeId)
            .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));
        
        if (!storeOwnerId.equals(ownerId)) {
            log.warn("Unauthorized store access attempt - ownerId: {}, storeId: {}, actual ownerId: {}", 
                ownerId, storeId, storeOwnerId);
            throw new UnauthorizedStoreAccessException(storeId, ownerId);
        }
    }
}