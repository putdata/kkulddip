import LikeFoodCard from '@/components/pages/likes/LikeFoodCard/LikeFoodCard';
import { useLikes } from '@/hooks/useLikes';
import { Loader2 } from 'lucide-react';

const Likes = () => {
  const { stores, isLoading } = useLikes(5);

  const transformedStores = stores.map(store => ({
    img: {
      src: store.storeProfileImage,
      alt: store.storeName,
    },
    storeInfo: {
      storeName: store.storeName,
      ratingAverage: store.reviewRating,
    },
    distance: store.distanceFromCustomer,
    price: {
      discount: 0,
    },
    discountRate: 0,
  }));

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">가게 정보를 가져오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 데이터가 없을 때
  if (!stores || stores.length === 0) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center space-y-4 px-4">
        <div className="text-4xl">💔</div>
        <div className="text-center">
          <h3 className="mb-2 text-lg font-semibold text-gray-800">
            아직 좋아하는 매장이 없어요
          </h3>
          <p className="text-sm text-gray-500">
            마음에 드는 매장을 찾아서 하트를 눌러보세요!
          </p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="space-y-2 px-2 pb-16 pt-16">
        {transformedStores.map((item, idx) => {
          return (
            <LikeFoodCard
              key={stores[idx]?.storeId}
              item={item}
              onClick={() => console.log('매장 클릭')}
            />
          );
        })}
      </div>
    </div>
  );
};

export default Likes;
