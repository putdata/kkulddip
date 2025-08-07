export const Ddipbox2 = () => {
  return (
    <div className="outline-color-grey-85 inline-flex w-96 items-end justify-between rounded-[10px] bg-white p-2.5 outline outline-1 outline-offset-[-1px]">
      <div className="inline-flex h-32 flex-col items-start justify-between overflow-hidden">
        <div className="inline-flex items-start justify-start gap-3.5 overflow-hidden">
          <div className="w-48 justify-start font-['Inter'] text-base font-normal text-black">
            랜덤 띱박스
          </div>
        </div>
        <div className="h-16 w-20 justify-start font-['Segoe_UI'] text-[10px] font-normal text-black">
          메뉴 구성
          <br />
          메뉴 구성 2<br />
          메뉴 구성
          <br />
          메뉴 구성 2<br />
          메뉴 구성
        </div>
        <div className="inline-flex h-6 w-20 items-center justify-center gap-2.5 overflow-hidden rounded-xl bg-zinc-300">
          <div className="justify-start font-['Inter'] text-[10px] font-normal text-black">
            가능한 구성 보기
          </div>
        </div>
      </div>
      <div className="inline-flex h-28 flex-col items-end justify-start gap-3.5 overflow-hidden">
        <div className="bg-color-grey-60 relative h-5 w-9 overflow-hidden rounded-[36px]">
          <div className="absolute left-[10px] top-[3.30px] justify-start font-['Inter'] text-[10px] font-bold text-white">
            품절
          </div>
        </div>
        <div className="flex flex-col items-end justify-center gap-1.5 overflow-hidden">
          <div className="flex flex-col items-end justify-end gap-1.5 overflow-hidden">
            <div className="bg-color-orange-60 inline-flex h-4 w-16 items-center justify-center gap-2.5 overflow-hidden rounded-[20px] py-[3px]">
              <div className="justify-start font-['Inter'] text-[10px] font-normal text-white">
                70% 할인(fe)
              </div>
            </div>
            <div className="inline-flex w-28 items-center justify-between overflow-hidden">
              <div className="text-color-grey-46 justify-start font-['Inter'] text-xs font-normal line-through">
                38,000원
              </div>
              <div className="text-color-orange-60 justify-start font-['Segoe_UI'] text-sm font-bold">
                7,500원
              </div>
            </div>
            <div className="bg-color-orange-50 inline-flex h-6 w-24 items-center justify-center gap-2.5 overflow-hidden rounded-[10px] px-5">
              <div className="h-4 w-12 justify-start font-['Segoe_UI'] text-sm font-bold text-white">
                구매하기
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
