import { ReviewService } from '@/services/reviewService';
import { useQuery } from '@tanstack/react-query';

export const useStoreReviews = (storeId: string) => {
  return useQuery({
    queryKey: ['store', storeId, 'reviews'],
    queryFn: () => ReviewService.getReviews(storeId),
  });
};
