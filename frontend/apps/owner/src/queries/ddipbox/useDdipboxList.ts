import { useQuery } from '@tanstack/react-query';
import type { DdipBoxListResponse } from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 특정 매장의 띱박스 목록을 조회하는 쿼리 훅
 *
 * @param storeId - 조회할 매장 ID
 * @param params - 페이지네이션 파라미터 (cursor, limit)
 * @returns 띱박스 목록 쿼리 객체
 */
export const useDdipboxList = (
  storeId: number,
  params?: { cursor?: string; limit?: number },
) => {
  return useQuery<DdipBoxListResponse>({
    queryKey: ddipboxQueryKeys.list(storeId, params),
    queryFn: () => ddipboxService.getDdipboxList(storeId, params),
    enabled: storeId !== null && storeId > 0,
  });
};
