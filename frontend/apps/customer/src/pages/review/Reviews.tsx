import ReviewsContainer from '@/components/pages/review/ReviewsContainer';
import ReviewStoreInfoCard from '@/components/pages/review/ReviewStoreInfoCard';
import { useStoreDetail } from '@/hooks/useStoreDetail';
import { useStoreReviews } from '@/hooks/useStoreReviews';
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
    data: reviews = [],
    isLoading: reviewsLoading,
    error: reviewsError,
  } = useStoreReviews(storeId);

  if (storeLoading || reviewsLoading) {
    return <div>로딩 중...</div>;
  }

  // 에러 상태 추가
  if (storeError || reviewsError) {
    return <div>에러가 발생했습니다.</div>;
  }

  // store 데이터가 없는 경우 처리
  if (!store) {
    return <div>가게 정보를 불러올 수 없습니다.</div>;
  }
  const totalReviews = reviews.length;

  console.log(reviews);

  return (
    <div>
      <ReviewStoreInfoCard store={store} totalReviews={totalReviews} />
      <ReviewsContainer reviews={reviews} />
    </div>
  );
};

export default ReviewsPage;
