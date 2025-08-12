import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  UpdateDdipBoxRequest,
  UpdateDdipBoxResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 정보를 수정하는 뮤테이션 훅
 */
export const useUpdateDdipbox = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateDdipBoxResponse,
    Error,
    { ddipboxId: number; data: UpdateDdipBoxRequest }
  >({
    mutationFn: ({ ddipboxId, data }) =>
      ddipboxService.updateDdipbox(ddipboxId, data),
    onSuccess: response => {
      // 딥박스 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.store(response.storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.detail(response.ddipboxId),
      });
    },
  });
};
