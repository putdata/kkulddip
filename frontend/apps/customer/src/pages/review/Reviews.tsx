import ReviewsContainer from '@/components/pages/review/ReviewsContainer';
import ReviewStoreInfoCard from '@/components/pages/review/ReviewStoreInfoCard';
import { useStoreDetail } from '@/hooks/useStoreDetail';
import { useStoreReviews } from '@/hooks/useStoreReviews';
import { useInfiniteScroll } from '@/hooks/useInfiniteScroll';
import type { ReviewResponse, ReviewListResponse } from '@/types/review';
import { Loader2 } from 'lucide-react';
import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  const params = useParams();
  const storeId = params.storeId!;

  const {
    data: store,
    isLoading: storeLoading,
    error: storeError,
  } = useStoreDetail(storeId);

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetching,
    isFetchingNextPage,
    isLoading: reviewsLoading,
    isError: reviewsError,
  } = useStoreReviews(storeId);

  const { lastElementRef } = useInfiniteScroll({
    isLoading: reviewsLoading,
    isFetchingNextPage,
    hasNextPage,
    isFetching,
    fetchNextPage,
  });

  if (storeLoading || reviewsLoading) {
    return (
      <div className="flex min-h-dvh items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">가게의 리뷰를 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 에러 상태 추가
  if (storeError || reviewsError) {
    return <div>에러가 발생했습니다.</div>;
  }

  // store 데이터가 없는 경우 처리
  if (!store) {
    return <div>가게 정보를 불러올 수 없습니다.</div>;
  }

  // 리뷰 목록 플래튼
  const reviews: ReviewResponse[] =
    data?.pages.flatMap((page: ReviewListResponse) => page.reviewList) ?? [];

  const totalReviews = reviews.length;

  console.log(reviews);

  return (
    <div>
      <ReviewStoreInfoCard store={store} totalReviews={totalReviews} />
      <ReviewsContainer
        reviews={reviews}
        lastElementRef={lastElementRef}
        isFetchingNextPage={isFetchingNextPage}
        hasNextPage={hasNextPage}
      />
    </div>
  );
};

export default ReviewsPage;
