import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Textarea } from '@/components/ui/textarea';
import { Camera, Star } from 'lucide-react';

import { reviewCreateMessages } from '@/constants/messages';
import { reviewCreateMockData } from '@/constants/mockData';
import { useState } from 'react';

// export interface Props {
//   storeId: number;
//   storeName: string;
//   img: string;
//   imgAlt: string;
// }

// const ReviewCreate = ({ storeName }: Props) => {
const ReviewCreate = () => {
  // ui 문구
  const messages = reviewCreateMessages;
  // 예시 데이터
  const props = reviewCreateMockData;

  // states
  // 리뷰 이미지
  const [selectedImages, setSelectedImages] = useState<File[]>([]);
  // 별점
  const [rating, setRating] = useState<number>(0);
  // 리뷰 내용
  const [reviewText, setReviewText] = useState<string>('');

  // handler
  const handleStarClick = (starNumber: number) => {
    setRating(starNumber);
    console.log('별점: ', starNumber);
  };

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
              {props.storeName}
            </div>
            <div className="inline-flex flex-[0_0_auto] items-end">
              <div className="text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
                {messages.question1}
              </div>
            </div>
            <div className="flex items-center">
              {[1, 2, 3, 4, 5].map(star => (
                <Star
                  key={star}
                  className={`cursor-pointer ${star <= rating ? 'fill-yellow-500 text-yellow-500' : 'text-gray-300'} `}
                  onClick={() => handleStarClick(star)}
                />
                // <div key={star} className="h-6 w-6 text-gray-300">
                //   ⭐
                // </div>
              ))}
            </div>
          </div>

          <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100 p-[0.8px]">
            <img alt={props.imgAlt} src={props.img} />
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
            />
          </div>
        </div>

        <div className="flex w-full items-center justify-center gap-2.5 p-5">
          <Button className="pointer-events-none flex h-auto w-full flex-1 grow items-center justify-center border border-dashed border-amber-600 bg-transparent text-amber-600">
            <Camera className="h-6 w-6" />
            <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
              {messages.addPicture}
            </span>
            <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
              {messages.pictureCount}
            </span>
          </Button>
        </div>

        <div className="flex justify-center">
          <Button className="w-50 pointer-events-none flex bg-amber-500 text-center font-bold text-white [font-family:Helvetica]">
            {messages.submitButton}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ReviewCreate;
