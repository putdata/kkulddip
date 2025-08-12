import { useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  CreateDdipBoxRequest,
  CreateDdipBoxResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 새로운 딥박스를 등록하는 뮤테이션 훅
 */
export const useCreateDdipbox = () => {
  const queryClient = useQueryClient();

  return useMutation<
    CreateDdipBoxResponse,
    Error,
    { storeId: number; data: CreateDdipBoxRequest }
  >({
    mutationFn: ({ storeId, data }) =>
      ddipboxService.createDdipbox(storeId, data),
    onSuccess: response => {
      // 해당 가게의 딥박스 목록을 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.store(response.storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
    },
  });
};
