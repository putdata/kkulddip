import { Card } from '@/components/ui/card';
import type { Store } from '@/types/store';

// 음식 카드 데이터 타입 정의

export interface FoodCardProps {
  store: Store;
  onClick?: () => void;
}

/**
 * 음식 카드 컴포넌트
 * @param FoodCardProps
 * @returns Card
 */
const FoodCard = ({ store, onClick }: FoodCardProps) => {
  console.log('유저와의 거리: ', store.distanceFromUser);

  return (
    <Card onClick={onClick} className="flex-row gap-1 overflow-hidden p-0">
      {/* 왼쪽 이미지 */}
      <div className="h-48 w-32">
        <img
          src={store.storeProfileImage}
          alt={`${store.storeName}의 가게 이미지`}
          className="h-full w-full object-cover"
        />
      </div>
      {/* 오른쪽 내용 */}
      <div className="flex flex-1 flex-col justify-between p-3">
        {/* 상단: 제목, 설명, 시간 배지 */}
        <div className="flex justify-between">
          <div className="flex-1">
            <h3 className="mb-1 text-lg font-bold text-gray-900">
              {store.storeName}
            </h3>
            <p className="mb-3 text-sm text-gray-500">{store.description}</p>

            {/* 가격 정보 */}
            <div className="mb-0.5">
              <span className="text-sm text-gray-400 line-through">
                {store.representativeOriginalPrice.toLocaleString()}원
              </span>
            </div>
            <span className="text-xl font-bold text-green-600">
              {store.representativeSalePrice.toLocaleString()}원
            </span>
          </div>

          {/* 오른쪽 배지들 */}
          <div className="flex flex-col-reverse items-end gap-1">
            {/* 할인율 배지 */}
            <div className="bg-linear-to-r mb-1 rounded from-red-400 to-yellow-400 px-1.5 py-0.5 text-xs font-bold text-white">
              {/* TODO: Badge 컴포넌트로 전환 */}
              {Math.round(
                (store.representativeSalePrice /
                  store.representativeOriginalPrice) *
                  100,
              )}
              %
            </div>
            {/* TODO: 시간 배지 표시 합의 필요 */}
            {/* 
            {store.timeLeftHour && (
              <div className="bg-linear-to-r rounded bg-red-400 px-1.5 py-0.5 text-xs font-medium text-white">
              {store.timeLeftHour}시간
              </div>
              )}
              */}
          </div>
        </div>

        {/* 하단: 별점, 거리, 남은 수량 */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1">
            {/* 별점 */}
            <span className="text-gray-600">★</span>
            <span className="text-sm font-medium text-gray-700">
              {store.ratingAverage}
            </span>

            {/* 거리 */}
            <span className="text-gray-400">📍</span>
            <span className="text-sm text-gray-600">
              {store.distanceFromUser.toFixed(2)}km
            </span>
          </div>

          {/* TODO: 남은 재고 수 합의 필요 */}
          {/* {store.remainingQuantity && (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              {store.remainingQuantity}개 남음
            </div>
          )} */}
          {/* TODO: 픽업 가능 유무 배지로 대체 */}
          {store.isActive ? (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              픽업 가능
            </div>
          ) : (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              픽업 불가
            </div>
          )}
        </div>
      </div>
    </Card>
  );
};

export default FoodCard;
