import { useQuery } from '@tanstack/react-query';
import { settlementService } from '@/services/settlementService';
import { settlementQueryKeys } from './settlementQueryKeys';
import type { MonthlySettlementResponse } from '@/types/settlement';

/**
 * 특정 가게의 월별 정산 리스트를 조회하는 쿼리 훅
 *
 * @param storeId - 조회할 매장 ID
 * @param startYear - 시작 년도
 * @param startMonth - 시작 월
 * @param endYear - 종료 년도
 * @param endMonth - 종료 월
 * @returns 월별 정산 리스트 쿼리 객체
 */
export const useMonthlySettlement = (
  storeId: number,
  startYear: number,
  startMonth: number,
  endYear: number,
  endMonth: number,
) => {
  return useQuery<MonthlySettlementResponse>({
    queryKey: settlementQueryKeys.monthly(
      storeId,
      startYear,
      startMonth,
      endYear,
      endMonth,
    ),
    queryFn: () =>
      settlementService.getMonthlySettlement(
        storeId,
        startYear,
        startMonth,
        endYear,
        endMonth,
      ),
    enabled:
      storeId > 0 &&
      startYear > 0 &&
      startMonth > 0 &&
      startMonth <= 12 &&
      endYear > 0 &&
      endMonth > 0 &&
      endMonth <= 12,
  });
};
