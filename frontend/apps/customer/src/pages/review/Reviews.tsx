import ReviewsComponent from '@/components/common/ReviewsComponent';
import { reviewMockData, reviewMockStore } from '@/constants/reviewMockData';
import { useParams } from 'react-router-dom';

const ReviewsPage = () => {
  const reviews = reviewMockData;
  const store = reviewMockStore;

  const params = useParams();

  return (
    <div>
      {params.storeId}
      <ReviewsComponent reviews={reviews} store={store} />;
    </div>
  );
};

export default ReviewsPage;
