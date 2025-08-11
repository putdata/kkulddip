import ReviewsContainer from '@/components/pages/review/ReviewsContainer';
import ReviewStoreInfoCard from '@/components/pages/review/ReviewStoreInfoCard';
import { mockReviews } from '@/dummies/reviewDummy';
import { mockStoreDetail } from '@/dummies/storeDetailDummy';
import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  const { storeId } = useParams();

  // TODO: 데이터 API 요청 추가 필요
  const reviews = mockReviews[Number(storeId)];
  const store = mockStoreDetail;

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
