import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { UpdateStoreRequest, UpdateStoreResponse } from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 가게 정보를 수정하는 뮤테이션 훅
 */
export const useUpdateStore = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateStoreResponse,
    Error,
    { storeId: number; data: UpdateStoreRequest }
  >({
    mutationFn: ({ storeId, data }) => storeService.updateStore(storeId, data),
    onSuccess: response => {
      // 가게 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({ queryKey: storeQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: storeQueryKeys.detail(response.storeId.toString()),
      });
    },
  });
};
