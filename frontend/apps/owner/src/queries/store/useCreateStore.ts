import { useMutation, useQueryClient } from '@tanstack/react-query';
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
    },
  });
};
