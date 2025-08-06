import { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { dummyCartProduct, dummyStoreInfo } from '@/dummies/CartDummy';
import { CART_CONSTANTS } from '@/constants/cart';
import { type CartData } from '@/types/orderflow';

import StoreInfo from '@/components/pages/cart/StoreInfo/StoreInfo';
import ProductCard from '@/components/pages/cart/ProductCard/ProductCard';
import QuantitySelector from '@/components/pages/cart/QuantitySelector/QuantitySelector';
import PriceSummary from '@/components/pages/cart/PriceSummary/PriceSummary';

interface CartProps {
  onNext: (cartData: CartData) => void;
  onBack: () => void;
  initialQuantity?: number;
}

const Cart = ({ onNext, onBack, initialQuantity = 1 }: CartProps) => {
  const [quantity, setQuantity] = useState(initialQuantity);

  const updateQuantity = (change: number) => {
    setQuantity(prev => Math.max(CART_CONSTANTS.MIN_QUANTITY, prev + change));
  };

  const handleNext = () => {
    const total = dummyCartProduct.price * quantity;
    const cartData: CartData = {
      quantity,
      total,
      productId: dummyCartProduct.id,
    };
    onNext(cartData); // 퍼넬 컨테이너로 데이터 전달
  };

  const total = dummyCartProduct.price * quantity;

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white pb-20">
      {/* 헤더 */}
      <div className="relative flex w-full flex-row items-center p-3">
        <button onClick={onBack} className="rounded-full p-2">
          <ArrowLeft className="h-6 w-6 text-gray-700" />
        </button>
        <h1 className="absolute left-1/2 -translate-x-1/2 text-lg font-semibold text-gray-700">
          장바구니
        </h1>
      </div>
      {/* 전체 카드 */}
      <div className="w-full overflow-hidden rounded-[2.5rem] bg-amber-50 pt-8 shadow-lg">
        {/* 진행 상태 표시 */}
        <div className="px-6 pb-6">
          <div className="flex items-center justify-between">
            {/* 장바구니 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-orange-300">
                <span className="text-lg text-white">🛒</span>
              </div>
              <span className="text-xs text-gray-500">장바구니</span>
            </div>

            {/* 연결선 */}
            <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

            {/* 결제 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-200">
                <span className="pb-2 text-2xl text-gray-500">💳</span>
              </div>
              <span className="text-xs text-gray-400">결제</span>
            </div>

            {/* 연결선 */}
            <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

            {/* 완료 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-200">
                <span className="text-lg text-gray-400">✓</span>
              </div>
              <span className="text-xs text-gray-400">완료</span>
            </div>
          </div>
        </div>

        {/* 메인 콘텐츠 스크롤 영역 */}
        <div className="rounded-t-4xl flex-1 bg-amber-100 p-6">
          <div className="space-y-3">
            {/* 매장 정보 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <StoreInfo
                Store={dummyStoreInfo}
                pickupTimePrefix={CART_CONSTANTS.PICKUP_TIME_PREFIX}
              />
            </div>

            {/* 상품 정보 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <ProductCard product={dummyCartProduct} />

              <div className="mt-4 border-t border-gray-100 pt-4">
                <QuantitySelector
                  quantity={quantity}
                  onQuantityChange={updateQuantity}
                  initialQuantity={initialQuantity}
                  label={CART_CONSTANTS.QUANTITY_LABEL}
                />
              </div>
            </div>

            {/* 가격 요약 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <PriceSummary
                productName={dummyCartProduct.name}
                quantity={quantity}
                total={total}
                totalLabel={CART_CONSTANTS.TOTAL_AMOUNT_LABEL}
              />
            </div>
          </div>
        </div>
      </div>
      {/* 하단 결제 버튼 */}
      <button
        onClick={handleNext}
        className="fixed bottom-3 w-11/12 rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
      >
        다음 단계
      </button>
    </div>
  );
};

export default Cart;
