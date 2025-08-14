import { useQuery } from '@tanstack/react-query';
import { settlementService } from '@/services/settlementService';
import { settlementQueryKeys } from './settlementQueryKeys';
import type { SettlementSummaryResponse } from '@/types/settlement';

/**
 * 전체 가게 정산 요약을 조회하는 쿼리 훅
 * 
 * @param year - 조회할 년도
 * @param month - 조회할 월
 * @returns 정산 요약 쿼리 객체
 */
export const useSettlementSummary = (year: number, month: number) => {
  return useQuery<SettlementSummaryResponse>({
    queryKey: settlementQueryKeys.summary(year, month),
    queryFn: () => settlementService.getSettlementSummary(year, month),
    enabled: year > 0 && month > 0 && month <= 12,
  });
};