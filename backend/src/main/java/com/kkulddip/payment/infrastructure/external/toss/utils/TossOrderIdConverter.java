package com.kkulddip.payment.infrastructure.external.toss.utils;

/**
 * 토스페이먼츠 API용 orderId 변환 유틸리티
 * - 토스페이먼츠는 orderId가 6글자 이상이어야 함
 * - 내부적으로는 Long 타입 사용, 외부 API 호출 시에만 "ORDER-" 프리픽스 추가/제거
 */
public class TossOrderIdConverter {
    
    private static final String ORDER_PREFIX = "ORDER-";
    
    /**
     * Long orderId를 토스페이먼츠용 String orderId로 변환
     * 예: 12345L → "ORDER-12345"
     */
    public static String toTossOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId는 null일 수 없습니다.");
        }
        return ORDER_PREFIX + orderId;
    }
    
    /**
     * 토스페이먼츠 응답의 String orderId를 Long orderId로 변환
     * 예: "ORDER-12345" → 12345L
     */
    public static Long fromTossOrderId(String tossOrderId) {
        if (tossOrderId == null || !tossOrderId.startsWith(ORDER_PREFIX)) {
            throw new IllegalArgumentException("올바르지 않은 토스 orderId 형식입니다: " + tossOrderId);
        }
        
        String orderIdStr = tossOrderId.substring(ORDER_PREFIX.length());
        try {
            return Long.parseLong(orderIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("orderId 숫자 변환 실패: " + orderIdStr, e);
        }
    }
    
    /**
     * 토스 orderId 형식인지 확인
     */
    public static boolean isTossOrderIdFormat(String orderId) {
        return orderId != null && orderId.startsWith(ORDER_PREFIX);
    }
}