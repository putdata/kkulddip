import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  SalesAnalyticsResponse,
  DailyAnalyticsResponse,
} from '@/types/analytics';

/**
 * Analytics 관련 API 서비스
 */
export const analyticsService = {
  /**
   * 매출 분석 데이터 조회
   */
  getSalesAnalytics: (
    storeId: number,
    startDate?: string,
    endDate?: string,
  ): Promise<SalesAnalyticsResponse> => {
    const params: Record<string, string | number> = { storeId };
    if (startDate) {
      params.startDate = startDate;
    }
    if (endDate) {
      params.endDate = endDate;
    }

    return apiClient.get<SalesAnalyticsResponse>(API_PATH.ANALYTICS.SALES, params);
  },

  /**
   * 일별 매출 분석 데이터 조회
   */
  getDailyAnalytics: (
    storeId: number,
    targetDate?: string,
  ): Promise<DailyAnalyticsResponse> => {
    const params: Record<string, string | number> = { storeId };
    if (targetDate) {
      params.targetDate = targetDate;
    }

    return apiClient.get<DailyAnalyticsResponse>(API_PATH.ANALYTICS.DAILY, params);
  },
};