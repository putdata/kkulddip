import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';

import { reviewCreateMockData } from '@/constants/mockData';

import ReviewCreateHeader from '@/components/pages/reviewCreate/ReviewCreateHeader';
import ReviewCreatePhotoInput from '@/components/pages/reviewCreate/ReviewCreatePhotoInput';
import ReviewCreateTextInput from '@/components/pages/reviewCreate/ReviewCreateTextInput';

interface ReviewFormState {
  rating: number;
  reviewText: string;
  selectedImages: File[];
  imagePreviewUrls: string[];
}

const ReviewCreate = () => {
  // TODO: 일단은 예시 데이터
  const store = reviewCreateMockData;

  const [reviewForm, setReviewForm] = useState<ReviewFormState>({
    rating: 0,
    reviewText: '',
    selectedImages: [],
    imagePreviewUrls: [],
  });

  const onSetRating = (rating: number) => {
    setReviewForm({
      ...reviewForm,
      rating: rating,
    });
  };

  const onSetReviewText = (reviewText: string) => {
    setReviewForm({
      ...reviewForm,
      reviewText: reviewText,
    });
  };

  const onImagesChange = (selectedImages: File[]) => {
    setReviewForm({
      ...reviewForm,
      selectedImages: selectedImages,
    });
  };

  const onPreviewUrlsChange = (imagePreviewUrls: string[]) => {
    setReviewForm({
      ...reviewForm,
      imagePreviewUrls: imagePreviewUrls,
    });
  };

  // 전송 부분
  // TODO: 요청으로 변경 필요
  const handleSubmit = () => {
    // 먼저 최신값 확보
    const textarea = document.querySelector('textarea');
    const currentValue = textarea?.value || reviewForm.reviewText;

    // 유효성 검사
    if (reviewForm.rating === 0) {
      toast.error('별점을 선택해주세요.');
      return;
    }

    if (currentValue.trim() === '') {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }

    toast(
      `별점: ${reviewForm.rating}\n리뷰: ${currentValue}\n사진 개수:${reviewForm.imagePreviewUrls.length}`,
    );
    console.log(currentValue);
  };

  return (
    <div className="flex h-screen flex-col items-center gap-5 bg-gray-100 p-5">
      {/* 리뷰 작성 페이지 상단 */}
      <ReviewCreateHeader
        store={store}
        setRating={onSetRating}
        rating={reviewForm.rating}
      />
      {/* 리뷰 텍스트 입력 컴포넌트에 필요한 props 전달 */}
      <ReviewCreateTextInput
        reviewText={reviewForm.reviewText}
        setReviewText={onSetReviewText}
      />
      <ReviewCreatePhotoInput
        selectedImages={reviewForm.selectedImages}
        imagePreviewUrls={reviewForm.imagePreviewUrls}
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
