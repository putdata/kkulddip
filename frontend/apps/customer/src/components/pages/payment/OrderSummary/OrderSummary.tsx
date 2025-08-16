import { useCartStore } from '@/store/useCartStore';

interface OrderSummaryProps {
  orderItems: Array<{
    productId: number;
    quantity: number;
    unitPrice: number;
  }>;
}

export default function OrderSummary({ orderItems }: OrderSummaryProps) {
  const { items } = useCartStore();
  
  const totalQuantity = orderItems.reduce(
    (sum, item) => sum + item.quantity,
    0,
  );

  return (
    <div className="space-y-3">
      <p className="text-sm text-gray-900">주문 내역</p>
      {items.map((item, index) => (
        <div key={index} className="rounded-lg bg-gray-50 p-4">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-900">{item.name}</span>
            <span className="text-xs text-gray-500">×{item.quantity}</span>
          </div>
        </div>
      ))}
      <div className="rounded-lg bg-amber-50 p-4">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-gray-900">총 수량</span>
          <span className="text-xs font-medium text-gray-900">
            ×{totalQuantity}
          </span>
        </div>
      </div>
    </div>
  );
}
