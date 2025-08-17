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
      <div className="flex flex-1 flex-col items-center justify-center space-y-6 px-4 py-16">
        <div className="relative">
          <div className="animate-pulse text-8xl">💖</div>
          <div className="absolute -right-2 -top-2 animate-bounce text-2xl">
            ✨
          </div>
        </div>
        <div className="space-y-3 text-center">
          <h3 className="mb-2 text-xl font-bold text-gray-800">
            찜한 매장이 없어요
          </h3>
          <p className="max-w-xs leading-relaxed text-gray-600">
            맛있는 띱박스를 판매하는
            <br />
            매장을 찾아서 ❤️를 눌러보세요!
          </p>
        </div>
        <div className="max-w-sm rounded-2xl border border-amber-200 bg-gradient-to-r from-amber-50 to-orange-50 p-4">
          <p className="text-center text-sm font-medium text-amber-700">
            💡 찜한 매장은 여기에서 모아볼 수 있어요
          </p>
        </div>
      </div>
    );
  }

  // 매장 목록 표시
  return (
    <div className="px-4 py-4">
      {/* 찜 목록 헤더 */}
      <div className="mb-6">
        <div className="mb-2 flex items-center gap-2">
          <span className="text-2xl">❤️</span>
          <h2 className="text-lg font-bold text-gray-900">내가 찜한 매장</h2>
        </div>
        <p className="text-sm text-gray-600">
          총{' '}
          <span className="font-semibold text-amber-600">
            {filteredStores.length}개
          </span>
          의 매장을 찜했어요
        </p>
      </div>

      {/* 찜 목록 */}
      <div className="space-y-3">
        {filteredStores.map(item => (
          <LikeFoodCard
            key={item.id}
            item={item}
            onClick={() => navigate(`/stores/${item.id}`)}
            onDelete={handleDelete}
          />
        ))}
      </div>

      {/* 하단 여백 (네비게이션 고려) */}
      <div className="h-20"></div>
    </div>
  );
};

export default Likes;
