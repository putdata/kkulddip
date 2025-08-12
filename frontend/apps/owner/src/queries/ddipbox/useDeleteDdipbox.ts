import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스를 삭제하는 뮤테이션 훅
 */
export const useDeleteDdipbox = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, { ddipboxId: number; storeId: number }>({
    mutationFn: ({ ddipboxId }) => ddipboxService.deleteDdipbox(ddipboxId),
    onSuccess: (_, { ddipboxId, storeId }) => {
      // 딥박스 목록을 새로고침하고 해당 딥박스의 상세 정보 캐시 제거
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.store(storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.removeQueries({
        queryKey: ddipboxQueryKeys.detail(ddipboxId),
      });
    },
  });
};
