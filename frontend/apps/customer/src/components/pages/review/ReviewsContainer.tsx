import { Button } from '@/components/ui/button';

import { useState } from 'react';
import ReviewItem from './ReviewItem';

interface ReviewsComponentProps {
  reviews: {
    id: number;
    user: {
      name: string;
      usageCount: number;
    };
    rating: number;
    content: string;
    images: { emoji: string }[];
    createDate: string;
  }[];
}

const ReviewsContainer = ({ reviews }: ReviewsComponentProps) => {
  const [visibleReviewsCount, setVisibleReviewsCount] = useState(5); // 처음에 5개만 보여주기
  const reviewsPerPage = 5; // 더보기 클릭 시 추가로 보여줄 개수
  const totalReviews = reviews.length;
  const remainingReviews = totalReviews - visibleReviewsCount;
  const displayedReviews = reviews.slice(0, visibleReviewsCount);

  // 핸들러
  const handleLoadMore = () => {
    setVisibleReviewsCount(prev =>
      Math.min(prev + reviewsPerPage, totalReviews),
    );
  };

  return (
    // TODO: [font-family:segoe_ui] 폰트 스타일 전역으로 이동
    <div className="flex w-full flex-col items-start gap-2 bg-gray-100 [font-family:segoe_ui]">
      {/* 리뷰 리스트 렌더링 */}
      {displayedReviews.map(review => (
        <ReviewItem review={review} />
      ))}
      {/* 더보기 버튼 */}
      {remainingReviews > 0 && (
        <div className="flex w-full items-center justify-center pb-2.5">
          <Button
            variant="outline"
            className="h-14 w-2/3 cursor-pointer rounded-xl border border-gray-200 bg-white text-gray-500"
            onClick={handleLoadMore}
          >
            리뷰 더보기 ({remainingReviews}개 남음)
          </Button>
        </div>
      )}
    </div>
  );
};

export default ReviewsContainer;
