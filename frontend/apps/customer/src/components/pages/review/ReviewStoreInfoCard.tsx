import { StarRating } from '@/components/common/StarRating';
import { Card, CardTitle } from '@/components/ui/card';
import type { StoreDetailDto } from '@/types/store';

interface StoreInfoProps {
  store: StoreDetailDto;
  totalReviews: number;
}

const ReviewStoreInfoCard = ({ store, totalReviews }: StoreInfoProps) => {
  return (
    <Card
      key={store.storeId}
      className="w-full flex-row justify-between gap-2 rounded-none bg-white p-4"
    >
      <div className="flex flex-col justify-center">
        <CardTitle>{store.storeName}</CardTitle>
        <div className="flex items-center gap-1">
          {/* 평점 */}
          <div className="flex items-center justify-start gap-2 text-lg font-bold text-amber-500">
            {store.ratingAverage}
          </div>
          {/* 별점 표시 */}
          <StarRating rating={store.ratingAverage} />
          <div className="text-xs text-gray-400">총 {totalReviews}개 리뷰</div>
        </div>
      </div>
      <img
        src={store.storeProfileImage}
        alt={`${store.storeName}의 사진`}
        className="h-20 w-20"
      />
    </Card>
  );
};

export default ReviewStoreInfoCard;
