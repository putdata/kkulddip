interface OrderSummaryProps {
  productName: string;
  quantity: number;
}

export default function OrderSummary({
  productName,
  quantity,
}: OrderSummaryProps) {
  return (
    <div className="space-y-3">
      <p className="text-sm text-gray-900">주문 내역</p>
      <div className="rounded-lg bg-gray-50 p-4">
        <div className="flex items-center justify-between">
          <span className="text-xs text-gray-900">{productName}</span>
          <span className="text-xs text-gray-500">×{quantity}</span>
        </div>
      </div>
    </div>
  );
}
