// import { Tabs } from 'radix-ui';

import { DdipboxContainer } from './DdipboxContainer/DdipboxContainer';
import { StoreDescription } from './StoreDescription';
import { StoreMainMenu } from './StoreMainMenu';

// export const StoreDetailContainer = () => {
//   return (
//     <div className="inline-flex h-8 w-96 items-center justify-between overflow-hidden">
//       <Tabs.Root>
//         <Tabs.List>
//           <Tabs.Trigger />
//         </Tabs.List>
//         <Tabs.Content />
//       </Tabs.Root>

//       <div className="flex w-96 items-center justify-between overflow-hidden">
//         <div className="h-8 w-52 border-b border-black" />
//         <div className="text-color-orange-50 w-32 justify-center text-center font-['Noto_Sans_KR'] text-base font-medium">
//           가게 정보
//         </div>
//         <div className="border-color-grey-85 h-8 w-52 border-b" />
//         <div className="w-28 justify-center text-center font-['Noto_Sans_KR'] text-base font-medium text-black">
//           리뷰 보기
//         </div>
//       </div>
//     </div>
//   );
// };

export function StoreDetailContainer() {
  return (
    <div className="flex w-full max-w-sm flex-col items-center gap-2">
      <StoreMainMenu />
      <DdipboxContainer />
      <StoreDescription />
    </div>
  );
}
