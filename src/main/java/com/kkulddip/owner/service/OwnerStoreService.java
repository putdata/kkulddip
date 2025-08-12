package com.kkulddip.owner.service;

import com.kkulddip.order.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.StoreListResponse;
import com.kkulddip.owner.exception.UnauthorizedStoreAccessException;
import com.kkulddip.owner.mapper.OwnerMapper;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OwnerStoreService {

    private final StoreRepository storeRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OwnerMapper ownerMapper;

    public StoreListResponse getOwnerStores(Long ownerId, Boolean activeOnly) {
        log.info("Owner 가게 목록 조회 요청 - ownerId: {}, activeOnly: {}", ownerId, activeOnly);

        List<Store> stores = activeOnly != null && activeOnly 
            ? storeRepository.findActiveStoresByOwnerId(ownerId)
            : storeRepository.findAllByOwnerId(ownerId);

        List<OwnerStoreResponse> storeResponses = stores.stream()
            .map(this::mapToStoreResponseWithStats)
            .toList();

        int totalCount = storeResponses.size();
        int activeCount = (int) storeResponses.stream()
            .mapToLong(store -> store.isActive() ? 1 : 0)
            .sum();
        int inactiveCount = totalCount - activeCount;

        StoreListResponse response = new StoreListResponse(
            storeResponses,
            totalCount,
            activeCount,
            inactiveCount,
            false, // 현재는 페이징 미구현
            null   // nextCursor
        );

        log.info("Owner 가게 목록 조회 완료 - ownerId: {}, 총 가게 수: {}, 활성: {}, 비활성: {}", 
            ownerId, totalCount, activeCount, inactiveCount);

        return response;
    }

    public OwnerStoreResponse getOwnerStore(Long ownerId, Long storeId) {
        log.info("Owner 특정 가게 조회 요청 - ownerId: {}, storeId: {}", ownerId, storeId);

        validateStoreOwnership(ownerId, storeId);
        
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + storeId));

        OwnerStoreResponse response = mapToStoreResponseWithStats(store);

        log.info("Owner 특정 가게 조회 완료 - ownerId: {}, storeId: {}, storeName: {}", 
            ownerId, storeId, store.getStoreName());

        return response;
    }

    private OwnerStoreResponse mapToStoreResponseWithStats(Store store) {
        Long totalOrderCount = orderJpaRepository.countConfirmedOrdersByStoreId(store.getStoreId());
        Long totalRevenue = orderJpaRepository.sumFinalPriceByStoreId(store.getStoreId());
        
        return ownerMapper.toOwnerStoreResponseWithStats(
            store, 
            totalOrderCount,
            totalRevenue.doubleValue()
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