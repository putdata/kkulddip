import PaymentInfoCard, {
  type PaymentInfo,
} from '@/components/pages/orders/PaymentInfoCard/PaymentInfoCard';
import PickUpStatusCard, {
  type StatusItem,
} from '@/components/pages/orders/PickUpStatusCard/PickUpStatusCard';

const dummyStatusData: StatusItem = {
  orderId: 67890,
  status: 'COMPLETED',
  createdAt: '2024-01-15 14:30',
  pickupCompletedAt: '2024-01-15 15:45',
};

const dummyPaymentInfo: PaymentInfo = {
  totalOriginalPrice: 18800,
  totalDiscount: 6500,
  discounts: [
    {
      discountHistoryId: 1,
      discountAmount: 4100,
      discountType: '꿀띱 MEMBERSHIP',
    },
    {
      discountHistoryId: 2,
      discountAmount: 1500,
      discountType: '즉시할인',
    },
    {
      discountHistoryId: 3,
      discountAmount: 900,
      discountType: 'COUPON',
    },
  ],
  totalItems: 3,
  paymentMethod: '토스페이',
};

const OrderDetail = () => {
  return (
    <div className="mx-2 space-y-2 pb-16 pt-12">
      <div className="mt-3 px-1 text-sm font-semibold">픽업 상태</div>

      <PickUpStatusCard item={dummyStatusData} />
      <div className="mt-3 px-1 text-sm font-semibold">결제 정보</div>
      <PaymentInfoCard paymentInfo={dummyPaymentInfo} />
    </div>
  );
};

export default OrderDetail;
