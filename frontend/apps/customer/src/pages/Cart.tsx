import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { dummyCartProduct, dummyRestaurantInfo } from '@/dummies/CartDummy';
import { CART_CONSTANTS } from '@/constants/cart';
import { ROUTE_PATH } from '@/router';

import RestaurantInfo from '@/components/pages/cart/RestaurantInfo/RestaurantInfo';
import ProductCard from '@/components/pages/cart/ProductCard/ProductCard';
import QuantitySelector from '@/components/pages/cart/QuantitySelector/QuantitySelector';
import PriceSummary from '@/components/pages/cart/PriceSummary/PriceSummary';
import PaymentButton from '@/components/pages/cart/PaymentButton/PaymentButton';

const Cart = () => {
  const navigate = useNavigate();
  const [quantity, setQuantity] = useState(1);

  const updateQuantity = (change: number) => {
    setQuantity(prev => Math.max(CART_CONSTANTS.MIN_QUANTITY, prev + change));
  };

  const handleNext = () => {
    navigate(ROUTE_PATH.PAYMENT);
  };

  const total = dummyCartProduct.price * quantity;

  return (
    <div className="mx-auto min-h-screen max-w-md bg-white pb-16 pt-16">
      <div className="space-y-6 px-4 py-4">
        <RestaurantInfo
          restaurant={dummyRestaurantInfo}
          pickupTimePrefix={CART_CONSTANTS.PICKUP_TIME_PREFIX}
        />

        <div className="space-y-4">
          <ProductCard product={dummyCartProduct} />

          <QuantitySelector
            quantity={quantity}
            onQuantityChange={updateQuantity}
            minQuantity={CART_CONSTANTS.MIN_QUANTITY}
            label={CART_CONSTANTS.QUANTITY_LABEL}
          />
        </div>

        <PriceSummary
          productName={dummyCartProduct.name}
          quantity={quantity}
          total={total}
          totalLabel={CART_CONSTANTS.TOTAL_AMOUNT_LABEL}
        />
      </div>

      <PaymentButton
        total={total}
        buttonSuffix={CART_CONSTANTS.PAYMENT_BUTTON_SUFFIX}
        onNext={handleNext}
      />
    </div>
  );
};

export default Cart;
