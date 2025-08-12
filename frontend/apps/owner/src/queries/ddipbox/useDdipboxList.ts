import { useQuery } from '@tanstack/react-query';
import type { DdipBoxListResponse } from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 특정 가게의 딥박스 목록을 조회
 */
export const useDdipboxList = (
  storeId: number,
  params?: { cursor?: string; limit?: number },
) => {
  return useQuery<DdipBoxListResponse>({
    queryKey: ddipboxQueryKeys.list(storeId, params),
    queryFn: () => ddipboxService.getDdipboxList(storeId, params),
    enabled: Boolean(storeId),
  });
};
