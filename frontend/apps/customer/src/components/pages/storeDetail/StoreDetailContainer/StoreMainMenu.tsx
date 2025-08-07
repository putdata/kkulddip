export const StoreMainMenu = () => {
  return (
    <div className="flex flex-col items-start justify-start self-stretch bg-white px-5 pt-5">
      <div className="flex flex-col items-start justify-start self-stretch">
        <div className="text-color-grey-20 justify-center self-stretch font-['Noto_Sans_KR'] text-lg font-bold leading-relaxed">
          대표 메뉴
        </div>
      </div>
      <div className="border-color-grey-94 inline-flex items-center justify-start gap-4 self-stretch border-b-[0.80px] py-3">
        <div className="inline-flex w-10 flex-col items-center justify-start">
          <div className="text-color-grey-20 justify-center text-center font-sans text-2xl font-normal leading-loose">
            🥐
          </div>
        </div>
        <div className="inline-flex flex-1 flex-col items-start justify-start gap-[3.40px]">
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-20 justify-center self-stretch font-['Noto_Sans_KR'] text-base font-bold leading-snug">
              크루아상
            </div>
          </div>
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-40 justify-center self-stretch font-['Noto_Sans_KR'] text-xs leading-tight">
              바삭한 겉면과 부드러운 속살의 프랑스 정통 크루아
              <br />상
            </div>
          </div>
        </div>
      </div>
      <div className="border-color-grey-94 inline-flex items-center justify-start gap-4 self-stretch border-b-[0.80px] py-3">
        <div className="inline-flex w-10 flex-col items-center justify-start">
          <div className="text-color-grey-20 justify-center text-center font-sans text-2xl font-normal leading-loose">
            🍞
          </div>
        </div>
        <div className="inline-flex flex-1 flex-col items-start justify-start gap-[3.30px]">
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-20 justify-center self-stretch font-['Noto_Sans_KR'] text-base font-bold leading-snug">
              소금빵
            </div>
          </div>
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-40 justify-center self-stretch font-['Noto_Sans_KR'] text-xs leading-tight">
              겉은 바삭, 속은 촉촉한 인기 No.1 소금빵
            </div>
          </div>
        </div>
      </div>
      <div className="inline-flex items-center justify-start gap-4 self-stretch py-3">
        <div className="inline-flex w-10 flex-col items-center justify-start">
          <div className="text-color-grey-20 justify-center text-center font-sans text-2xl font-normal leading-loose">
            🧁
          </div>
        </div>
        <div className="inline-flex flex-1 flex-col items-start justify-start gap-[3.40px]">
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-20 justify-center self-stretch font-['Noto_Sans_KR'] text-base font-bold leading-snug">
              티라미수 케이크
            </div>
          </div>
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-40 justify-center self-stretch font-['Noto_Sans_KR'] text-xs leading-tight">
              진한 커피향과 부드러운 마스카포네 치즈의 완벽한
              <br />
              조화
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
