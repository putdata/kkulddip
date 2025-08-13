import { ChevronRight } from 'lucide-react';
import { type CardItemProps } from '@/types/likeFoodCard';

const LikeFoodCard = ({ item, onClick }: CardItemProps) => {
  return (
    <div
      onClick={onClick}
      className="flex max-w-full cursor-pointer items-center gap-3 rounded-lg border border-gray-200 bg-white p-3 shadow-sm transition-shadow hover:shadow-md"
    >
      {/* 이미지 조건부 렌더링 */}
      <div className="relative h-16 w-16 flex-shrink-0 overflow-hidden rounded-md bg-gray-100">
        {item.img.src ? (
          <img
            src={item.img.src}
            alt={item.img.alt}
            className="h-full w-full object-cover"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-gray-200 text-gray-500">
            <span className="text-xs">🍽️</span>
          </div>
        )}
        {item.price.discount > 0 && (
          <div className="absolute bottom-0.5 left-0.5 rounded bg-red-500 px-1 py-0.5 text-[0.6rem] font-bold text-white">
            {item.discountRate}% 할인
          </div>
        )}
      </div>

      {/* 텍스트 영역 */}
      <div className="flex flex-1 flex-col gap-1">
        <h3 className="line-clamp-1 text-sm font-semibold text-gray-900">
          {item.storeInfo.storeName}
        </h3>
        <div className="flex items-center gap-2 text-xs text-gray-500">
          <span>⭐ {item.storeInfo.ratingAverage.toFixed(1)}</span>
          <span>📍 {item.distance.toFixed(2)} km</span>
        </div>
      </div>

      {/* Chevron 아이콘 */}
      <ChevronRight className="h-4 w-4 text-gray-400" />
    </div>
  );
};

export default LikeFoodCard;
