import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  SettlementSummaryResponse,
  StoreSettlementResponse,
  MonthlySettlementResponse,
} from '@/types/settlement';

/**
 * Settlement 관련 API 서비스
 */
export const settlementService = {
  /**
   * 전체 가게 정산 요약 조회
   */
  getSettlementSummary: (
    year: number,
    month: number,
  ): Promise<SettlementSummaryResponse> => {
    return apiClient.get<SettlementSummaryResponse>(
      API_PATH.SETTLEMENT.SUMMARY,
      {
        year,
        month,
      },
    );
  },

  /**
   * 특정 가게 정산 조회
   */
  getStoreSettlement: (
    storeId: number,
    year: number,
    month: number,
  ): Promise<StoreSettlementResponse> => {
    return apiClient.get<StoreSettlementResponse>(
      API_PATH.SETTLEMENT.STORE(storeId),
      {
        year,
        month,
      },
    );
  },

  /**
   * 특정 가게 월별 정산 리스트 조회
   */
  getMonthlySettlement: (
    storeId: number,
    startYear: number,
    startMonth: number,
    endYear: number,
    endMonth: number,
  ): Promise<MonthlySettlementResponse> => {
    return apiClient.get<MonthlySettlementResponse>(
      API_PATH.SETTLEMENT.MONTHLY(storeId),
      {
        startYear,
        startMonth,
        endYear,
        endMonth,
      },
    );
  },
};
