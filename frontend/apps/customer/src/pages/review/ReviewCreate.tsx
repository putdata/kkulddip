import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';
import { Toaster } from '@/components/ui/sonner';

import { reviewCreateMockData } from '@/constants/mockData';

import ReviewCreateHeader from '@/components/pages/reviewCreate/ReviewCreateHeader';
import ReviewCreatePhotoInput from '@/components/pages/reviewCreate/ReviewCreatePhotoInput';
import ReviewCreateTextInput from '@/components/pages/reviewCreate/ReviewCreateTextInput';

const ReviewCreate = () => {
  // TODO: 일단은 예시 데이터
  const store = reviewCreateMockData;

  // 별점 관련
  const [rating, setRating] = useState<number>(0);
  // 리뷰 작성 관련
  const [reviewText, setReviewText] = useState<string>('');
  // 사진 추가 관련
  const [selectedImages, setSelectedImages] = useState<File[]>([]);
  const [imagePreviewUrls, setImagePreviewUrls] = useState<string[]>([]);

  const onSetReviewText = (reviewText: string) => {
    setReviewText(reviewText);
  };
  const onSetRating = (rating: number) => {
    setRating(rating);
  };
  const onImagesChange = (selectedImages: File[]) => {
    setSelectedImages(selectedImages);
  };

  const onPreviewUrlsChange = (imagePreviewUrls: string[]) => {
    setImagePreviewUrls(imagePreviewUrls);
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

    toast(
      `별점: ${rating}\n리뷰: ${currentValue}\n사진 개수:${imagePreviewUrls.length}`,
    );
    console.log(currentValue);
  };

  return (
    <div className="flex h-screen flex-col items-center gap-5 bg-gray-100 p-5">
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
      <ReviewCreatePhotoInput
        selectedImages={selectedImages}
        imagePreviewUrls={imagePreviewUrls}
        setSelectedImages={onImagesChange}
        setImagePreviewUrls={onPreviewUrlsChange}
      />
      <Button
        onClick={handleSubmit}
        className="w-50 flex h-10 cursor-pointer bg-amber-500 text-center text-lg font-bold text-white [font-family:Helvetica] hover:bg-amber-600"
      >
        등록하기
      </Button>
    </div>
  );
};

export default ReviewCreate;
