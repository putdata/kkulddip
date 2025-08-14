import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { orderService } from '@/services/orderService';
import { orderQueryKeys } from './orderQueryKeys';
import type { PickupOrderResponse } from '@/types/order';

/**
 * 주문 픽업 완료를 처리하는 뮤테이션 훅
 *
 * @param storeId - 매장 ID (캐시 무효화를 위해 사용)
 * @returns 픽업 완료 뮤테이션 객체
 */
export const usePickupOrder = (storeId?: number) => {
  const queryClient = useQueryClient();

  return useMutation<PickupOrderResponse, Error, string>({
    mutationFn: (orderId: string) => orderService.pickupOrder(orderId),
    onSuccess: () => {
      toast.success('픽업이 완료되었습니다.');

      // 캐시 무효화하여 서버에서 최신 데이터 가져오기
      if (storeId) {
        queryClient.invalidateQueries({
          queryKey: orderQueryKeys.storeHistoryByStore(storeId),
        });
      }
    },
    onError: error => {
      toast.error('픽업 완료 처리에 실패했습니다.');
      console.error('주문 픽업 완료 실패:', error);
    },
  });
};
