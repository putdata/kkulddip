import { useQuery } from '@tanstack/react-query';
import type { DdipBox } from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 특정 딥박스의 상세 정보를 조회
 */
export const useDdipboxDetail = (ddipboxId: number) => {
  return useQuery<DdipBox>({
    queryKey: ddipboxQueryKeys.detail(ddipboxId),
    queryFn: () => ddipboxService.getDdipbox(ddipboxId),
    enabled: Boolean(ddipboxId),
  });
};
