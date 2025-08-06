import { useState } from 'react';
import ReviewCreateTextInput from '@/components/pages/reviewCreate/ReviewCreateTextInput';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import { Toaster } from '@/components/ui/sonner';
import ReviewCreateHeader from '@/components/pages/reviewCreate/ReviewCreateHeader';
import { reviewCreateMockData } from '@/constants/mockData';
// import { reviewCreateMessages } from '@/constants/messages';

const ReviewCreate = () => {
  // TODO: 일단은 예시 데이터
  const store = reviewCreateMockData;

  // 별점 관련
  const [rating, setRating] = useState<number>(0);

  const onSetRating = (rating: number) => {
    setRating(rating);
  };

  // 리뷰 작성 관련
  const [reviewText, setReviewText] = useState<string>('');

  const onSetReviewText = (reviewText: string) => {
    setReviewText(reviewText);
  };

  // 전송 부분
  // TODO: 요청으로 변경 필요
  const handleSubmit = () => {
    // 먼저 최신값 확보
    const textarea = document.querySelector('textarea');
    const currentValue = textarea?.value || reviewText;

    // 유효성 검사
    if (rating === 0) {
      toast.error('별점을 선택해주세요.');
      return;
    }

    if (currentValue.trim() === '') {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }

    toast(currentValue);
    console.log(currentValue);
  };

  return (
    <div className="flex h-screen flex-col bg-gray-100">
      {/* 리뷰 작성 페이지 상단 */}
      <ReviewCreateHeader
        store={store}
        setRating={onSetRating}
        rating={rating}
      />
      {/* 리뷰 텍스트 입력 컴포넌트에 필요한 props 전달 */}
      <ReviewCreateTextInput
        reviewText={reviewText}
        setReviewText={onSetReviewText}
      />
      <Button onClick={handleSubmit}>등록하기</Button>

      <Toaster position="top-center" />
    </div>
  );
};

export default ReviewCreate;
