import type { StoreDetailDto } from '@/dummies/storeDetailDummy';
import { DdipboxContainer } from './DdipboxContainer/DdipboxContainer';
import { StoreDescription } from './StoreDescription';
import { StoreMainMenu } from './StoreMainMenu';

interface StoreDetailHeaderProps {
  store: StoreDetailDto;
}

export const StoreDetailContainer = ({ store }: StoreDetailHeaderProps) => {
  return (
    <div className="min-w-sm flex w-full flex-col items-center gap-2">
      <StoreMainMenu />
      <DdipboxContainer store={store} />
      <StoreDescription store={store} />
    </div>
  );
};
