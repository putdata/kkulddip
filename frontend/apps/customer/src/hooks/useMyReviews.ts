import { useQuery } from '@tanstack/react-query';
import { ReviewService } from '@/services/reviewService'; // service에서 import

// React Query 훅
export const useMyReviews = () => {
  return useQuery({
    queryKey: ['myReviews'],
    queryFn: ReviewService.getMyReviews,
    staleTime: 5 * 60 * 1000, // 5분간 캐시 유지
    gcTime: 10 * 60 * 1000, // 10분간 가비지 컬렉션 방지
  });
};
