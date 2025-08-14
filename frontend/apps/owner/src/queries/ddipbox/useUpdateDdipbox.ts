import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type {
  UpdateDdipBoxRequest,
  UpdateDdipBoxResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 정보를 수정하는 뮤테이션 훅
 */
export const useUpdateDdipbox = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateDdipBoxResponse,
    Error,
    { storeId: number; ddipboxId: number; data: UpdateDdipBoxRequest }
  >({
    mutationFn: ({ storeId, ddipboxId, data }) =>
      ddipboxService.updateDdipbox(storeId, ddipboxId, data),
    onSuccess: response => {
      // 딥박스 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.list(response.storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.detail(response.ddipboxId),
      });
      toast.success('띱박스 정보가 성공적으로 수정되었습니다.');
    },
    onError: () => {
      toast.error('띱박스 수정 중 오류가 발생했습니다.');
    },
  });
};
