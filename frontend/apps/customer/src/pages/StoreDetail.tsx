import { StoreDetailHeader } from '@/components/pages/storeDetail/StoreDetailHeader/StoreDetailHeader';
import { StoreDetailTab } from '@/components/pages/storeDetail/StoreDetailTab';
import { mockStoreDetail } from '@/dummies/storeDetailDummy';
import { useParams } from 'react-router-dom';

const StoreDetail = () => {
  //   const params = useParams();
  //   const storeId = params.storeId;

  const store = mockStoreDetail;

  return (
    <div className="gap-2 bg-gray-300">
      <StoreDetailHeader store={store} />
      <StoreDetailTab />
      <div className="flex flex-col items-center justify-center gap-2.5 self-stretch overflow-hidden pb-3.5">
        <div className="bg-color-white-solid flex flex-col items-start justify-start self-stretch px-5 pt-5">
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
        <div className="outline-color-grey-85 inline-flex w-96 items-end justify-between rounded-[10px] bg-white p-2.5 outline outline-1 outline-offset-[-1px]">
          <div className="inline-flex flex-col items-start justify-start gap-2.5 overflow-hidden">
            <div className="inline-flex items-start justify-start gap-3.5 overflow-hidden">
              <div className="w-48 justify-start font-['Inter'] text-base font-normal text-black">
                띱박스 이름
              </div>
            </div>
            <div className="h-24 w-20 justify-start font-['Segoe_UI'] text-[10px] font-normal text-black">
              메뉴 구성
              <br />
              메뉴 구성 2<br />
              메뉴 구성
              <br />
              메뉴 구성 2<br />
              메뉴 구성
            </div>
          </div>
          <div className="inline-flex h-28 flex-col items-end justify-start gap-3.5 overflow-hidden">
            <div className="relative h-5 w-16 overflow-hidden rounded-[36px] bg-green-500">
              <div className="absolute left-[17px] top-[3.75px] justify-start font-['Inter'] text-[10px] font-bold text-white">
                예약가능
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
        <div className="bg-color-white-solid flex flex-col items-start justify-start self-stretch px-5 pb-7 pt-5">
          <div className="flex flex-col items-start justify-start self-stretch">
            <div className="text-color-grey-20 justify-center self-stretch font-['Noto_Sans_KR'] text-lg font-bold leading-relaxed">
              가게 소개
            </div>
          </div>
          <div className="flex flex-col items-start justify-start gap-5 self-stretch">
            <div className="text-color-grey-40 justify-center font-['Noto_Sans_KR'] text-sm leading-snug">
              안녕하세요! 뚜레주르 역삼점입니다 🥐
            </div>
            <div className="text-color-grey-40 justify-center font-['Noto_Sans_KR'] text-sm leading-snug">
              저희는 매일 새벽부터 정성스럽게 구운 신선한 빵과 케이
              <br />
              크를 준비하고 있습니다. 프랑스 전통 베이킹 기법으로 만<br />든
              크루아상부터 한국인의 입맛에 맞는 소금빵까지, 다양한
              <br />
              빵들을 만나보실 수 있어요.
            </div>
            <div className="text-color-grey-40 justify-center font-['Noto_Sans_KR'] text-sm leading-snug">
              꿀띱을 통해 당일 남은 신선한 빵들을 더욱 합리적인 가격
              <br />에 드실 수 있도록 사장님이 직접 구성해드리는 특별한 띱
              <br />
              박스를 준비했습니다. 오늘은 어떤 맛있는 빵들이 들어갈지
              <br />
              기대해주세요! ✨
            </div>
          </div>
        </div>
        <div className="flex h-80 flex-col items-start justify-start gap-2.5 self-stretch overflow-hidden bg-white px-5 py-3.5">
          <div className="justify-center text-center font-['Noto_Sans_KR'] text-base font-bold leading-none text-black">
            위치
          </div>
          <img
            className="h-60 self-stretch rounded-[10px]"
            src="https://placehold.co/384x237"
          />
        </div>
      </div>
    </div>
  );
};

export default StoreDetail;
