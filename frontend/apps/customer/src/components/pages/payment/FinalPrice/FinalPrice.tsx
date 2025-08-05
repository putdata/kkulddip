interface FinalPriceProps {
  orderAmount: number;
  discount: number;
}

export default function FinalPrice({ orderAmount, discount }: FinalPriceProps) {
  const finalAmount = orderAmount - discount;

  return (
    <div className="space-y-2 rounded-lg bg-gray-50 p-4">
      <div className="flex justify-between text-sm">
        <span>주문금액</span>
        <span>{orderAmount.toLocaleString()}원</span>
      </div>
      <div className="flex justify-between text-sm text-green-600">
        <span>할인금액</span>
        <span>-{discount.toLocaleString()}원</span>
      </div>
      <div className="flex justify-between border-t pt-2 text-lg font-bold">
        <span>최종 결제금액</span>
        <span>{finalAmount.toLocaleString()}원</span>
      </div>
    </div>
  );
}
