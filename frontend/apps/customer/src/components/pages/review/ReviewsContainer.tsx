import ReviewItem from './ReviewItem';
import type { ReviewResponse } from '@/types/review';
import { Loader2 } from 'lucide-react';

interface ReviewsComponentProps {
  reviews: ReviewResponse[];
  lastElementRef?: (node: HTMLDivElement | null) => void;
  isFetchingNextPage?: boolean;
  hasNextPage?: boolean;
}

const ReviewsContainer = ({
  reviews,
  lastElementRef,
  isFetchingNextPage,
  hasNextPage,
}: ReviewsComponentProps) => {
  if (reviews.length === 0) {
    return (
      <div className="flex w-full flex-col items-center justify-center space-y-4 bg-gray-100 py-12">
        <div className="text-4xl">📝</div>
        <div className="text-center">
          <h3 className="mb-2 text-lg font-semibold text-gray-800">
            아직 등록된 리뷰가 없어요
          </h3>
          <p className="text-sm text-gray-500">
            이 가게의 첫 번째 리뷰를 남겨보세요!
          </p>
        </div>
      </div>
    );
  }

  return (
    // TODO: [font-family:segoe_ui] 폰트 스타일 전역으로 이동
    <div className="flex w-full flex-col gap-2 bg-gray-100">
      {/* 리뷰 리스트 렌더링 */}
      {reviews.map((review, index) => (
        <div
          key={`${review.reviewId}-${index}`}
          ref={index === reviews.length - 1 ? lastElementRef : null}
        >
          <ReviewItem review={review} />
        </div>
      ))}

      {/* 로딩 인디케이터 */}
      {isFetchingNextPage && (
        <div className="flex w-full justify-center py-4">
          <Loader2 className="h-6 w-6 animate-spin text-amber-600" />
        </div>
      )}

      {/* 더 이상 로드할 데이터가 없을 때 */}
      {!hasNextPage && reviews.length > 0 && (
        <div className="flex w-full justify-center py-8">
          <p className="text-sm text-gray-500">모든 리뷰를 불러왔어요</p>
        </div>
      )}
    </div>
  );
};

export default ReviewsContainer;
