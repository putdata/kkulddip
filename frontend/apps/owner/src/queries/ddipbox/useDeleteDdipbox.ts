import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스를 삭제하는 뮤테이션 훅
 */
export const useDeleteDdipbox = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, { storeId: number; ddipboxId: number }>({
    mutationFn: ({ storeId, ddipboxId }) =>
      ddipboxService.deleteDdipbox(storeId, ddipboxId),
    onSuccess: (_, { ddipboxId, storeId }) => {
      // 딥박스 목록을 새로고침하고 해당 딥박스의 상세 정보 캐시 제거
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.list(storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.removeQueries({
        queryKey: ddipboxQueryKeys.detail(ddipboxId),
      });
      toast.success('띱박스가 성공적으로 삭제되었습니다.');
    },
    onError: () => {
      toast.error('띱박스 삭제 중 오류가 발생했습니다.');
    },
  });
};
