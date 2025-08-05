import {
  getPickupStatusMessage,
  getPickupStatusColor,
} from '@/constants/pickupStatus';

export interface StatusItem {
  orderId: number;
  status: string;
  createdAt: string;
  pickupCompletedAt?: string;
}

export interface StatusProps {
  item: StatusItem;
}

const PickUpStatusCard = ({ item }: StatusProps) => {
  const { orderId, status, createdAt, pickupCompletedAt } = item;

  return (
    <div className="w-full max-w-sm overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition-shadow">
      <div className="space-y-1 p-2">
        {/* 상태 메시지 */}
        <div className="py-2 text-center">
          <div className={`text-lg font-bold ${getPickupStatusColor(status)}`}>
            {getPickupStatusMessage(status)}{' '}
          </div>
        </div>

        {/* 시간 정보 */}
        <div className="border-t-1 flex flex-col space-y-1 border-gray-400 px-2 text-xs text-gray-500">
          <div className="mt-2">주문일시 {createdAt}</div>
          {pickupCompletedAt && <div>픽업시간 {pickupCompletedAt}</div>}
          <div>주문번호 #{orderId}</div>
        </div>
      </div>
    </div>
  );
};

export default PickUpStatusCard;
