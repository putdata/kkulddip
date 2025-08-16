import FoodCard from '@/components/common/FoodCard/FoodCard';
import type { Store } from '@/types/store';
import { useNavigate } from 'react-router-dom';
import { Loader2 } from 'lucide-react';

interface HomeMainProps {
  stores: Store[];
  lastElementRef?: (node: HTMLDivElement | null) => void;
  isFetchingNextPage?: boolean;
  hasNextPage?: boolean;
}

const HomeMainContainer = ({ 
  stores, 
  lastElementRef, 
  isFetchingNextPage, 
  hasNextPage 
}: HomeMainProps) => {
  const navigate = useNavigate();

  const handleStoreClick = (storeId: string | number) => {
    navigate(`/stores/${storeId}`);
  };

  if (stores.length === 0) {
    return <div className="p-5">표시할 매장이 없습니다.</div>;
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
          <p className="text-sm text-gray-500">
            모든 가게를 불러왔어요
          </p>
        </div>
      )}
    </div>
  );
};

export default HomeMainContainer;
