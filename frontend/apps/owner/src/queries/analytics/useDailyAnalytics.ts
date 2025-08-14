import { useQuery } from '@tanstack/react-query';
import { analyticsService } from '@/services/analyticsService';
import { analyticsQueryKeys } from './analyticsQueryKeys';
import type { DailyAnalyticsResponse } from '@/types/analytics';

/**
 * 일별 매출 분석 데이터를 조회하는 쿼리 훅
 * 
 * @param storeId - 조회할 매장 ID
 * @param targetDate - 분석 대상 날짜 (YYYY-MM-DD, 선택)
 * @returns 일별 분석 쿼리 객체
 */
export const useDailyAnalytics = (storeId: number, targetDate?: string) => {
  return useQuery<DailyAnalyticsResponse>({
    queryKey: analyticsQueryKeys.daily(storeId, targetDate),
    queryFn: () => analyticsService.getDailyAnalytics(storeId, targetDate),
    enabled: storeId > 0,
  });
};