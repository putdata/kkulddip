import { ChevronRight, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { type CardItemProps } from '@/types/likeFoodCard';

const LikeFoodCard = ({
  item,
  onClick,
  onDelete,
}: CardItemProps & { onDelete?: (id: string | number) => void }) => {
  const [translateX, setTranslateX] = useState(0);
  const [startX, setStartX] = useState(0);
  const [isDragging, setIsDragging] = useState(false);

  const handleTouchStart = (e: React.TouchEvent) => {
    if (!e.touches[0]) {
      return;
    }
    setStartX(e.touches[0].clientX);
    setIsDragging(true);
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    if (!isDragging || !e.touches[0]) {
      return;
    }
    const diffX = e.touches[0].clientX - startX;
    if (diffX < 0) {
      setTranslateX(Math.max(diffX, -120));
    }
  };

  const handleTouchEnd = () => {
    setIsDragging(false);
    if (translateX < -80) {
      onDelete?.(item.id);
    } else {
      setTranslateX(0);
    }
  };

  return (
    <div className="relative overflow-hidden rounded-lg">
      {/* 삭제 버튼 배경 */}
      <div className="absolute bottom-1 right-1 top-1 flex w-20 items-center justify-center rounded-r-lg bg-red-500">
        <Trash2 className="h-5 w-5 text-white" />
      </div>

      {/* 메인 카드 */}
      <div
        onClick={() => !isDragging && translateX === 0 && onClick?.()}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleTouchEnd}
        className="flex max-w-full cursor-pointer items-center gap-3 rounded-lg border border-gray-200 bg-white p-3 shadow-sm transition-shadow hover:shadow-md"
        style={{
          transform: `translateX(${translateX}px)`,
          transition: isDragging ? 'none' : 'transform 0.3s ease',
        }}
      >
        {/* 이미지 */}
        <div className="relative h-16 w-16 flex-shrink-0 overflow-hidden rounded-md bg-gray-100">
          {item.img.src ? (
            <img
              src={item.img.src}
              alt={item.img.alt}
              className="h-full w-full object-cover"
              draggable={false}
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
            {item.distance !== undefined && item.distance !== null && (
              <span>📍 {item.distance.toFixed(2)} km</span>
            )}
          </div>
        </div>

        {/* Chevron 아이콘 */}
        <ChevronRight className="h-4 w-4 text-gray-400" />
      </div>
    </div>
  );
};

export default LikeFoodCard;
