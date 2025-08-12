import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  BulkUpdateDdipBoxRequest,
  BulkUpdateDdipBoxResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스를 일괄 수정하는 뮤테이션 훅
 */
export const useBulkUpdateDdipboxes = () => {
  const queryClient = useQueryClient();

  return useMutation<
    BulkUpdateDdipBoxResponse,
    Error,
    { storeId: number; data: BulkUpdateDdipBoxRequest }
  >({
    mutationFn: ({ storeId, data }) =>
      ddipboxService.bulkUpdateDdipboxes(storeId, data),
    onSuccess: (response, { storeId, data }) => {
      // 해당 가게의 딥박스 목록을 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.store(storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });

      // 수정된 딥박스들의 상세 정보 캐시도 무효화
      data.ddipboxIds.forEach(ddipboxId => {
        queryClient.invalidateQueries({
          queryKey: ddipboxQueryKeys.detail(ddipboxId),
        });
      });
    },
  });
};
