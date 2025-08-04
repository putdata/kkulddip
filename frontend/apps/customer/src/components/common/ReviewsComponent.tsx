import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardTitle } from '@/components/ui/card';
import { Star } from 'lucide-react';

import { reviewMessages } from '@/constants/reviewMessages';

import { useState } from 'react';

interface ReviewsComponentProps {
  store: {
    id: number;
    storeName: string;
    avgRate: number;
  };
  reviews: Array<{
    id: number;
    user: {
      name: string;
      usageCount: number;
    };
    rating: number;
    content: string;
    images: Array<{ emoji: string }>;
    createDate: string;
  }>;
}

const ReviewsComponent = ({ reviews, store }: ReviewsComponentProps) => {
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
      {/* 가게 정보 카드 */}
      <Card key={store.id} className="w-full gap-2 rounded-none bg-white p-4">
        <CardTitle>{store.storeName}</CardTitle>
        <div className="flex items-center gap-1">
          {/* 평점 */}
          <div className="flex items-center justify-start gap-2 text-lg font-bold text-amber-500">
            {store.avgRate}
          </div>
          {/* 별점 표시 */}
          <div className="flex items-center gap-0.5">
            {[1, 2, 3, 4, 5].map(star => (
              <Star
                key={star}
                className={`h-4 w-4 ${
                  star <= store.avgRate
                    ? 'fill-amber-500 text-amber-500'
                    : 'fill-gray-200 text-gray-200'
                }`}
              />
            ))}
          </div>
          <div className="text-xs text-gray-400">총 {totalReviews}개 리뷰</div>
        </div>
      </Card>

      {/* 리뷰 리스트 렌더링 */}
      {displayedReviews.map(review => (
        <Card key={review.id} className="w-full rounded-none bg-white">
          <CardContent className="flex flex-col items-start gap-3 px-5">
            <div className="flex w-full items-start justify-between">
              <div className="inline-flex items-center gap-3">
                {/* TODO: 유저 프로필 사진 어떻게 할 것 인지 논의 필요... */}
                <Avatar className="h-10 w-10 bg-amber-500">
                  <AvatarFallback className="bg-amber-500 text-sm font-bold text-white">
                    {review.user.name.charAt(0)}
                  </AvatarFallback>
                </Avatar>

                <div className="inline-flex flex-col items-start gap-1">
                  <div className="flex items-center gap-2">
                    <div className="font-normal text-gray-700">
                      {/* TODO: 이름은 맨 첫 글자만 표시되고 그 뒤는 *처리 했는데, 어떤지?  */}
                      {review.user.name.charAt(0) +
                        '*'.repeat(review.user.name.length - 1)}
                      님
                    </div>

                    {/* 사진 있을때만 PHOTO 뱃지 표시 */}
                    {review.images.length >= 1 && (
                      <Badge className="py-0.25 rounded bg-amber-500 px-1 text-xs text-white hover:bg-amber-500">
                        PHOTO
                      </Badge>
                    )}
                  </div>
                  {/* 별점 표시 */}
                  <div className="flex items-center">
                    {[1, 2, 3, 4, 5].map(star => (
                      <Star
                        key={star}
                        className={`h-3 w-3 ${
                          star <= review.rating
                            ? 'fill-amber-500 text-amber-500'
                            : 'fill-gray-200 text-gray-200'
                        }`}
                      />
                    ))}
                  </div>
                </div>
              </div>
              {/* 리뷰 작성 날짜 */}
              <div className="text-sm font-normal text-gray-400">
                {review.createDate}
              </div>
            </div>

            {/* 리뷰 내용 */}
            <div className="flex w-full flex-col items-start">
              <div className="whitespace-pre-wrap text-sm text-gray-500">
                {review.content}
              </div>
            </div>

            {/* 이미지들 */}
            {review.images.length > 0 && (
              <div className="flex w-full gap-2">
                {review.images.map((image, index) => (
                  <div
                    key={index}
                    className="flex h-20 w-20 items-center justify-center overflow-hidden rounded bg-gray-100"
                  >
                    <div className="text-2xl">{image.emoji}</div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      ))}

      {/* 더보기 버튼 */}
      {remainingReviews > 0 && (
        <div className="flex w-full items-center justify-center pb-2.5">
          <Button
            variant="outline"
            className="h-14 w-2/3 cursor-pointer rounded-xl border border-gray-200 bg-white text-gray-500"
            onClick={handleLoadMore}
          >
            {reviewMessages.moreReviews(remainingReviews)}
          </Button>
        </div>
      )}
    </div>
  );
};

export default ReviewsComponent;
