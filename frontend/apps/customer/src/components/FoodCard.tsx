import React from 'react';

// 음식 카드 데이터 타입 정의
export interface FoodItem {
  storeInfo: {
    storeName: string;
    description: string;
    ratingAverage: number;
  };
  img: {
    src: string;
    alt: string;
  };
  price: {
    original: number;
    discount: number;
  };
  distance: number;
  timeLeftHour?: number;
  remainingQuantity: number;
}

export interface FoodCardProps {
  food: FoodItem;
  onClick?: () => void;
}

/**
 * 음식 카드 컴포넌트
 * @param FoodCardProps
 * @returns Card
 */
const FoodCard = ({ food, onClick }: FoodCardProps) => {
  return (
    <div
      className="flex w-full overflow-hidden rounded-lg bg-white shadow-md"
      onClick={onClick}
    >
      {/* 왼쪽 이미지 */}
      <div className="relative h-48 w-32 flex-shrink-0">
        <img
          src={food.img.src}
          alt={food.img.alt}
          className="h-full w-full object-cover"
        />
      </div>
      {/* 오른쪽 내용 */}
      <div className="flex flex-1 flex-col justify-between p-6">
        {/* 상단: 제목, 설명, 시간 배지 */}
        <div className="flex justify-between">
          <div className="flex-1">
            <h3 className="mb-1 text-lg font-bold text-gray-900">
              {food.storeInfo.storeName}
            </h3>
            <p className="mb-3 text-sm text-gray-500">
              {food.storeInfo.description}
            </p>

            {/* 가격 정보 */}
            <div className="mb-0.5">
              <span className="text-sm text-gray-400 line-through">
                {food.price.original.toLocaleString()}원
              </span>
            </div>
            <span className="text-xl font-bold text-green-600">
              {food.price.discount.toLocaleString()}원
            </span>
          </div>

          {/* 오른쪽 배지들 */}
          <div className="flex flex-col-reverse items-end gap-1">
            {/* 할인율 배지 */}
            <div className="bg-linear-to-r mb-1 rounded from-red-400 to-yellow-400 px-1.5 py-0.5 text-xs font-bold text-white">
              {/* TODO: Badge 컴포넌트로 전환 */}
            </div>
            {/* 시간 배지 */}
            {food.timeLeftHour && (
              <div className="bg-linear-to-r rounded bg-red-400 px-1.5 py-0.5 text-xs font-medium text-white">
                {/* TODO: Badge 컴포넌트로 전환 */}
                {food.timeLeftHour}시간
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
              {food.storeInfo.ratingAverage}
            </span>

            {/* 거리 */}
            <span className="text-gray-400">📍</span>
            <span className="text-sm text-gray-600">{food.distance}km</span>
          </div>

          {/* 남은 수량 */}
          {food.remainingQuantity && (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              {/* TODO: Badge 컴포넌트로 전환 */}
              {food.remainingQuantity}개 남음
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default FoodCard;
