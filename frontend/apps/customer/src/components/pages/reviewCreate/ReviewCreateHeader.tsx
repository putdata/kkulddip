import { reviewCreateMockData } from '@/constants/mockData';
import { reviewCreateMessages } from '@/constants/messages';
import { useState } from 'react';
import { Star } from 'lucide-react';

const ReviewCreateHeader = () => {
  const [rating, setRating] = useState<number>(0);

  // 별점 클릭 핸들러
  const handleStarClick = (starNumber: number) => {
    setRating(starNumber);
    console.log('선택된 별점:', starNumber); // 개발용 로그
  };

  const storeData = reviewCreateMockData;
  const messages = reviewCreateMessages;
  return (
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
                  isFilled ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
                }`}
                onClick={() => handleStarClick(star)}
              />
            );
          })}
        </div>
      </div>

      <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100 p-[0.8px]">
        <img alt={storeData.imgAlt} src={storeData.img} />
      </div>
    </div>
  );
};

export default ReviewCreateHeader;
