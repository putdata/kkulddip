import { StarRating } from '@/components/common/StarRating';
import { Card, CardContent } from '@/components/ui/card';

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

interface ReviewProps {
  review: Review;
}

const StoreReviewItem = ({ review }: ReviewProps) => {
  return (
    <Card key={review.id} className="w-full rounded-none bg-white">
      <CardContent className="flex flex-col items-start gap-3 px-5">
        <div className="flex w-full items-start justify-between">
          <div className="inline-flex items-center gap-3">
            {/* TODO: 유저 프로필 사진 어떻게 할 것 인지 논의 필요... */}

            <div className="inline-flex flex-col items-start gap-1">
              <div className="flex items-center gap-2">
                <div className="font-normal text-gray-700">
                  {review.user.name}님
                </div>
              </div>
              {/* 별점 표시 */}
              <StarRating rating={review.rating} />
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
      </CardContent>
    </Card>
  );
};

export default StoreReviewItem;
