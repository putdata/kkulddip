import { useQuery } from '@tanstack/react-query';
import { likeService } from '@/services/likeService';
import type { LikeParams } from '@/types/likedStore'; // import 추가

export const useCheckFavorite = (customerId: number, storeId: number) => {
  return useQuery({
    queryKey: ['favorite-check', customerId, storeId],
    queryFn: async () => {
      const params: LikeParams = {
        customerId,
        storeId,
      };
      return likeService.checkIsFavorite(params);
    },
    enabled: Boolean(customerId) && Boolean(storeId),
  });
};
