import FoodCard from '@/components/common/FoodCard/FoodCard';
import type { Store } from '@/types/store';
import { useNavigate } from 'react-router-dom';

interface HomeMainProps {
  stores: Store[];
}

const HomeMainContainer = ({ stores }: HomeMainProps) => {
  const navigate = useNavigate();

  const handleStoreClick = (storeId: string | number) => {
    navigate(`/stores/${storeId}`);
  };

  if (stores.length === 0) {
    return <div className="p-5">표시할 매장이 없습니다.</div>;
  }

  return (
    <div className="flex flex-col gap-3 p-5">
      {stores.map(store => (
        <FoodCard
          key={store.storeId}
          store={store}
          onClick={() => handleStoreClick(store.storeId)}
        />
      ))}
    </div>
  );
};

export default HomeMainContainer;
