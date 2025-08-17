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
  const [hasDragged, setHasDragged] = useState(false);

  const handleTouchStart = (e: React.TouchEvent) => {
    if (!e.touches[0]) {
      return;
    }
    setStartX(e.touches[0].clientX);
    setIsDragging(true);
    setHasDragged(false);
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    if (!isDragging || !e.touches[0]) {
      return;
    }
    const diffX = e.touches[0].clientX - startX;
    // 왼쪽으로 밀기 (음수 값)
    if (diffX < 0) {
      setTranslateX(Math.max(diffX, -100));
      // 드래그 거리가 5px 이상이면 드래그로 인식
      if (Math.abs(diffX) > 5) {
        setHasDragged(true);
      }
    }
  };

  const handleTouchEnd = () => {
    setIsDragging(false);
    if (translateX < -80) {
      onDelete?.(item.id);
    } else {
      setTranslateX(0);
    }
    // 드래그가 끝나면 hasDragged 상태를 약간의 지연 후 리셋
    setTimeout(() => setHasDragged(false), 100);
  };

  // 마우스 이벤트 (데스크톱 지원)
  const handleMouseDown = (e: React.MouseEvent) => {
    setStartX(e.clientX);
    setIsDragging(true);
    setHasDragged(false);
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDragging) {
      return;
    }
    const diffX = e.clientX - startX;
    if (diffX < 0) {
      setTranslateX(Math.max(diffX, -100));
      // 드래그 거리가 5px 이상이면 드래그로 인식
      if (Math.abs(diffX) > 5) {
        setHasDragged(true);
      }
    }
  };

  const handleMouseUp = () => {
    setIsDragging(false);
    if (translateX < -80) {
      onDelete?.(item.id);
    } else {
      setTranslateX(0);
    }
    // 드래그가 끝나면 hasDragged 상태를 약간의 지연 후 리셋
    setTimeout(() => setHasDragged(false), 100);
  };

  return (
    <div className="relative overflow-hidden rounded-xl">
      {/* 삭제 버튼 배경 */}
      <div className="absolute bottom-1 right-1 top-1 flex w-16 items-center justify-center rounded-md bg-gradient-to-l from-red-500 to-red-400 shadow-lg">
        <div className="flex flex-col items-center gap-1">
          <Trash2 className="h-4 w-4 text-white" />
          <span className="text-[10px] font-medium text-white">삭제</span>
        </div>
      </div>

      {/* 메인 카드 */}
      <div
        onClick={() => {
          // 드래그가 없었고, 현재 위치가 원점이고, 드래그 중이 아닐 때만 클릭 처리
          if (!hasDragged && !isDragging && translateX === 0) {
            onClick?.();
          }
        }}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleTouchEnd}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        className="flex max-w-full cursor-pointer select-none items-center gap-4 rounded-xl border border-gray-100 bg-white p-4 shadow-md transition-all duration-200 hover:scale-[1.01] hover:shadow-lg"
        style={{
          transform: `translateX(${translateX}px)`,
          transition: isDragging ? 'none' : 'transform 0.3s ease',
        }}
      >
        {/* 이미지 */}
        <div className="relative h-20 w-20 flex-shrink-0 overflow-hidden rounded-xl bg-gradient-to-br from-gray-100 to-gray-200 shadow-sm">
          {item.img.src ? (
            <img
              src={item.img.src}
              alt={item.img.alt}
              className="h-full w-full object-cover"
              draggable={false}
            />
          ) : (
            <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-100 to-orange-100">
              <span className="text-2xl">🍽️</span>
            </div>
          )}
          {item.price.discount > 0 && (
            <div className="absolute bottom-1 left-1 rounded-full bg-gradient-to-r from-red-500 to-red-400 px-2 py-1 text-[0.6rem] font-bold text-white shadow-sm">
              {item.discountRate}% 할인
            </div>
          )}
          {/* 하트 아이콘 */}
          <div className="absolute right-1 top-1 flex h-6 w-6 items-center justify-center rounded-full bg-white/90 shadow-sm">
            <span className="text-xs">❤️</span>
          </div>
        </div>

        {/* 텍스트 영역 */}
        <div className="flex flex-1 flex-col gap-2">
          <h3 className="line-clamp-1 text-base font-bold text-gray-900">
            {item.storeInfo.storeName}
          </h3>
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1">
              <span className="text-amber-400">⭐</span>
              <span className="text-sm font-medium text-gray-700">
                {item.storeInfo.ratingAverage.toFixed(1)}
              </span>
            </div>
            {item.distance !== undefined && item.distance !== null && (
              <div className="flex items-center gap-1">
                <span className="text-xs text-gray-400">📍</span>
                <span className="text-sm text-gray-600">
                  {item.distance < 1
                    ? `${(item.distance * 1000).toFixed(0)}m`
                    : `${item.distance.toFixed(1)}km`}
                </span>
              </div>
            )}
          </div>
          <div className="text-xs text-gray-500">← 왼쪽으로 밀어서 삭제</div>
        </div>

        {/* Chevron 아이콘 */}
        <div className="flex flex-col items-center gap-1">
          <ChevronRight className="h-5 w-5 text-gray-400" />
          <span className="text-xs text-gray-400">보기</span>
        </div>
      </div>
    </div>
  );
};

export default LikeFoodCard;
