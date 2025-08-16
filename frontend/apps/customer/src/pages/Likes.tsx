import LikeFoodCard from '@/components/pages/likes/LikeFoodCard/LikeFoodCard';
import { useLikes } from '@/hooks/useLikes';
import { useDeleteFavorite } from '@/hooks/useDeleteFavorite';
import { useCustomerProfile } from '@/hooks/useProfile';
import { Loader2 } from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Likes = () => {
  const navigate = useNavigate();
  const { data: profile } = useCustomerProfile();
  const {
    data: stores,
    isLoading,
    isFetching,
  } = useLikes(profile?.customerId || 0);
  const deleteFavoriteMutation = useDeleteFavorite();
  const [deletedStores, setDeletedStores] = useState<Set<string | number>>(
    new Set(),
  );

  // stores가 undefined/null이어도 안전하게 처리
  const transformedStores = (stores ?? []).map(store => ({
    id: store.storeId,
    img: {
      src: store.storeProfileImage,
      alt: store.storeName,
    },
    storeInfo: {
      storeName: store.storeName,
      ratingAverage: store.reviewRating,
    },
    distance: store.distanceFromCustomer ?? null,
    price: {
      discount: 0,
    },
    discountRate: 0,
  }));

  const filteredStores = transformedStores.filter(
    store => !deletedStores.has(store.id),
  );

  const handleDelete = (storeId: string | number) => {
    if (!profile?.customerId) {
      return;
    }

    setDeletedStores(prev => new Set([...prev, storeId]));

    deleteFavoriteMutation.mutate({
      customerId: profile.customerId,
      storeId: Number(storeId),
    });
  };

  // 로딩 화면
  if (isLoading || isFetching) {
    return (
      <div className="flex flex-1 items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">가게 정보를 가져오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 찜 없음 화면 (isSuccess 없이도 표시)
  if (!isLoading && !isFetching && filteredStores.length === 0) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center space-y-4 px-4">
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

  // 매장 목록 표시
  return (
    <div>
      <div className="space-y-2 px-2">
        {filteredStores.map(item => (
          <LikeFoodCard
            key={item.id}
            item={item}
            onClick={() => navigate(`/stores/${item.id}`)}
            onDelete={handleDelete}
          />
        ))}
      </div>
    </div>
  );
};

export default Likes;
