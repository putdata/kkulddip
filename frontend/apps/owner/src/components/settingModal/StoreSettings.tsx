import { useMyStores } from '@/queries/store';
import StoreCard from '@/components/StoreCard';

const StoreSettings = () => {
  const { data: storesData, isLoading } = useMyStores();

  if (isLoading) {
    return <div className="py-8 text-center">로딩 중...</div>;
  }

  if (storesData === undefined || storesData.stores.length === 0) {
    return (
      <div className="py-8 text-center text-gray-500">
        등록된 가게가 없습니다.
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {storesData.stores.map(store => (
        <StoreCard key={store.storeId} store={store} />
      ))}
    </div>
  );
};

export default StoreSettings;
