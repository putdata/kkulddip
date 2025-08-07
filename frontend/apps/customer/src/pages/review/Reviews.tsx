import ReviewsContainer from '@/components/pages/review/ReviewsContainer';
import ReviewStoreInfoCard from '@/components/pages/review/ReviewStoreInfoCard';
import { reviewMockData, reviewMockStore } from '@/constants/reviewMockData';
// import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  // const params = useParams();

  // TODO: 데이터 API 요청 추가 필요
  const reviews = reviewMockData;
  const store = reviewMockStore;

  const totalReviews = reviews.length;

  return (
    <div>
      <ReviewStoreInfoCard store={store} totalReviews={totalReviews} />
      <ReviewsContainer reviews={reviews} />
    </div>
  );
};

export default ReviewsPage;
