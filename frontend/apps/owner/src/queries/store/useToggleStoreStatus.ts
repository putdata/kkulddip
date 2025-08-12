import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  ToggleStoreStatusRequest,
  ToggleStoreStatusResponse,
} from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 가게 활성화/비활성화 상태를 토글하는 뮤테이션 훅
 */
export const useToggleStoreStatus = () => {
  const queryClient = useQueryClient();

  return useMutation<
    ToggleStoreStatusResponse,
    Error,
    { storeId: number; data: ToggleStoreStatusRequest }
  >({
    mutationFn: ({ storeId, data }) =>
      storeService.toggleStoreStatus(storeId, data),
    onSuccess: response => {
      // 가게 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({ queryKey: storeQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: storeQueryKeys.detail(response.storeId.toString()),
      });
    },
  });
};
