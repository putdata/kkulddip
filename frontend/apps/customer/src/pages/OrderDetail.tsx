import { useParams } from 'react-router-dom';
import { useOrders } from '@/hooks/useOrders';
import PaymentInfoCard, {
  type PaymentInfo,
} from '@/components/pages/orders/PaymentInfoCard/PaymentInfoCard';
import PickUpStatusCard, {
  type StatusItem,
} from '@/components/pages/orders/PickUpStatusCard/PickUpStatusCard';

const OrderDetail = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const { data: orders, isLoading, error } = useOrders();

  const orderDetail = orders?.find(order => order.orderId === orderId);

  if (isLoading) {
    return (
      <div className="mx-2 space-y-2">
        <div className="flex h-40 items-center justify-center">
          <div className="text-gray-500">주문 상세를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  if (error || !orderDetail) {
    return (
      <div className="mx-2 space-y-2">
        <div className="flex h-40 items-center justify-center">
          <div className="text-red-500">
            주문 상세를 불러오는데 실패했습니다
          </div>
        </div>
      </div>
    );
  }

  const statusData: StatusItem = {
    orderId: Number(orderDetail.orderId),
    status: orderDetail.orderStatus as
      | 'CREATED'
      | 'PAYMENT_PENDING'
      | 'PAID'
      | 'AWAITING_CONFIRMATION'
      | 'CONFIRMED'
      | 'CANCELLED'
      | 'FAILED',
    createdAt: orderDetail.orderDate,
    pickupCompletedAt: orderDetail.pickupTime,
  };

  const paymentInfo: PaymentInfo = {
    totalOriginalPrice: orderDetail.originalPrice,
    totalDiscount: orderDetail.originalPrice - orderDetail.finalPrice,
    discounts: [
      {
        discountHistoryId: 1,
        discountAmount: orderDetail.originalPrice - orderDetail.finalPrice,
        discountType: '꿀띱 할인',
      },
    ],
    totalItems: orderDetail.orderItems.reduce(
      (sum, item) => sum + item.quantity,
      0,
    ),
  };

  return (
    <div className="mx-2 space-y-2">
      <div className="text-md mt-3 px-1 pt-2 font-semibold">픽업 상태</div>
      <PickUpStatusCard item={statusData} />

      <div className="text-md mt-3 px-1 pt-4 font-semibold">결제 정보</div>
      <PaymentInfoCard paymentInfo={paymentInfo} />
    </div>
  );
};

export default OrderDetail;
