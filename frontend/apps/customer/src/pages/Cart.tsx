// pages/Cart.tsx
import { CART_CONSTANTS } from '@/constants/cart';
import { type CartData } from '@/types/orderflow';

import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import ProductCard from '@/components/pages/cart/ProductCard/ProductCard';
import QuantitySelector from '@/components/pages/cart/QuantitySelector/QuantitySelector';
import PriceSummary from '@/components/pages/cart/PriceSummary/PriceSummary';
import { Separator } from '@/components/ui/separator';
import { Card } from '@/components/ui/card';

import { useCartStore } from '@/store/useCartStore';
import StoreInfo from '@/components/pages/cart/StoreInfo/StoreInfo';
import { useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

interface CartProps {
  onNext: (cartData: CartData) => void;
  onBack: () => void;
}

const Cart = ({ onNext, onBack }: CartProps) => {
  const { items, storeInfo, updateQuantity } = useCartStore(); // Zustand에서 가져오기
  const navigate = useNavigate();

  // 장바구니가 비어있는 경우
  if (!items.length || !storeInfo) {
    return (
      <OrderFlowLayout title="장바구니" currentStep="cart" onBack={onBack}>
        <div className="flex flex-1 flex-col items-center justify-center space-y-4 px-4 py-20">
          <div className="text-5xl">🛒</div>
          <div className="text-center">
            <h3 className="mb-2 text-lg font-semibold text-gray-800">
              장바구니가 비어있어요
            </h3>
            <p className="text-sm text-gray-500">맛있는 띱박스를 담아보세요!</p>
          </div>
          <button
            onClick={() => navigate(ROUTE_PATH.HOME)}
            className="mt-4 rounded-xl bg-amber-500 px-6 py-3 font-medium text-white transition-colors hover:bg-amber-600"
          >
            띱박스 둘러보기
          </button>
        </div>
      </OrderFlowLayout>
    );
  }

  const handleNext = () => {
    const total = items.reduce(
      (sum, item) => sum + item.price * item.quantity,
      0,
    );
    const cartData: CartData = {
      total,
      orderItems: items.map(item => ({
        productId: item.ddipboxId,
        quantity: item.quantity,
        unitPrice: item.price,
        // TODO: discountInfos 내용 확인 필요
        discountInfos: [], // 일단 빈 배열
      })),
    };
    onNext(cartData);
  };

  const total = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0,
  );

  const bottomButton = (
    <button
      onClick={handleNext}
      className="w-full rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
    >
      다음 단계
    </button>
  );

  return (
    <OrderFlowLayout
      title="장바구니"
      currentStep="cart"
      onBack={onBack}
      bottomButton={bottomButton}
    >
      {/* 매장 정보 카드 */}
      <Card className="px-3">
        <StoreInfo
          Store={storeInfo}
          pickupTimePrefix={CART_CONSTANTS.PICKUP_TIME_PREFIX}
        />

        <Separator />

        {/* 상품 정보 카드 */}
        {items.map((item, index) => (
          <Card key={item.ddipboxId}>
            <ProductCard product={item} />
            <QuantitySelector
              quantity={item.quantity}
              onQuantityChange={change => {
                const newQuantity = Math.max(1, item.quantity + change);
                updateQuantity(item.ddipboxId, newQuantity);
              }}
              initialQuantity={1}
              label={CART_CONSTANTS.QUANTITY_LABEL}
            />
            {index < items.length - 1 && <Separator />}
          </Card>
        ))}

        <Separator />

        {/* 가격 요약 카드 */}
        <PriceSummary
          productName="장바구니 합계"
          quantity={items.reduce((sum, item) => sum + item.quantity, 0)}
          total={total}
          totalLabel={CART_CONSTANTS.TOTAL_AMOUNT_LABEL}
        />
      </Card>
    </OrderFlowLayout>
  );
};

export default Cart;
