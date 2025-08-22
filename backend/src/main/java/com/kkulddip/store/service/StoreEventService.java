package com.kkulddip.store.service;

import com.kkulddip.common.event.DdipBoxDataRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Store 도메인에서 다른 도메인으로 이벤트를 발송하는 서비스
 * (필요시 Store 도메인에서 다른 도메인의 데이터를 요청할 때 사용)
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StoreEventService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 다른 도메인에 DdipBox 데이터를 요청
     * (예: Order 도메인에서 Store 도메인의 데이터가 필요할 때)
     * 
     * @param storeId 대상 가게 ID
     * @param ddipBoxIds 요청할 띱박스 ID 목록 (null이면 전체)
     * @param targetDomain 요청할 대상 도메인
     * @return 트랜잭션 ID (응답 매칭용)
     */
    public String requestDdipBoxData(Long storeId, List<Long> ddipBoxIds, String targetDomain) {
        String txId = generateTxId();
        
        DdipBoxDataRequestEvent event = DdipBoxDataRequestEvent.builder()
            .txId(txId)
            .storeId(storeId)
            .ddipBoxIds(ddipBoxIds)
            .requestingDomain(targetDomain)
            .requestTimestamp(System.currentTimeMillis())
            .build();
        
        log.info("DdipBox 데이터 요청 발송 - txId: {}, storeId: {}, targetDomain: {}", 
                txId, storeId, targetDomain);
        
        eventPublisher.publishEvent(event);
        
        return txId;
    }
    
    /**
     * 고유한 트랜잭션 ID 생성
     */
    private String generateTxId() {
        return "STORE_REQ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}