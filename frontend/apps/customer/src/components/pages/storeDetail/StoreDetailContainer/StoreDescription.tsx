import type { StoreDetail } from '@/types/store';
import StoreDetailMap from './StoreDetailMap';

interface StoreDetailHeaderProps {
  store: StoreDetail;
}

export const StoreDescription = ({ store }: StoreDetailHeaderProps) => {
  return (
    <div className="flex w-full flex-col items-start justify-start gap-2 bg-white p-5">
      <h2 className="text justify-center text-lg font-bold">가게 위치</h2>
      <div className="w-full">
        <StoreDetailMap store={store} />
      </div>
    </div>
  );
};
