package com.kkulddip.analytics.service;

import com.kkulddip.analytics.dto.response.AnalyticsResponseDto;
import java.time.LocalDate;

/**
 * 매출 분석 서비스 인터페이스
 * 주문 데이터를 기반으로 통계 분석 및 매출 예측을 제공합니다.
 */
public interface AnalyticsService {

    /**
     * 특정 스토어의 특정 기간 매출 분석을 수행합니다.
     *
     * @param storeId 분석할 스토어 ID
     * @param startDate 분석 시작 날짜
     * @param endDate 분석 종료 날짜
     * @return 분석 결과 (TOP 3 아이템, 가격대별 통계, 매출 예측)
     */
    AnalyticsResponseDto generateSalesAnalytics(Long storeId, LocalDate startDate, LocalDate endDate);
}
