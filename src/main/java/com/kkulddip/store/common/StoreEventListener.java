package com.kkulddip.store.common;

import com.kkulddip.common.event.DdipBoxDataRequestEvent;
import com.kkulddip.common.event.DdipBoxDataResponseEvent;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Store 도메인의 이벤트 수신 및 처리 담당
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StoreEventListener {
    
    private final DdipBoxRepository ddipBoxRepository;
    private final StoreRepository storeRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * DdipBox 데이터 요청 이벤트 처리
     * 
     * @param event 다른 도메인에서 발송한 DdipBox 데이터 요청 이벤트
     */
    @EventListener
    @Async("storeEventTaskExecutor") // 비동기 처리
    @Transactional(readOnly = true)
    public void handleDdipBoxDataRequest(DdipBoxDataRequestEvent event) {
        log.info("DdipBox 데이터 요청 수신 - txId: {}, storeId: {}, ddipBoxIds: {}, requestingDomain: {}", 
                event.txId(), event.storeId(), event.ddipBoxIds(), event.requestingDomain());
        
        try {
            // 1. DdipBox 목록 조회
            List<DdipBox> ddipBoxes = findDdipBoxesByIds(event.storeId(), event.ddipBoxIds());
            
            // 2. 성공 응답 발송
            DdipBoxDataResponseEvent responseEvent = DdipBoxDataResponseEvent.success(
                event.txId(),
                event.storeId(),
                ddipBoxes,
                event.requestingDomain()
            );
            
            eventPublisher.publishEvent(responseEvent);
            
            log.info("DdipBox 데이터 응답 발송 완료 - txId: {}, 조회된 DdipBox 수: {}", 
                    event.txId(), ddipBoxes.size());
                    
        } catch (Exception e) {
            log.error("DdipBox 데이터 요청 처리 중 오류 발생 - txId: {}", event.txId(), e);
        }
    }
    
    /**
     * DdipBox 목록 조회 (가게 ID와 DdipBox ID 목록으로)
     */
    private List<DdipBox> findDdipBoxesByIds(Long storeId, List<Long> ddipBoxIds) {
        if (ddipBoxIds == null || ddipBoxIds.isEmpty()) {
            // DdipBox ID가 없으면 해당 가게의 모든 활성화된 DdipBox 반환
            return ddipBoxRepository.findActiveByStoreId(storeId);
        }
        
        // 특정 DdipBox ID들만 조회
        return ddipBoxRepository.findActiveByStoreIdAndDdipBoxIds(storeId, ddipBoxIds);
    }

}