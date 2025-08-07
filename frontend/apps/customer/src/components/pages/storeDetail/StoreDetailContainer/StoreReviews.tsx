import ReviewsContainer from '../../review/ReviewsContainer';

export const StoreReviews = () => {
  return (
    <div className="h-10 w-full bg-white">
      <ReviewsContainer reviews={[]} />
    </div>
  );
};
