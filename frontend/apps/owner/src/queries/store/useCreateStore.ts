import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type { CreateStoreRequest, CreateStoreResponse } from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 새로운 가게를 등록하는 뮤테이션 훅
 */
export const useCreateStore = () => {
  const queryClient = useQueryClient();

  return useMutation<CreateStoreResponse, Error, CreateStoreRequest>({
    mutationFn: storeService.createStore,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: storeQueryKeys.lists() });
      toast.success('새로운 가게가 성공적으로 등록되었습니다.');
    },
    onError: () => {
      toast.error('가게 등록 중 오류가 발생했습니다.');
    },
  });
};
