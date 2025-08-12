package com.kkulddip.order.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.store.repository.StoreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreAuthService {
    
    private final StoreRepository storeRepository;
    
    /**
     * 사장님이 특정 가게에 대한 권한을 가지고 있는지 확인
     * 
     * @param ownerId 사장님 ID
     * @param storeId 가게 ID
     * @throws OrderException 권한이 없을 경우
     */
    public void validateOwnerPermission(Long ownerId, StoreId storeId) {
        List<Long> ownedStoreIds = getOwnedStoreIds(ownerId);
        if (!ownedStoreIds.contains(storeId.value())) {
            log.warn("가게 접근 권한 없음 - ownerId: {}, requestedStoreId: {}, ownedStores: {}", 
                ownerId, storeId.value(), ownedStoreIds);
            throw OrderException.orderCannotBeConfirmed(String.valueOf(storeId.value()));
        }
        log.debug("가게 접근 권한 확인 완료 - ownerId: {}, storeId: {}", ownerId, storeId.value());
    }
    
    /**
     * 사장님이 소유한 모든 가게 ID 목록 조회
     * 
     * @param ownerId 사장님 ID
     * @return 소유한 가게 ID 목록
     */
    public List<Long> getOwnedStoreIds(Long ownerId) {
        try {
            List<Long> storeIds = storeRepository.findStoreIdsByOwnerId(ownerId);
            log.debug("사장님 소유 가게 조회 - ownerId: {}, storeCount: {}", ownerId, storeIds.size());
            return storeIds;
        } catch (Exception e) {
            log.error("사장님 소유 가게 조회 중 오류 발생 - ownerId: {}", ownerId, e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    /**
     * 사장님이 소유한 가게가 있는지 확인
     * 
     * @param ownerId 사장님 ID
     * @return 소유한 가게가 있으면 true, 없으면 false
     */
    public boolean hasOwnedStores(Long ownerId) {
        return !getOwnedStoreIds(ownerId).isEmpty();
    }
}