export const StoreDetailTab = () => {
  return (
    <div className="inline-flex h-8 w-96 items-center justify-between overflow-hidden">
      <div className="flex w-96 items-center justify-between overflow-hidden">
        <div className="h-8 w-52 border-b border-black" />
        <div className="text-color-orange-50 w-32 justify-center text-center font-['Noto_Sans_KR'] text-base font-medium">
          가게 정보
        </div>
        <div className="border-color-grey-85 h-8 w-52 border-b" />
        <div className="w-28 justify-center text-center font-['Noto_Sans_KR'] text-base font-medium text-black">
          리뷰 보기
        </div>
      </div>
    </div>
  );
};
