import type { StoreDetailDto } from '@/types/store';
import StoreDetailMap from './StoreDetailMap';

interface StoreDetailHeaderProps {
  store: StoreDetailDto;
}

export const StoreDescription = ({ store }: StoreDetailHeaderProps) => {
  return (
    <div className="flex flex-col items-start justify-start gap-2 bg-white p-5">
      <h2 className="text justify-center text-lg font-bold">가게 소개</h2>
      <div className="text-grey flex justify-center text-sm">
        {store.description}
      </div>
      <StoreDetailMap store={store} />
    </div>
  );
};
