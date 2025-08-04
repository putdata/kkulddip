import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Toaster } from '@/components/ui/sonner';
import { toast } from 'sonner';
import { Textarea } from '@/components/ui/textarea';
import { Camera, Star, X } from 'lucide-react';
import { reviewCreateMessages } from '@/constants/messages';
import { reviewCreateMockData } from '@/constants/mockData';

import { useCreateReviewMutation } from '@/services/reviewService';

// // 전달 받는 값
// export interface Props {
//   storeId: string;
//   storeName: string;
//   img: string;
//   imgAlt: string;
// }

// 전달 할 값
// starNumber
// event.target.value
// files

// const ReviewCreate = ({ storeId, storeName, img, imgAlt }: Props) => {
const ReviewCreate = () => {
  // 이미지 업로드 관련 state들
  const [selectedImages, setSelectedImages] = useState<File[]>([]);
  const [imagePreviewUrls, setImagePreviewUrls] = useState<string[]>([]);

  // 리뷰 작성 관련 state들
  const [rating, setRating] = useState<number>(0);
  const [reviewText, setReviewText] = useState<string>('');

  const { mutate: createReview } = useCreateReviewMutation();

  // 별점 클릭 핸들러
  const handleStarClick = (starNumber: number) => {
    setRating(starNumber);
    console.log('선택된 별점:', starNumber); // 개발용 로그
  };

  // 리뷰 텍스트 변경 핸들러
  const handleReviewTextChange = (
    event: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setReviewText(event.target.value);
    console.log('작성된 리뷰:', event.target.value); // 개발용 로그
  };

  // 파일 선택 핸들러
  const handleImageSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;

    // 이미지 업로드 취소
    if (!files) {
      return;
    }

    console.log('선택된 파일들:', files);

    // 현재 선택된 이미지 + 새로 선택된 이미지
    const newImages = Array.from(files);
    const totalImages = [...selectedImages, ...newImages];

    if (totalImages.length > 3) {
      // toast 로 바꾸기
      // alert('업로드 가능한 사진은 최대 3개입니다.');
      toast.error('사진 업로드 제한', {
        description: '최대 3개까지만 선택할 수 있습니다.',
      });
      return;
    }

    setSelectedImages(totalImages);

    // 미리보기 URL 생성
    const newPreviewUrls = newImages.map(file => URL.createObjectURL(file));
    setImagePreviewUrls(prev => [...prev, ...newPreviewUrls]);
  };

  // 이미지 삭제 핸들러
  const handleImageRemove = (index: number) => {
    // 해당 인덱스의 이미지와 미리보기 URL 제거
    const newImages = selectedImages.filter((_, i) => i !== index);
    const newPreviewUrls = imagePreviewUrls.filter((_, i) => i !== index);

    // 메모리 해제 (중요!)
    const urlToRevoke = imagePreviewUrls[index];
    if (urlToRevoke) {
      // undefined가 아닐 때만 실행
      URL.revokeObjectURL(urlToRevoke);
    }

    setSelectedImages(newImages);
    setImagePreviewUrls(newPreviewUrls);
  };

  // 파일 선택창 열기
  const openFileDialog = () => {
    document.getElementById('imageInput')?.click();
  };

  // 리뷰 데이터 전송 핸들러
  const handleSubmit = async () => {
    // 1. 입력 검증
    if (rating === 0) {
      toast.error('별점을 선택해주세요.');
      return;
    }

    if (reviewText.trim() === '') {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }

    createReview({
      storeId: storeData.storeId,
      rating: rating,
      reviewText: reviewText,
      images: selectedImages,
    });
  };

  // UI 문구 (프론트엔드에서 관리)
  const messages = reviewCreateMessages;
  // 서버 데이터 (나중에 API로 교체될 부분)
  const storeData = reviewCreateMockData;

  return (
    // 임시 - div 확인용 스타일
    <>
      <style>{`
      // div {
      //   border: 1px solid aqua;
      // }
    `}</style>
      {/* // 메인 div  */}
      {/* 주의: h-screen을 해뒀지만, footer 크기만큼 빼줘야 함.
      아마.... footer 에서 조정해줘야 할듯?? */}
      <div className="flex h-screen flex-col bg-gray-100">
        {/* 가게 정보, 별점 남기기 */}
        <div className="flex w-full items-center justify-between p-5">
          {/* 상단 왼쪽 */}
          <div className="flex flex-col items-start justify-center gap-2">
            <div className="text-xl font-bold text-black [font-family:'Segoe_UI']">
              {storeData.storeName}
            </div>
            <div className="inline-flex flex-[0_0_auto] items-end">
              <div className="text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
                {messages.question1}
              </div>
            </div>
            <div className="flex items-center">
              {[1, 2, 3, 4, 5].map(star => {
                // 현재 별이 채워져야 하는지 판단
                const isFilled = star <= rating;

                return (
                  <Star
                    key={star}
                    className={`h-6 w-6 cursor-pointer transition-colors ${
                      isFilled
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                    onClick={() => handleStarClick(star)}
                    // onMouseEnter={() => setHoveredRating(star)}
                    // onMouseLeave={() => setHoveredRating(0)}
                  />
                );
              })}
            </div>
          </div>

          <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100 p-[0.8px]">
            <img alt={storeData.imgAlt} src={storeData.img} />
          </div>
        </div>

        <Separator />

        <div className="flex flex-col items-start gap-2 px-5 py-2.5">
          <div className="w-fit whitespace-nowrap text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
            {messages.description1}
          </div>
          <div className="flex w-full items-start gap-1.5 self-stretch">
            <Textarea
              className="h-50 resize-none bg-white p-2.5"
              placeholder={messages.placeholder1}
              value={reviewText}
              onChange={handleReviewTextChange}
            />
          </div>
        </div>

        <div className="flex w-full items-center justify-center gap-2.5 p-5">
          {selectedImages.length < 3 && (
            <Button
              className="flex h-auto w-full flex-1 grow cursor-pointer items-center justify-center border border-dashed border-amber-600 bg-transparent text-amber-600 hover:bg-amber-500 hover:text-white"
              onClick={openFileDialog}
            >
              <input
                id="imageInput"
                type="file"
                multiple
                accept="image/*"
                onChange={handleImageSelect}
                className="hidden"
              />
              <Camera className="h-6 w-6" />
              <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
                {messages.addPicture}
              </span>
              <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
                ( {selectedImages.length} / 3 )
              </span>
            </Button>
          )}
        </div>

        {/* 선택된 이미지 미리보기 */}
        {selectedImages.length > 0 && (
          <div className="flex flex-col gap-2 px-5">
            <div className="flex gap-2 overflow-x-auto">
              {imagePreviewUrls.map((url, index) => (
                <div key={index} className="relative flex-shrink-0">
                  <img
                    src={url}
                    alt={`선택된 이미지 ${index + 1}`}
                    className="h-20 w-20 rounded-lg border border-gray-200 object-cover"
                  />
                  <button
                    onClick={() => handleImageRemove(index)}
                    className="absolute right-1 top-1 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-white hover:bg-red-600"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="flex justify-center">
          <Button
            onClick={handleSubmit}
            className="w-50 m-5 flex cursor-pointer bg-amber-500 text-center font-bold text-white [font-family:Helvetica] hover:bg-amber-600"
          >
            {messages.submitButton}
          </Button>
        </div>
        <Toaster position="top-center" />
      </div>
    </>
  );
};

export default ReviewCreate;
