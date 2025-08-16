import type { ReviewListResponse } from '@/types/review';
import { Button } from '@/components/ui/button'; // Button 컴포넌트 import 추가
import StoreReviewItem from './StoreReviewItem';
import { generatePath, useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

interface StoreReviewsProps {
  storeId: string;
  reviewResponse?: ReviewListResponse;
  reviewTotalCount: number;
}

export const StoreReviewsContainer = ({
  storeId,
  reviewResponse,
  reviewTotalCount,
}: StoreReviewsProps) => {
  const reviews = reviewResponse?.reviewList || [];

  const navigate = useNavigate();

  const handleLoadAllReviews = () => {
    // navigate(ROUTE_PATH.REVIEW.replace(':storeId', storeId.toString()));
    navigate(generatePath(ROUTE_PATH.REVIEW, { storeId }));
  };

  // TODO: 리뷰 없을 경우 문구 수정 필요
  return (
    <div className="flex flex-col items-start justify-start gap-2 p-5 pb-0">
      <div className="flex w-full items-baseline justify-between">
        <h2 className="text text-lg font-bold">리뷰</h2>
        <div className="mb-2 text-sm text-gray-500">
          (총 {reviewTotalCount}개)
        </div>
      </div>
      <div className="flex w-full flex-col items-start gap-2 bg-gray-100">
        {/* 리뷰 리스트 렌더링 (최대 3개) */}
        {reviews?.slice(0, 3).map((review, index) => (
          <StoreReviewItem key={index} review={review} />
        ))}
        {/* 더보기 버튼 - 총 리뷰가 3개보다 많을 때만 표시 */}
        {reviewTotalCount > 3 && (
          <div className="flex w-full items-center justify-center pb-2.5">
            <Button
              variant="outline"
              className="h-14 w-2/3 cursor-pointer rounded-xl border border-gray-200 bg-white text-gray-500"
              onClick={handleLoadAllReviews}
            >
              모든 리뷰 보기
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};
