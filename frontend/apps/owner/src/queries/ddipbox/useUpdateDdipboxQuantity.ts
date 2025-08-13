import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type {
  UpdateDdipBoxQuantityRequest,
  UpdateDdipBoxQuantityResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 재고 수량을 업데이트하는 뮤테이션 훅
 */
export const useUpdateDdipboxQuantity = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateDdipBoxQuantityResponse,
    Error,
    { storeId: number; ddipboxId: number; data: UpdateDdipBoxQuantityRequest }
  >({
    mutationFn: ({ storeId, ddipboxId, data }) =>
      ddipboxService.updateDdipboxQuantity(storeId, ddipboxId, data),
    onSuccess: response => {
      // 딥박스 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.list(response.storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.detail(response.ddipboxId),
      });
      toast.success('재고 수량이 성공적으로 업데이트되었습니다.');
    },
    onError: () => {
      toast.error('재고 업데이트 중 오류가 발생했습니다.');
    },
  });
};
