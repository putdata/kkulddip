import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type {
  UpdateDdipBoxStatusRequest,
  UpdateDdipBoxStatusResponse,
} from '@/types/ddipbox';
import { ddipboxService } from '@/services/ddipboxService';
import { ddipboxQueryKeys } from './ddipboxQueryKeys';

/**
 * 딥박스 활성화/비활성화 상태를 토글하는 뮤테이션 훅
 */
export const useToggleDdipboxStatus = () => {
  const queryClient = useQueryClient();

  return useMutation<
    UpdateDdipBoxStatusResponse,
    Error,
    { storeId: number; ddipboxId: number; data: UpdateDdipBoxStatusRequest }
  >({
    mutationFn: ({ storeId, ddipboxId, data }) =>
      ddipboxService.toggleDdipboxStatus(storeId, ddipboxId, data),
    onSuccess: (response, { storeId, data }) => {
      // 딥박스 목록과 상세 정보를 새로고침
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.list(storeId),
      });
      queryClient.invalidateQueries({ queryKey: ddipboxQueryKeys.lists() });
      queryClient.invalidateQueries({
        queryKey: ddipboxQueryKeys.detail(response.ddipboxId),
      });

      toast.success(
        data.isActive
          ? '띱박스 판매가 시작되었습니다.'
          : '띱박스 판매가 중지되었습니다.',
      );
    },
    onError: () => {
      toast.error('상태 변경 중 오류가 발생했습니다.');
    },
  });
};
