import type { StoreDetailDto } from '@/types/store';
import { DdipboxContainer } from './DdipboxContainer/DdipboxContainer';
import { StoreDescription } from './StoreDescription';

interface StoreDetailHeaderProps {
  store: StoreDetailDto;
}

export const StoreDetailContainer = ({ store }: StoreDetailHeaderProps) => {
  return (
    <div className="min-w-sm flex w-full flex-col items-center gap-2">
      <DdipboxContainer store={store} />
      <StoreDescription store={store} />
    </div>
  );
};
