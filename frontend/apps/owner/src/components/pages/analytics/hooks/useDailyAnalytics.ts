import { useMemo } from 'react';

/**
 * 일일 분석 데이터 처리 훅
 *
 * @description
 * 일일 개요 카드, 분석 요약 카드 등 일일 분석 관련 비즈니스 로직을 관리합니다.
 *
 * @param totalRevenue 총 매출
 * @param totalOrders 총 주문 수
 * @param totalWeight 총 무게
 * @returns 일일 분석에 필요한 처리된 데이터와 계산 결과
 */
export const useDailyAnalytics = (
  totalRevenue?: number,
  totalOrders?: number,
  totalWeight?: number,
) => {
  /*
   * 평균 주문 금액 계산
   */
  const averageOrderValue = useMemo(() => {
    if (!totalOrders || totalOrders === 0 || !totalRevenue) return 0;
    return totalRevenue / totalOrders;
  }, [totalRevenue, totalOrders]);

  /*
   * 주문당 평균 무게 계산
   */
  const averageWeightPerOrder = useMemo(() => {
    if (!totalOrders || totalOrders === 0 || !totalWeight) return 0;
    return totalWeight / totalOrders;
  }, [totalWeight, totalOrders]);

  /*
   * 매출 상태 평가 (높음/보통/낮음)
   */
  const getRevenueStatus = () => {
    if (!averageOrderValue) return null;
    
    if (averageOrderValue >= 50000) {
      return {
        status: 'high',
        color: 'default',
        text: '높음',
        description: '주문당 평균 매출이 높습니다',
      };
    } else if (averageOrderValue >= 20000) {
      return {
        status: 'medium',
        color: 'secondary',
        text: '보통',
        description: '안정적인 주문 금액입니다',
      };
    } else {
      return {
        status: 'low',
        color: 'outline',
        text: '낮음',
        description: '주문 금액 향상이 필요합니다',
      };
    }
  };

  /*
   * 무게 상태 평가 (적정/과다/부족)
   */
  const getWeightStatus = () => {
    if (!averageWeightPerOrder) return null;
    
    if (averageWeightPerOrder >= 2.0) {
      return {
        status: 'heavy',
        color: 'default',
        text: '과다',
        description: '배송비 최적화를 고려해보세요',
      };
    } else if (averageWeightPerOrder >= 0.5) {
      return {
        status: 'normal',
        color: 'secondary',
        text: '적정',
        description: '적절한 상품 구성입니다',
      };
    } else {
      return {
        status: 'light',
        color: 'outline',
        text: '부족',
        description: '추가 상품 판매를 고려해보세요',
      };
    }
  };

  return {
    averageOrderValue,
    averageWeightPerOrder,
    getRevenueStatus,
    getWeightStatus,
  };
};