import ReviewsComponent from '@/components/common/ReviewsComponent';
import { reviewMockData, reviewMockStore } from '@/constants/reviewMockData';

const ReviewsPage = () => {
  const reviews = reviewMockData;
  const store = reviewMockStore;

  return <ReviewsComponent reviews={reviews} store={store} />;
};

export default ReviewsPage;
