import { likeService } from '@/services/likeService';
import type { LikeParams } from '@/types/likedStore';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const useAddFavorite = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: LikeParams) => likeService.addFavorite(params),

    // 🚀 즉시 UI 업데이트 (낙관적 업데이트)
    onMutate: async params => {
      // 기존 쿼리 취소
      await queryClient.cancelQueries({
        queryKey: ['favorite-check', params.customerId, params.storeId],
      });

      // 이전 데이터 백업
      const previousData = queryClient.getQueryData([
        'favorite-check',
        params.customerId,
        params.storeId,
      ]);

      // 즉시 UI 업데이트 (찜 상태를 true로)
      queryClient.setQueryData(
        ['favorite-check', params.customerId, params.storeId],
        { success: true, status: 200, body: true },
      );

      return { previousData };
    },

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['likes'] });
      queryClient.invalidateQueries({ queryKey: ['favorite-check'] });
    },

    // 실패시 이전 상태로 롤백
    onError: (err, params, context) => {
      if (context?.previousData) {
        queryClient.setQueryData(
          ['favorite-check', params.customerId, params.storeId],
          context.previousData,
        );
      }
    },
  });
};
