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
    <Card
      onClick={onClick}
      className="h-[140px] cursor-pointer flex-row overflow-hidden border-gray-100 p-0 shadow-md transition-all duration-300 hover:shadow-lg gap-0"
    >
      {/* 왼쪽 이미지 */}
      <div className="h-[140px] w-36 flex-shrink-0 overflow-hidden bg-gray-100">
        {store.storeProfileImage ? (
          <img
            src={store.storeProfileImage}
            alt={`${store.storeName}의 가게 이미지`}
            className="h-full w-full object-cover"
            loading="lazy"
            onError={e => {
              const img = e.currentTarget;
              img.style.display = 'none';
              const fallback = document.createElement('div');
              fallback.className =
                'flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-100 to-orange-100';
              fallback.innerHTML = '<span class="text-4xl">🍽️</span>';
              img.parentElement?.appendChild(fallback);
            }}
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-100 to-orange-100">
            <span className="text-4xl">🍽️</span>
          </div>
        )}
      </div>
      {/* 오른쪽 내용 */}
      <div className="flex h-[140px] flex-1 flex-col justify-between p-3">
        {/* 상단: 제목, 설명, 시간 배지 */}
        <div className="flex justify-between">
          <div className="flex-1 min-w-0">
            <h3 className="mb-1 line-clamp-1 text-sm font-semibold text-gray-900">
              {store.storeName}
            </h3>
            <p className="mb-2 line-clamp-1 text-xs text-gray-600">
              {store.description}
            </p>

            {/* 가격 정보 */}
            <div className="flex items-baseline gap-1.5">
              <span className="text-base font-bold text-gray-900">
                {store.representativeSalePrice.toLocaleString()}원
              </span>
              <span className="text-xs text-gray-400 line-through">
                {store.representativeOriginalPrice.toLocaleString()}원
              </span>
            </div>
          </div>

          {/* 오른쪽 배지들 */}
          <div className="flex flex-col items-end gap-2">
            {/* 할인율 배지 */}
            <div className="rounded-full bg-gradient-to-r from-amber-500 to-orange-500 px-2.5 py-1 text-xs font-bold text-white shadow-sm">
              {/* TODO: Badge 컴포넌트로 전환 */}
              {Math.round(
                ((store.representativeOriginalPrice -
                  store.representativeSalePrice) /
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
        <div className="mt-1 flex items-center justify-between">
          <div className="flex items-center gap-2 flex-1 min-w-0">
            {/* 별점 */}
            <div className="flex items-center gap-0.5">
              <span className="text-amber-400">★</span>
              <span className="text-sm font-medium text-gray-700">
                {store.ratingAverage.toFixed(1)}
              </span>
            </div>

            {/* 거리 */}
            {store.distanceFromUser !== undefined &&
              store.distanceFromUser !== null && (
                <div className="flex items-center gap-0.5">
                  <span className="text-xs text-gray-400">📍</span>
                  <span className="text-sm text-gray-600">
                    {store.distanceFromUser < 1
                      ? `${(store.distanceFromUser * 1000).toFixed(0)}m`
                      : `${store.distanceFromUser.toFixed(1)}km`}
                  </span>
                </div>
              )}
          </div>

          {/* TODO: 남은 재고 수 합의 필요 */}
          {/* {store.remainingQuantity && (
            <div className="rounded border border-red-200 bg-white px-2 py-0.5 text-xs text-red-500">
              {store.remainingQuantity}개 남음
            </div>
          )} */}
          {/* 픽업 가능 유무 배지 - 스마트폰에서 숨김 */}
          {store.isActive ? (
            <div className="hidden sm:block rounded-full border border-green-200 bg-green-50 px-3 py-1 text-xs font-medium text-green-700">
              픽업 가능
            </div>
          ) : (
            <div className="hidden sm:block rounded-full border border-gray-200 bg-gray-50 px-3 py-1 text-xs font-medium text-gray-500">
              픽업 마감
            </div>
          )}
        </div>
      </div>
    </Card>
  );
};

export default FoodCard;
