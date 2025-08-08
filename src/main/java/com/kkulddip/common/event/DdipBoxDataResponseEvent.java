package com.kkulddip.common.event;

import com.kkulddip.store.entity.DdipBox;
import lombok.Builder;

import java.util.List;

/**
 * Store 도메인에서 DdipBox 데이터를 응답하는 이벤트
 * 요청한 도메인이 수신하여 처리
 */
@Builder
public record DdipBoxDataResponseEvent(
    /**
     * 원본 트랜잭션 ID (요청-응답 매칭용)
     */
    String txId,
    
    /**
     * 대상 가게 ID
     */
    Long storeId,
    
    /**
     * 조회된 띱박스 목록
     */
    List<DdipBox> ddipBoxes,
    
    /**
     * 대상 도메인 (응답을 받을 도메인)
     */
    String targetDomain,
    
    /**
     * 요청 처리 성공 여부
     */
    boolean success,
    
    /**
     * 오류 메시지 (실패 시)
     */
    String errorMessage,
    
    /**
     * 응답 시각
     */
    long responseTimestamp
) {
    
    /**
     * 성공 응답 생성
     */
    public static DdipBoxDataResponseEvent success(String txId, Long storeId, 
                                                 List<DdipBox> ddipBoxes, String targetDomain) {
        return DdipBoxDataResponseEvent.builder()
            .txId(txId)
            .storeId(storeId)
            .ddipBoxes(ddipBoxes)
            .targetDomain(targetDomain)
            .success(true)
            .errorMessage(null)
            .responseTimestamp(System.currentTimeMillis())
            .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public static DdipBoxDataResponseEvent failure(String txId, Long storeId, 
                                                 String targetDomain, String errorMessage) {
        return DdipBoxDataResponseEvent.builder()
            .txId(txId)
            .storeId(storeId)
            .ddipBoxes(List.of())
            .targetDomain(targetDomain)
            .success(false)
            .errorMessage(errorMessage)
            .responseTimestamp(System.currentTimeMillis())
            .build();
    }
}