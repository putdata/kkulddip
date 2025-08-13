import { Star } from 'lucide-react';
import { useStarRating } from '@/hooks/useStarRating'; // 1. import 추가
import { useEffect } from 'react';

// 2. 기존 useState 삭제하고 커스텀 훅 사용

interface Store {
  storeId: string;
  storeName: string;
  img: string;
  imgAlt: string;
}

interface ReviewCreateHeaderProps {
  store: Store;
  rating: number; // 추가
  setRating: (rating: number) => void;
}

const ReviewCreateHeader = ({
  store,
  rating,
  setRating,
}: ReviewCreateHeaderProps) => {
  const {
    handleStarClick,
    isStarFilled,
    setRating: setLocalRating,
  } = useStarRating(rating);

  // 부모의 rating이 변경되면 로컬 상태도 동기화
  useEffect(() => {
    setLocalRating(rating);
  }, [rating, setLocalRating]);

  // 별점 클릭 핸들러
  const onStarClick = (star: number) => {
    handleStarClick(star); // 훅의 로컬 상태 업데이트
    setRating(star); // 부모 컴포넌트로 값 전달
    console.log('선택된 별점:', star);
  };

  return (
    <div className="flex w-full items-center justify-between">
      {/* 상단 왼쪽 */}
      <div className="flex flex-col items-start justify-center gap-2">
        <div className="text-xl font-bold text-black [font-family:'Segoe_UI']">
          {store.storeName}
        </div>
        <div className="inline-flex items-end">
          <div className="text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
            이번 띱박스는 어떠셨나요?
          </div>
        </div>
        <div className="flex items-center">
          {[1, 2, 3, 4, 5].map(star => (
            // 현재 별이 채워져야 하는지 판단
            <Star
              key={star}
              className={`h-6 w-6 cursor-pointer transition-colors ${
                isStarFilled(star)
                  ? 'fill-yellow-400 text-yellow-400'
                  : 'text-gray-300'
              }`}
              onClick={() => onStarClick(star)}
            />
          ))}
        </div>
      </div>

      <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100">
        <img alt={store.imgAlt} src={store.img} />
      </div>
    </div>
  );
};

export default ReviewCreateHeader;
