import React from 'react';

interface FoodImage {
  url: string;
  alt: string;
}

interface PriceInfo {
  original: number;
  discounted: number;
  discountPercent: number;
}

interface LocationInfo {
  rating: number;
  distance: number;
}

interface StockInfo {
  timeLeft?: number;
  remainingQuantity?: number;
}

// 음식 카드 데이터 타입 정의
export interface FoodCardProps {
  title: string;
  description: string;
  image: FoodImage;
  price: PriceInfo;
  location: LocationInfo;
  stock?: StockInfo;
}

/**
 * 음식 카드 컴포넌트
 * @param FoodCardProps
 * @returns Card
 */
const FoodCard = ({
  title,
  description,
  image,
  price,
  location,
  stock,
}: FoodCardProps) => {
  return (
    <div className="flex w-full overflow-hidden rounded-lg bg-white shadow-md">
      {/* 왼쪽 이미지 */}
      <div className="relative h-48 w-32 flex-shrink-0">
        <img
          src={image.url}
          alt={image.alt}
          className="h-full w-full object-cover"
        />
      </div>
      {/* 오른쪽 내용 */}
      <div className="flex flex-1 flex-col justify-between p-6">
        {/* 상단: 제목, 설명, 시간 배지 */}
        <div className="flex justify-between">
          <div className="flex-1">
            <h3 className="mb-1 text-lg font-bold text-gray-900">{title}</h3>
            <p className="mb-3 text-sm text-gray-500">{description}</p>

            {/* 가격 정보 */}
            <div className="mb-0.5">
              <span className="text-sm text-gray-400 line-through">
                {price.original.toLocaleString()}원
              </span>
            </div>
            <span className="text-xl font-bold text-green-600">
              {price.discounted.toLocaleString()}원
            </span>
          </div>

          {/* 오른쪽 배지들 */}
          <div className="flex flex-col-reverse items-end gap-1">
            {/* 할인율 배지 */}
            <div className="bg-linear-to-r mb-1 rounded from-red-400 to-yellow-400 px-1.5 py-0.5 text-xs font-bold text-white">
              {price.discountPercent}% 할인
            </div>
            {/* 시간 배지 */}
            {stock?.timeLeft && (
              <div className="bg-linear-to-r rounded bg-red-400 px-1.5 py-0.5 text-xs font-medium text-white">
                {stock.timeLeft}시간
              </div>
            )}
          </div>
        </div>

        {/* 하단: 별점, 거리, 남은 수량 */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1">
            {/* 별점 */}
            <span className="text-gray-600">★</span>
            <span className="text-sm font-medium text-gray-700">
              {location.rating}
            </span>

            {/* 거리 */}
            <span className="text-gray-400">📍</span>
            <span className="text-sm text-gray-600">{location.distance}km</span>
          </div>

          {/* 남은 수량 */}
          {stock?.remainingQuantity && (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              {stock.remainingQuantity}개 남음
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default FoodCard;
