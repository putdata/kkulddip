import { OperatingHoursBadge } from './OperatingHoursBadge';
import { OutOfStockBadge } from './OutOfStockBadge';
import { ChevronRight, Heart } from 'lucide-react';
import type { DdipBox, StoreDetail } from '@/types/store';
import { generatePath, useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';
import { useState } from 'react';
import { likeService } from '@/services/likeService';
import { toast } from 'sonner';

interface StoreDetailHeaderProps {
  store: StoreDetail;
  ddipboxes: DdipBox[];
  customerId?: number;
}

export const StoreDetailHeader = ({
  store,
  ddipboxes,
  customerId,
}: StoreDetailHeaderProps) => {
  const navigate = useNavigate();
  const [isFavorite, setIsFavorite] = useState(false);

  const handleLoadAllReviews = () => {
    navigate(generatePath(ROUTE_PATH.REVIEW, { storeId: `${store.storeId}` }));
  };

  const handleFavoriteToggle = async () => {
    if (!customerId) {
      toast.warning('로그인이 필요한 서비스입니다.');
      navigate(ROUTE_PATH.LOGIN);
      return;
    }

    try {
      const params = {
        customerId: customerId, // 실제 구현시 현재 사용자 ID로 변경 필요
        storeId: store.storeId,
      };

      if (isFavorite) {
        await likeService.deleteFavorite(params);
      } else {
        await likeService.addFavorite(params);
      }

      setIsFavorite(!isFavorite);
    } catch (error) {
      console.error('Error toggling favorite:', error);
    }
  };

  const outOfStockCheck = (ddipboxes: DdipBox[]) => {
    let quantity = 0;
    ddipboxes.forEach(item => {
      quantity += item.remainingQuantity;
    });
    return quantity === 0;
  };

  const stockAvailablity = outOfStockCheck(ddipboxes);

  return (
    <div className="flex flex-col">
      <img className="h-60 w-full object-cover" src={store.storeProfileImage} />
      <div className="flex w-full flex-col items-start justify-start bg-white px-5 py-2">
        {/* 가게 이름, 주소, 별점 */}
        <div className="flex w-full flex-col justify-between">
          {/* 가게 이름, 찜 */}
          <div className="flex items-start justify-between gap-1 p-2">
            <h1 className="text-2xl font-bold">{store.storeName}</h1>
            <button
              onClick={handleFavoriteToggle}
              className="transition-colors duration-200 hover:scale-110 disabled:opacity-50"
            >
              <Heart
                className={`h-6 w-6 ${
                  isFavorite
                    ? 'fill-red-500 text-red-500'
                    : 'text-gray-400 hover:text-red-500'
                }`}
              />
            </button>
          </div>
          {/* 주소, 평점 */}
          <div className="flex items-end justify-between gap-1 pb-2">
            <p className="flex text-sm font-light">{store.storeAddress}</p>
            <div className="flex gap-1">
              <div className="justify-center font-bold">
                ⭐ {store.ratingAverage}
              </div>
              <div
                className="justify-end text-gray-900"
                onClick={handleLoadAllReviews}
              >
                ({store.reviewCount})
              </div>
              <ChevronRight className="w-5" />
            </div>
          </div>
        </div>
        <div className="flex w-full flex-col items-start justify-start gap-2 p-2">
          {stockAvailablity ? <OutOfStockBadge /> : null}
          <OperatingHoursBadge operatingHours={store.operatingHours} />
        </div>
      </div>
    </div>
  );
};
