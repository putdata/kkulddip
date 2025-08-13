import ReviewsContainer from '@/components/pages/review/ReviewsContainer';
import ReviewStoreInfoCard from '@/components/pages/review/ReviewStoreInfoCard';
import { mockReviews } from '@/dummies/reviewDummy';
import { useStoreDetail } from '@/hooks/useStoreDetail';
import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  const params = useParams();
  const storeId = params.storeId!;

  // TODO: 데이터 API 요청 추가 필요
  const reviews = mockReviews[Number(storeId)];

  const {
    data: store,
    isLoading: storeLoading,
    error: storeError,
  } = useStoreDetail(storeId);

  // 둘 중 하나라도 로딩 중이면 로딩 표시
  if (storeLoading) {
    return <div>로딩 중...</div>;
  }

  // 에러 처리
  if (storeError || !store) {
    return <div>에러가 발생했습니다.</div>;
  }

  if (reviews) {
    const totalReviews = reviews.length;

    return (
      <div>
        <ReviewStoreInfoCard store={store} totalReviews={totalReviews} />
        <ReviewsContainer reviews={reviews} />
      </div>
    );
  }
};

export default ReviewsPage;
