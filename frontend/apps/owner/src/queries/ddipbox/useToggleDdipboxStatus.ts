import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  UpdateDdipBoxStatusRequest,
  UpdateDdipBoxStatusResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 활성화/비활성화 상태를 토글하는 뮤테이션 훅
 */
export const useToggleDdipboxStatus = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateDdipBoxStatusResponse,
    Error,
    { storeId: number; ddipboxId: number; data: UpdateDdipBoxStatusRequest }
  >({
    mutationFn: ({ storeId, ddipboxId, data }) =>
      ddipboxService.toggleDdipboxStatus(storeId, ddipboxId, data),
    onSuccess: (response, { storeId }) => {
      // 딥박스 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.store(storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.detail(response.ddipboxId),
      });
    },
  });
};
