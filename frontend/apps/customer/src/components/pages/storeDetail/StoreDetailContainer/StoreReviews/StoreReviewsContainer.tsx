import StoreReviewItem from './StoreReviewItem';

interface Review {
  id: number;
  user: {
    name: string;
    usageCount: number;
  };
  rating: number;
  content: string;
  //   TODO: 이미지 경로로 수정 필요
  images: { emoji: string }[];
  createDate: string;
}

interface StoreReviewsProps {
  reviews: Review[];
}

export const StoreReviewsContainer = ({ reviews }: StoreReviewsProps) => {
  const getLatestReviews = (reviewList: Review[], count: number = 3) => {
    const latestReviews = reviewList
      .sort(
        (a, b) =>
          new Date(b.createDate).getTime() - new Date(a.createDate).getTime(),
      )
      .slice(0, count);

    return {
      latestReviews,
      hasReviews: latestReviews.length > 0,
      totalCount: reviewList.length,
    };
  };

  const { latestReviews, hasReviews, totalCount } = getLatestReviews(
    reviews,
    3,
  );

  if (!hasReviews) {
    return <div>리뷰가 없습니다.</div>;
  }

  return (
    <div className="flex flex-col items-start justify-start gap-2 bg-white p-5">
      <div className="flex w-full items-baseline justify-between">
        <h2 className="text text-lg font-bold">리뷰</h2>
        <div className="mb-2 text-sm text-gray-500">(총 {totalCount}개)</div>
      </div>
      {latestReviews.map(review => (
        <StoreReviewItem key={review.id} review={review} />
      ))}
    </div>
  );
};
