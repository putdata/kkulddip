import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type {
  UpdateStoreStatusRequest,
  UpdateStoreStatusResponse,
} from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 가게 활성화/비활성화 상태를 토글하는 뮤테이션 훅
 */
export const useToggleStoreStatus = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateStoreStatusResponse,
    Error,
    { storeId: number; data: UpdateStoreStatusRequest }
  >({
    mutationFn: ({ storeId, data }) =>
      storeService.toggleStoreStatus(storeId, data),
    onSuccess: response => {
      // 가게 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({ queryKey: storeQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: storeQueryKeys.detail(response.storeId.toString()),
      });
      const statusText = response.isActive ? '활성화' : '비활성화';
      toast.success(`가게가 성공적으로 ${statusText}되었습니다.`);
    },
    onError: (error: Error) => {
      toast.error(`가게 상태 변경에 실패했습니다: ${error.message}`);
    },
  });
};
