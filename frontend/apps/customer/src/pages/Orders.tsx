import OrderCard from '@/components/pages/orders/OrderCard/OrderCard';
import { useOrdersWithReviews } from '@/hooks/useOrdersWithReviews';
import { Loader2 } from 'lucide-react';

const Orders = () => {
  const {
    data: orders,
    isLoading,
    isFetching,
    isSuccess,
  } = useOrdersWithReviews();
  if (isLoading || isFetching) {
    return (
      <div className="flex flex-1 items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">주문 내역을 가져오고 있어요...</div>
        </div>
      </div>
    );
  }

  if (isSuccess && (!orders || orders.length === 0)) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center space-y-4 px-4">
        <div className="text-4xl">📋</div>
        <div className="text-center">
          <h3 className="mb-2 text-lg font-semibold text-gray-800">
            아직 주문 내역이 없어요
          </h3>
          <p className="text-sm text-gray-500">맛있는 음식을 주문해보세요!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="px-4 py-4">
      <div className="space-y-4">
        {orders?.map(order => (
          <OrderCard key={order.orderId} item={order} />
        ))}
      </div>
    </div>
  );
};

export default Orders;
