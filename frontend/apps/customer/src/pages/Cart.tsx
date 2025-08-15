// pages/Cart.tsx
import { useState } from 'react';
import { dummyCartProduct, dummyStoreInfo } from '@/dummies/CartDummy';
import { CART_CONSTANTS } from '@/constants/cart';
import { type CartData } from '@/types/orderflow';

import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import StoreInfo from '@/components/pages/cart/StoreInfo/StoreInfo';
import ProductCard from '@/components/pages/cart/ProductCard/ProductCard';
import QuantitySelector from '@/components/pages/cart/QuantitySelector/QuantitySelector';
import PriceSummary from '@/components/pages/cart/PriceSummary/PriceSummary';
import { Separator } from '@/components/ui/separator';
import { Card } from '@/components/ui/card';

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
    onNext(cartData);
  };

  const total = dummyCartProduct.price * quantity;

  const bottomButton = (
    <button
      onClick={handleNext}
      className="fixed bottom-3 w-11/12 rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
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
          Store={dummyStoreInfo}
          pickupTimePrefix={CART_CONSTANTS.PICKUP_TIME_PREFIX}
        />

        <Separator />

        {/* 상품 정보 카드 */}
        <Card className="px-3">
          <ProductCard product={dummyCartProduct} />
          <div className="mt-4 border-t border-gray-100 pt-4">
            <QuantitySelector
              quantity={quantity}
              onQuantityChange={updateQuantity}
              initialQuantity={initialQuantity}
              label={CART_CONSTANTS.QUANTITY_LABEL}
            />
          </div>
        </Card>

        <Separator />

        {/* 가격 요약 카드 */}
        <PriceSummary
          productName={dummyCartProduct.name}
          quantity={quantity}
          total={total}
          totalLabel={CART_CONSTANTS.TOTAL_AMOUNT_LABEL}
        />
      </Card>
    </OrderFlowLayout>
  );
};

export default Cart;
