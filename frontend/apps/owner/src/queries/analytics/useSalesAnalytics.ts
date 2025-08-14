import { useQuery } from '@tanstack/react-query';
import { analyticsService } from '@/services/analyticsService';
import { analyticsQueryKeys } from './analyticsQueryKeys';
import type { SalesAnalyticsResponse } from '@/types/analytics';

/**
 * 매출 분석 데이터를 조회하는 쿼리 훅
 * 
 * @param storeId - 조회할 매장 ID
 * @param startDate - 시작 날짜 (YYYY-MM-DD, 선택)
 * @param endDate - 종료 날짜 (YYYY-MM-DD, 선택)
 * @returns 매출 분석 쿼리 객체
 */
export const useSalesAnalytics = (
  storeId: number,
  startDate?: string,
  endDate?: string,
) => {
  return useQuery<SalesAnalyticsResponse>({
    queryKey: analyticsQueryKeys.sales(storeId, startDate, endDate),
    queryFn: () => analyticsService.getSalesAnalytics(storeId, startDate, endDate),
    enabled: storeId > 0,
  });
};