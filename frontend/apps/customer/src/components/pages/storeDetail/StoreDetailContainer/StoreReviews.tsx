import { reviewMockData } from '@/constants/reviewMockData';
import ReviewItem from '../../review/ReviewItem';

export const StoreReviews = () => {
  const review = reviewMockData[0];
  if (!review) {
    return <div>리뷰가 없습니다.</div>;
  }
  return (
    <div className="h-10 w-full bg-white">
      <ReviewItem review={review} />
    </div>
  );
};
