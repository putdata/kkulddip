import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { STORE_DETAIL_MESSAGES } from '@/constants/storeDetail';
import { useCartStore } from '@/store/useCartStore';
import type { StoreInfo } from '@/types/cart';
import type { DdipBox, StoreDetail } from '@/types/store';
import { formatPrice } from '@/utils/priceFormat';
import { toast } from 'sonner';

interface RandomItemProps {
  store: StoreDetail;
  ddipbox: DdipBox;
}

export const DdipboxItem = ({ store, ddipbox }: RandomItemProps) => {
  const {
    addToCart,
    clearAndAddNewStore,
    storeChangeInfo,
    setStoreChangeModal,
  } = useCartStore();

  const storeInfo: StoreInfo = {
    storeId: store.storeId,
    name: store.storeName,
    pickupTime: store.operatingHours,
    address: store.storeAddress,
    storeImageUrl: store.storeProfileImage,
  };

  const isUnavailable = !ddipbox.isActive || ddipbox.remainingQuantity === 0;

  // 장바구니에 추가하기
  const handleAddtoCart = () => {
    if (isUnavailable) {
      if (!ddipbox.isActive) {
        toast.error('현재 판매중이지 않습니다');
      } else {
        toast.error('재고가 없습니다');
      }
      return;
    }

    const result = addToCart(ddipbox, storeInfo);

    if (result.success) {
      toast(result.message);
    }
  };

  // 확인 버튼 클릭 시 함수
  const handleConfirmStoreChange = () => {
    clearAndAddNewStore(); // 매개변수 제거
    toast('새로운 가게 상품으로 교체되었습니다!');
  };

  const message = STORE_DETAIL_MESSAGES;

  return (
    <Card className="flex w-full flex-col gap-1">
      <CardHeader className="text-lg font-bold">
        {ddipbox.ddipboxName}
        <CardDescription className="text-gray-400">
          {ddipbox.description}
        </CardDescription>
      </CardHeader>
      {/* 만약 랜덤일 경우(isRandom)? 메뉴 3개와 더보기 버튼 : 메뉴 5개까지 출력  */}
      <CardContent>
        {ddipbox.items.slice(0, 3).map(item => (
          <li key={item.itemId}>{item.ddipboxItemName}</li>
        ))}
        {ddipbox.items.length >= 4 && (
          <Dialog>
            <DialogTrigger asChild>
              <span className="h-6 text-sm text-gray-500 underline">
                모두 보기
              </span>
            </DialogTrigger>
            <DialogContent className="w-80 sm:max-w-md">
              <DialogDescription>
                {ddipbox.items.map(item => (
                  <li key={item.itemId}>{item.ddipboxItemName}</li>
                ))}
              </DialogDescription>
            </DialogContent>
          </Dialog>
        )}
      </CardContent>

      <CardFooter className="justify-between">
        <div className="flex items-end gap-1">
          <p className="text-gray-500 line-through">
            {formatPrice(ddipbox.originalPrice)}
          </p>
          <p className="text-2xl font-bold text-amber-500">
            {formatPrice(ddipbox.salePrice)}
          </p>
        </div>
        <div className="flex flex-col items-end gap-2">
          {!isUnavailable && (
            <span className="text-xs text-gray-500">
              재고 {ddipbox.remainingQuantity}개
            </span>
          )}
          <Button
            className={`${
              isUnavailable
                ? 'cursor-not-allowed bg-gray-400 hover:bg-gray-400'
                : 'bg-amber-500 hover:bg-amber-500 data-[state=on]:bg-amber-600'
            }`}
            onClick={handleAddtoCart}
            disabled={isUnavailable}
          >
            {!ddipbox.isActive
              ? '판매 중지'
              : ddipbox.remainingQuantity === 0
                ? '품절'
                : message.RESERVE_BUTTON_TEXT}
          </Button>
        </div>
      </CardFooter>

      {storeChangeInfo.show && (
        <Dialog
          open={storeChangeInfo.show}
          onOpenChange={open => setStoreChangeModal(open)}
        >
          <DialogContent>
            <DialogHeader>
              <DialogTitle>가게 변경 확인</DialogTitle>
              <DialogDescription>
                동일한 가게의 띱박스만 장바구니에 담을 수 있어요! 기존
                장바구니를 초기화하고 새로운 가게의 띱박스를 추가할까요?
              </DialogDescription>
            </DialogHeader>
            <div className="mt-4 flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => setStoreChangeModal(false)}
              >
                아니오
              </Button>
              <Button onClick={handleConfirmStoreChange}>네</Button>
            </div>
          </DialogContent>
        </Dialog>
      )}
    </Card>
  );
};
