import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 가게를 삭제하는 뮤테이션 훅
 */
export const useDeleteStore = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, number>({
    mutationFn: storeService.deleteStore,
    onSuccess: (_, storeId) => {
      // 가게 목록을 새로고침하고 해당 가게의 상세 정보 캐시 제거
      queryClient.invalidateQueries({ queryKey: storeQueryKeys.lists() });
      queryClient.removeQueries({
        queryKey: storeQueryKeys.detail(storeId.toString()),
      });
      toast.success('가게가 성공적으로 삭제되었습니다.');
    },
    onError: (error: Error) => {
      toast.error(`가게 삭제에 실패했습니다: ${error.message}`);
    },
  });
};
