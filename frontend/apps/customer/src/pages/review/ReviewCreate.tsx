import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';

import ReviewCreateHeader from '@/components/pages/reviewCreate/ReviewCreateHeader';
import ReviewCreatePhotoInput from '@/components/pages/reviewCreate/ReviewCreatePhotoInput';
import ReviewCreateTextInput from '@/components/pages/reviewCreate/ReviewCreateTextInput';
import { useCreateReviewMutation } from '@/services/reviewService';
import type { ReviewCreateRequest } from '@/types/review';
import { useParams } from 'react-router-dom';
import { useStoreDetail } from '@/hooks/useStoreDetail';
import { useCustomerProfile } from '@/hooks/useProfile';

interface ReviewFormState {
  rating: number;
  reviewText: string;
  selectedImages: File[];
  imagePreviewUrls: string[];
}

const ReviewCreate = () => {
  const params = useParams();
  const storeId = params.storeId!;

  // customerId 받기
  const { data: profile } = useCustomerProfile();
  const customerId = profile?.customerId;

  const {
    data: store,
    isLoading: storeLoading,
    error: storeError,
  } = useStoreDetail(storeId);

  const [reviewForm, setReviewForm] = useState<ReviewFormState>({
    rating: 0,
    reviewText: '',
    selectedImages: [],
    imagePreviewUrls: [],
  });

  const createReviewMutation = useCreateReviewMutation();

  const onSetRating = (rating: number) => {
    console.log('Rating updated:', rating); // 디버깅용

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

  const onImagesChange = (
    selectedImages: File[],
    imagePreviewUrls: string[],
  ) => {
    setReviewForm({
      ...reviewForm,
      selectedImages: selectedImages,
      imagePreviewUrls: imagePreviewUrls,
    });
  };

  // 전송 부분
  const handleSubmit = async () => {
    // 유효성 검사
    if (reviewForm.rating === 0) {
      toast.error('별점을 선택해주세요.');
      return;
    }

    const trimmedContent = reviewForm.reviewText.trim();
    if (trimmedContent === '') {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }

    const reviewData: ReviewCreateRequest = {
      storeId: storeId,
      customerId: customerId!,
      content: trimmedContent,
      // TODO: 실제 주문 ID로 변경 필요 (주문 연동 시)
      orderId: 119,
      rating: reviewForm.rating,
      images: reviewForm.selectedImages,
    };

    toast(
      `별점: ${reviewForm.rating}\n리뷰: ${reviewForm.reviewText}\n사진 개수:${reviewForm.imagePreviewUrls.length}`,
    );

    console.log('전송할 리뷰 데이터:', {
      ...reviewData,
      images: reviewData.images.map(img => ({
        name: img.name,
        size: img.size,
        type: img.type,
      })),
    });

    try {
      const result = await createReviewMutation.mutateAsync(reviewData);

      toast.success('리뷰가 성공적으로 등록되었습니다! 🎉');

      // 폼 초기화 및 메모리 정리
      reviewForm.imagePreviewUrls.forEach(url => {
        URL.revokeObjectURL(url);
      });

      setReviewForm({
        rating: 0,
        reviewText: '',
        selectedImages: [],
        imagePreviewUrls: [],
      });

      console.log('등록된 리뷰 정보:', {
        reviewId: result.reviewId,
        customerId: result.customerId,
        storeId: result.storeId,
        rating: result.rating,
        content: result.content,
        imageCount: result.images.length,
        createdAt: result.createdAt,
      });
    } catch (error) {
      console.error('리뷰 제출 오류:', error);

      // 에러 타입에 따른 메시지 처리
      if (error instanceof Error) {
        // 네트워크 에러나 서버 에러 구분
        if (error.message.includes('Failed to fetch')) {
          toast.error('네트워크 연결을 확인해주세요.');
        } else if (error.message.includes('400')) {
          toast.error('입력 정보를 다시 확인해주세요.');
        } else if (error.message.includes('401')) {
          toast.error('로그인이 필요합니다.');
        } else if (error.message.includes('500')) {
          toast.error('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } else {
          toast.error(`리뷰 등록에 실패했습니다: ${error.message}`);
        }
      } else {
        toast.error('알 수 없는 오류가 발생했습니다. 다시 시도해주세요.');
      }
    }
  };

  // 둘 중 하나라도 로딩 중이면 로딩 표시
  if (storeLoading) {
    return <div>로딩 중...</div>;
  }

  // 에러 처리
  if (storeError) {
    return <div>에러가 발생했습니다.</div>;
  }

  if (!store) {
    return <div>가게 정보를 찾을 수 없습니다.</div>;
  }

  // 폼 유효성 검사
  const isFormValid =
    reviewForm.rating > 0 && reviewForm.reviewText.trim().length > 0;

  return (
    <div className="flex h-dvh flex-col items-center gap-5 bg-gray-100 p-5">
      {/* 리뷰 작성 페이지 상단 */}
      <ReviewCreateHeader
        store={store}
        rating={reviewForm.rating}
        setRating={onSetRating}
      />
      {/* 리뷰 텍스트 입력 컴포넌트에 필요한 props 전달 */}
      <ReviewCreateTextInput
        reviewText={reviewForm.reviewText}
        setReviewText={onSetReviewText}
      />
      <ReviewCreatePhotoInput
        selectedImages={reviewForm.selectedImages}
        imagePreviewUrls={reviewForm.imagePreviewUrls}
        onImagesChange={onImagesChange}
      />
      <Button
        onClick={handleSubmit}
        disabled={createReviewMutation.isPending || !isFormValid}
        className="w-50 flex h-10 cursor-pointer bg-amber-500 text-center text-lg font-bold text-white [font-family:Helvetica] hover:bg-amber-600 disabled:cursor-not-allowed disabled:opacity-50"
      >
        {createReviewMutation.isPending ? '등록 중...' : '등록하기'}
      </Button>
    </div>
  );
};

export default ReviewCreate;
