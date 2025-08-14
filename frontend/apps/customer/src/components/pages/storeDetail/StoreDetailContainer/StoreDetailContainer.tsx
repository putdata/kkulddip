import type { DdipBox, StoreDetail } from '@/types/store';
import { DdipboxContainer } from './DdipboxContainer/DdipboxContainer';
import { StoreDescription } from './StoreDescription';

interface StoreDetailHeaderProps {
  store: StoreDetail;
  ddipBoxes: DdipBox[];
}

export const StoreDetailContainer = ({
  store,
  ddipBoxes,
}: StoreDetailHeaderProps) => {
  return (
    <div className="min-w-sm flex w-full flex-col items-center gap-2">
      <DdipboxContainer store={store} ddipBoxes={ddipBoxes} />
      <StoreDescription store={store} />
    </div>
  );
};
