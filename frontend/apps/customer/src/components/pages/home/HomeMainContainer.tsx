import FoodCard from '@/components/common/FoodCard/FoodCard';
import type { Store } from '@/types/store';
import { useNavigate } from 'react-router-dom';
import { Loader2 } from 'lucide-react';

interface HomeMainProps {
  stores: Store[];
  lastElementRef?: (node: HTMLDivElement | null) => void;
  isFetchingNextPage?: boolean;
  hasNextPage?: boolean;
  hasLocationError?: boolean;
  onRequestLocation?: () => void;
}

const HomeMainContainer = ({
  stores,
  lastElementRef,
  isFetchingNextPage,
  hasNextPage,
}: HomeMainProps) => {
  const navigate = useNavigate();

  const handleStoreClick = (storeId: string | number) => {
    navigate(`/stores/${storeId}`);
  };

  if (stores.length === 0) {
    return (
      <div className="flex min-h-[50vh] flex-col items-center justify-center p-8">
        <div className="mb-4 text-6xl">📍</div>
        <h3 className="mb-2 text-lg font-semibold text-gray-800">
          위치 권한을 허용해 주세요!
        </h3>
        <p className="text-center text-sm text-gray-500">
          내 주변 맛집을 찾기 위해
          <br />
          위치 권한이 필요해요
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-3 p-5">
      {stores.map((store, index) => (
        <div
          key={`${store.storeId}-${index}`}
          ref={index === stores.length - 1 ? lastElementRef : null}
        >
          <FoodCard
            store={store}
            onClick={() => handleStoreClick(store.storeId)}
          />
        </div>
      ))}

      {/* 로딩 인디케이터 */}
      {isFetchingNextPage && (
        <div className="flex justify-center py-4">
          <Loader2 className="h-6 w-6 animate-spin text-amber-600" />
        </div>
      )}

      {/* 더 이상 로드할 데이터가 없을 때 */}
      {!hasNextPage && stores.length > 0 && (
        <div className="py-8 text-center">
          <p className="text-sm text-gray-500">모든 가게를 불러왔어요</p>
        </div>
      )}
    </div>
  );
};

export default HomeMainContainer;
