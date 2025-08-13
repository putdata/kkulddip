import LikeFoodCard from '@/components/pages/likes/LikeFoodCard/LikeFoodCard';
import { useLikes, useDeleteFavorite } from '@/hooks/useLikes';
import { useUserStore } from 'common';
import { Loader2 } from 'lucide-react';
import { useState } from 'react';

const Likes = () => {
  const user = useUserStore(state => state.user);
  const {
    data: stores,
    isLoading,
    isFetching,
    isSuccess,
  } = useLikes(user?.userId || 0);
  const deleteFavoriteMutation = useDeleteFavorite();
  const [deletedStores, setDeletedStores] = useState<Set<string | number>>(
    new Set(),
  );

  const transformedStores = stores?.map(store => ({
    id: store.storeId,
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

  const filteredStores = transformedStores?.filter(
    store => !deletedStores.has(store.id),
  );

  const handleDelete = (storeId: string | number) => {
    if (!user?.userId) {
      return;
    }

    setDeletedStores(prev => new Set([...prev, storeId]));

    deleteFavoriteMutation.mutate({
      customerId: user.userId,
      storeId: Number(storeId),
    });
  };

  if (isLoading || isFetching) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">가게 정보를 가져오고 있어요...</div>
        </div>
      </div>
    );
  }

  if (isSuccess && (!filteredStores || filteredStores.length === 0)) {
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
        {filteredStores?.map(item => (
          <LikeFoodCard
            key={item.id}
            item={item}
            onClick={() => console.log('매장 클릭:', item.id)}
            onDelete={handleDelete}
          />
        ))}
      </div>
    </div>
  );
};

export default Likes;
