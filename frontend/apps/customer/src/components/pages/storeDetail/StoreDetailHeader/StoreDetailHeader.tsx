import { OperatingHoursBadge } from './OperatingHoursBadge';
import { OutOfStockBadge } from './OutOfStockBadge';
import { ChevronRight } from 'lucide-react';
import type { DdipBox, StoreDetail } from '@/types/store';
import { generatePath, useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

interface StoreDetailHeaderProps {
  store: StoreDetail;
  ddipboxes: DdipBox[];
}

export const StoreDetailHeader = ({
  store,
  ddipboxes,
}: StoreDetailHeaderProps) => {
  const navigate = useNavigate();

  const handleLoadAllReviews = () => {
    navigate(generatePath(ROUTE_PATH.REVIEW, { storeId: `${store.storeId}` }));
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
        <div className="flex w-full justify-between">
          {/* 가게 이름, 주소 */}
          <div className="flex flex-col items-start justify-start gap-1 p-2">
            <h1 className="text-2xl font-bold">{store.storeName}</h1>
            <p className="flex text-sm font-light">{store.storeAddress}</p>
          </div>
          {/* 평점 */}
          <div className="flex items-end gap-1 pb-2">
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
        <div className="flex w-full flex-col items-start justify-start gap-2 p-2">
          {stockAvailablity ? <OutOfStockBadge /> : null}
          <OperatingHoursBadge operatingHours={store.operatingHours} />
        </div>
      </div>
    </div>
  );
};
