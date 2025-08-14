import { useQuery } from '@tanstack/react-query';
import { settlementService } from '@/services/settlementService';
import { settlementQueryKeys } from './settlementQueryKeys';
import type { StoreSettlementResponse } from '@/types/settlement';

/**
 * 특정 가게의 정산 데이터를 조회하는 쿼리 훅
 * 
 * @param storeId - 조회할 매장 ID
 * @param year - 조회할 년도
 * @param month - 조회할 월
 * @returns 매장 정산 쿼리 객체
 */
export const useStoreSettlement = (
  storeId: number,
  year: number,
  month: number,
) => {
  return useQuery<StoreSettlementResponse>({
    queryKey: settlementQueryKeys.store(storeId, year, month),
    queryFn: () => settlementService.getStoreSettlement(storeId, year, month),
    enabled: storeId > 0 && year > 0 && month > 0 && month <= 12,
  });
};