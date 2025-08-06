import { Card, CardTitle } from '@/components/ui/card';
import { Star } from 'lucide-react';

interface StoreInfo {
  id: number;
  storeName: string;
  avgRate: number;
}

interface StoreInfoProps {
  store: StoreInfo;
  totalReviews: number;
}

const ReviewStoreInfoCard = ({ store, totalReviews }: StoreInfoProps) => {
  return (
    <div>
      <Card key={store.id} className="w-full gap-2 rounded-none bg-white p-4">
        <CardTitle>{store.storeName}</CardTitle>
        <div className="flex items-center gap-1">
          {/* 평점 */}
          <div className="flex items-center justify-start gap-2 text-lg font-bold text-amber-500">
            {store.avgRate}
          </div>
          {/* 별점 표시 */}
          <div className="flex items-center gap-0.5">
            {[1, 2, 3, 4, 5].map(star => (
              <Star
                key={star}
                className={`h-4 w-4 ${
                  star <= store.avgRate
                    ? 'fill-amber-500 text-amber-500'
                    : 'fill-gray-200 text-gray-200'
                }`}
              />
            ))}
          </div>
          <div className="text-xs text-gray-400">총 {totalReviews}개 리뷰</div>
        </div>
      </Card>
    </div>
  );
};

export default ReviewStoreInfoCard;
