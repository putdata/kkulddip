import { Star } from 'lucide-react';

interface Store {
  storeId: number;
  storeName: string;
  img: string;
  imgAlt: string;
}

interface ReviewCreateHeaderProps {
  store: Store;
  rating: number;
  setRating: (rating: number) => void;
}

const ReviewCreateHeader = ({
  store,
  rating,
  setRating,
}: ReviewCreateHeaderProps) => {
  return (
    <div className="flex w-full items-center justify-between p-5">
      {/* 상단 왼쪽 */}
      <div className="flex flex-col items-start justify-center gap-2">
        <div className="text-xl font-bold text-black [font-family:'Segoe_UI']">
          {store.storeName}
        </div>
        <div className="inline-flex flex-[0_0_auto] items-end">
          <div className="text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
            이번 띱박스는 어떠셨나요?
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
                onClick={() => {
                  setRating(star);
                  console.log('선택된 별점:', star);
                }}
              />
            );
          })}
        </div>
      </div>

      <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100 p-[0.8px]">
        <img alt={store.imgAlt} src={store.img} />
      </div>
    </div>
  );
};

export default ReviewCreateHeader;
