import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  ToggleDdipBoxStatusRequest,
  ToggleDdipBoxStatusResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 활성화/비활성화 상태를 토글하는 뮤테이션 훅
 */
export const useToggleDdipboxStatus = () => {
  const queryClient = useQueryClient();

  return useMutation<
    ToggleDdipBoxStatusResponse,
    Error,
    { ddipboxId: number; data: ToggleDdipBoxStatusRequest; storeId: number }
  >({
    mutationFn: ({ ddipboxId, data }) =>
      ddipboxService.toggleDdipboxStatus(ddipboxId, data),
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
