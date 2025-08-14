import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
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
      toast.success('가게 정보가 성공적으로 수정되었습니다.');
    },
    onError: (error: Error) => {
      toast.error(`가게 정보 수정에 실패했습니다: ${error.message}`);
    },
  });
};
