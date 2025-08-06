import { useState } from 'react';
import ReviewCreateTextInput from '@/components/pages/reviewCreate/ReviewCreateTextInput';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import { Toaster } from '@/components/ui/sonner';

const ReviewCreate = () => {
  // 리뷰 작성 관련 state들
  const [reviewText, setReviewText] = useState('');

  function onSetReviewText(reviewText: string) {
    setReviewText(reviewText);
  }

  // 임시
  const handleSubmit = () => {
    // 먼저 최신값 확보
    const textarea = document.querySelector('textarea');
    const currentValue = textarea?.value || reviewText;

    // 유효성 검사
    if (currentValue.trim() === '') {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }

    // 전송 부분
    toast(currentValue);
    console.log(currentValue);
  };

  return (
    <div className="flex h-screen flex-col bg-gray-100">
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
