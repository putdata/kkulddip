import { StarRating } from '@/components/common/StarRating';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { ReviewService } from '@/services/reviewService';
import type { ReviewResponse } from '@/types/review';
import { formatDate } from '@/utils/dateFormat';
import { MessageCircleMore, ThumbsUpIcon } from 'lucide-react';
import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query'; // ✅ react-query 훅 추가

interface ReviewProps {
  review: ReviewResponse;
}

const ReviewItem = ({ review }: ReviewProps) => {
  // ✅ 초기 상태를 props 값 기반으로 설정
  const [helpfulCount, setHelpfulCount] = useState(review.helpfulCount);
  const [isHelpful, setIsHelpful] = useState(review.isHelpful);
  const queryClient = useQueryClient();

  const addHelpfulMutation = useMutation({
    mutationFn: () => ReviewService.addHelpful(review.reviewId.toString()),
    onSuccess: () => {
      // 요청 성공 시, 최신 리뷰 목록 다시 불러오기
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });

  const removeHelpfulMutation = useMutation({
    mutationFn: () => ReviewService.removeHelpful(review.reviewId.toString()),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });

  const handleHelpfulClick = async () => {
    // if (addHelpfulMutation.isLoading || removeHelpfulMutation.isLoading) {return};

    if (isHelpful) {
      // 즉시 UI 업데이트
      setHelpfulCount(prev => prev - 1);
      setIsHelpful(false);
      removeHelpfulMutation.mutate(); // API 호출
    } else {
      setHelpfulCount(prev => prev + 1);
      setIsHelpful(true);
      addHelpfulMutation.mutate(); // API 호출
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
                {/* ✅ 프로필 이미지 대신 리뷰 이미지 사용 */}
                <img src={image.imageUrl} alt={image.originalName} />
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
          <div className="gap flex items-center">
            <MessageCircleMore className="h-5" />
            <span>사장님 댓글</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ReviewItem;
