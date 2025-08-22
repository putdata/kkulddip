package com.kkulddip.common.event;

import lombok.Builder;

import java.util.List;

/**
 * 다른 도메인에서 DdipBox 데이터를 요청하는 이벤트
 * Store 도메인이 수신하여 처리
 */
@Builder
public record DdipBoxDataRequestEvent(
    /**
     * 트랜잭션을 식별하기 위한 고유 ID
     */
    String txId,
    
    /**
     * 대상 가게 ID
     */
    Long storeId,
    
    /**
     * 요청할 띱박스 ID 목록
     */
    List<Long> ddipBoxIds,
    
    /**
     * 요청한 도메인 정보 (응답 시 참조)
     */
    String requestingDomain,
    
    /**
     * 요청 시각 (Optional: 디버깅용)
     */
    long requestTimestamp
) {
    
}