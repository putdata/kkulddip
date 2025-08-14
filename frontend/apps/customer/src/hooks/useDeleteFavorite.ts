import { likeService } from '@/services/likeService';
import type { LikeParams } from '@/types/likedStore';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const useDeleteFavorite = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: LikeParams) => likeService.deleteFavorite(params),

    // 🚀 즉시 UI 업데이트 (낙관적 업데이트)
    onMutate: async params => {
      await queryClient.cancelQueries({
        queryKey: ['favorite-check', params.customerId, params.storeId],
      });

      const previousData = queryClient.getQueryData([
        'favorite-check',
        params.customerId,
        params.storeId,
      ]);

      // 즉시 UI 업데이트 (찜 상태를 false로)
      queryClient.setQueryData(
        ['favorite-check', params.customerId, params.storeId],
        { success: true, status: 200, body: false },
      );

      return { previousData };
    },

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['likes'] });
      queryClient.invalidateQueries({ queryKey: ['favorite-check'] });
    },

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
