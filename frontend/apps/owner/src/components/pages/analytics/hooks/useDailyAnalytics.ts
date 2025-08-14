import { useMemo } from 'react';

/**
 * 일일 분석 데이터 처리 훅
 *
 * @description
 * 일일 개요 카드, 분석 요약 카드 등 일일 분석 관련 비즈니스 로직을 관리합니다.
 *
 * @param totalRevenue 총 매출
 * @param totalOrders 총 주문 수
 * @returns 일일 분석에 필요한 처리된 데이터와 계산 결과
 */
export const useDailyAnalytics = (
  totalRevenue?: number,
  totalOrders?: number,
) => {
  /*
   * 평균 주문 금액 계산
   */
  const averageOrderValue = useMemo(() => {
    if (!totalOrders || totalOrders === 0 || !totalRevenue) {
      return 0;
    }
    return totalRevenue / totalOrders;
  }, [totalRevenue, totalOrders]);

  return {
    averageOrderValue,
  };
};