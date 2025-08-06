import ReviewsComponent from '@/components/pages/review/ReviewsContainer';
import StoreInfo from '@/components/pages/review/StoreInfo';
import { reviewMockData, reviewMockStore } from '@/constants/reviewMockData';
// import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  const reviews = reviewMockData;
  const store = reviewMockStore;

  // const params = useParams();

  const totalReviews = reviews.length;

  return (
    <div>
      <StoreInfo store={store} totalReviews={totalReviews} />
      <ReviewsComponent reviews={reviews} store={store} />
    </div>
  );
};

export default ReviewsPage;
