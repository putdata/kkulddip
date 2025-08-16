import { ReviewService, type ReviewListParams } from '@/services/reviewService';
import type { ReviewListResponse } from '@/types/review';
import { useInfiniteQuery } from '@tanstack/react-query';

export const useStoreReviews = (storeId: string) => {
  return useInfiniteQuery({
    queryKey: ['store', storeId, 'reviews'],
    queryFn: ({ pageParam }) => {
      const params: ReviewListParams = {
        size: 10,
        cursor: pageParam as string | undefined,
        sort: 'createdAt',
      };
      return ReviewService.getReviewsWithPagination(storeId, params);
    },
    getNextPageParam: (lastPage: ReviewListResponse) => {
      return lastPage.hasNext ? lastPage.cursor : undefined;
    },
    initialPageParam: undefined,
    staleTime: 5 * 60 * 1000, // 5분
  });
};
