import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type { ConfirmOrderRequest, ConfirmOrderResponse } from '@/types/order';
import { orderService } from '@/services/orderService';
import { orderQueryKeys } from './orderQueryKeys';

interface UseConfirmOrderParams {
  orderId: string;
  storeId: number;
}

/**
 * 주문을 확정하거나 거절하는 뮤테이션 훅
 * 
 * @param orderId - 주문 ID
 * @param storeId - 매장 ID  
 * @returns 주문 확정/거절 뮤테이션 객체
 */
export const useConfirmOrder = ({
  orderId,
  storeId,
}: UseConfirmOrderParams) => {
  const queryClient = useQueryClient();

  return useMutation<ConfirmOrderResponse, Error, ConfirmOrderRequest>({
    mutationFn: data => orderService.confirmOrder(orderId, data),
    onSuccess: (_, variables) => {
      // 대기 주문 목록 갱신
      queryClient.invalidateQueries({
        queryKey: orderQueryKeys.pendingByStore(storeId),
      });

      // 주문 확정/거절 시 주문 내역도 갱신 (새로운 주문이 내역에 추가됨)
      queryClient.invalidateQueries({
        queryKey: orderQueryKeys.storeHistoryByStore(storeId),
      });

      if (variables.action === 'CONFIRM') {
        toast.success('주문이 확정되었습니다.');
      } else {
        toast.success('주문이 거절되었습니다.');
      }
    },
    onError: (_, variables) => {
      if (variables.action === 'CONFIRM') {
        toast.error('주문 확정 중 오류가 발생했습니다.');
      } else {
        toast.error('주문 거절 중 오류가 발생했습니다.');
      }
    },
  });
};
