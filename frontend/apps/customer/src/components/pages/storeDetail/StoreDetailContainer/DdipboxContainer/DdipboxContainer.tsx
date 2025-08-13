import type { DdipBox, StoreDetail } from '@/types/store';
import { DdipboxItem } from './DdipboxItem';

interface DdipboxContainerProps {
  store: StoreDetail;
  ddipBoxes: DdipBox[];
}

export const DdipboxContainer = ({
  store,
  ddipBoxes,
}: DdipboxContainerProps) => {
  // TODO: 에러 처리 필요
  if (store) {
    return (
      <div className="flex w-full flex-col items-start justify-start gap-2 bg-white p-5">
        <h2 className="text justify-center text-lg font-bold">띱박스 구성</h2>
        <div className="flex w-full flex-col gap-2 p-2">
          {ddipBoxes.map(ddipBox => (
            <div key={ddipBox.ddipboxId}>
              <DdipboxItem ddipbox={ddipBox} />
            </div>
          ))}
        </div>
      </div>
    );
  }
};
