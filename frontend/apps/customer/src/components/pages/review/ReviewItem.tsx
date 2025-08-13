import { StarRating } from '@/components/common/StarRating';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { ReviewService } from '@/services/reviewService';
import type { ReviewResponse } from '@/types/review';
import { formatDate } from '@/utils/dateFormat';
import { MessageCircleMore, ThumbsUpIcon } from 'lucide-react';
import { useEffect, useState } from 'react';
// import { toast } from 'sonner';

interface ReviewProps {
  review: ReviewResponse;
}

const ReviewItem = ({ review }: ReviewProps) => {
  // 도움돼요 상태 관리
  const [helpfulCount, setHelpfulCount] = useState(review.helpfulCount);
  const [isHelpful, setIsHelpful] = useState(false); // 사용자가 이미 눌렀는지 여부
  // const [isLoading, setIsLoading] = useState(false);

  // 컴포넌트 마운트 시 도움돼요 상태 확인
  useEffect(() => {
    const checkHelpfulStatus = async () => {
      try {
        const helpful = await ReviewService.checkHelpful(review.reviewId);
        setIsHelpful(helpful);
      } catch (error) {
        console.error('도움돼요 상태 확인 실패:', error);
      }
    };

    checkHelpfulStatus();
  }, [review.reviewId]);

  // 도움돼요 버튼 클릭 핸들러
  // const handleHelpfulClick = async () => {
  //   if (isLoading) {
  //     return;
  //   }

  //   setIsLoading(true);

  //   try {
  //     if (isHelpful) {
  //       await ReviewService.removeHelpful(review.reviewId);
  //       // 이미 눌렀다면 취소
  //       setHelpfulCount(prev => prev - 1);
  //       setIsHelpful(false);
  //       console.log('도움돼요 취소:', review.reviewId);
  //     } else {
  //       // 처음 누르는 경우
  //       setHelpfulCount(prev => prev + 1);
  //       setIsHelpful(true);
  //       console.log('도움돼요 추가:', review.reviewId);

  //       // TODO: API 호출 - 도움돼요 추가
  //       // await ReviewService.addHelpful(review.reviewId);
  //     }
  //   } catch (error) {
  //     console.error('도움돼요 처리 실패:', error);
  //     // 에러 시 상태 롤백

  //     setHelpfulCount(review.helpfulCount);
  //     setIsHelpful(false);
  //     toast.error('처리 중 오류가 발생했습니다.');
  //   } finally {
  //     setIsLoading(false);
  //   }
  // };

  const handleHelpfulClick = () => {
    if (isHelpful) {
      setHelpfulCount(prev => prev - 1);
      setIsHelpful(false);
    } else {
      setHelpfulCount(prev => prev + 1);
      setIsHelpful(true);
    }
  };

  return (
    <Card key={review.reviewId} className="w-full rounded-none bg-white">
      <CardContent className="flex flex-col items-start gap-3 px-5">
        <div className="flex w-full items-start justify-between">
          <div className="inline-flex items-center gap-3">
            <Avatar className="h-10 w-10 bg-amber-500">
              <AvatarFallback className="bg-amber-500 text-sm font-bold text-white">
                {review.userName.charAt(0)}
              </AvatarFallback>
            </Avatar>

            <div className="inline-flex flex-col items-start gap-1">
              <div className="flex items-center gap-2">
                <div className="font-normal text-gray-700">
                  {review.userName}님
                </div>

                {review.images.length >= 1 && (
                  <Badge className="py-0.25 rounded bg-amber-500 px-1 text-xs text-white hover:bg-amber-500">
                    PHOTO
                  </Badge>
                )}
              </div>
              {/* 별점 표시 */}
              <StarRating rating={review.rating} />
            </div>
          </div>
          {/* 리뷰 작성 날짜 */}
          <div className="text-sm font-normal text-gray-400">
            {formatDate(review.createdAt)}
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
                <img src={review.profileImage} alt="" />
              </div>
            ))}
          </div>
        )}

        {/* 도움, 사장님 댓글 */}
        <Separator />
        <div className="flex h-5 w-full items-center justify-end gap-2 text-sm text-gray-400">
          <button
            onClick={handleHelpfulClick}
            className={`flex cursor-pointer items-center gap-1 transition-colors hover:text-amber-500 ${
              isHelpful ? 'text-amber-500' : 'text-gray-400'
            }`}
          >
            <div className="flex">
              <ThumbsUpIcon
                className={`h-5 w-5 ${isHelpful ? 'fill-amber-500' : ''}`}
              />
              <span>도움돼요 {helpfulCount}</span>
            </div>
          </button>
          {/* TODO: 사장님 댓글 여부 확인 및 동작 추가 필요 */}
          <div className="flex items-center gap-1">
            <MessageCircleMore className="h-5" />
            <span>사장님 댓글</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ReviewItem;
