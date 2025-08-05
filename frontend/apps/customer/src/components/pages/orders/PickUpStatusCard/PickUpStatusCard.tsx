import {
  getPickupStatusMessage,
  getPickupStatusColor,
} from '@/constants/pickupStatus';
import { Calendar, Clock, Hash } from 'lucide-react';

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
  const statusMessage = getPickupStatusMessage(status);
  const statusColor = getPickupStatusColor(status);

  return (
    <div className="w-full max-w-sm rounded-2xl border border-gray-100 bg-white shadow-md">
      <div className="space-y-4 p-4">
        {/* 상태 뱃지 + 주문번호 */}
        <div className="flex items-center justify-between">
          <span
            className={`rounded-full px-3 py-1 text-sm font-semibold ${statusColor} bg-opacity-20`}
          >
            {statusMessage}
          </span>
          <div className="flex items-center space-x-1 text-xs text-gray-400">
            <Hash className="h-3.5 w-3.5" />
            <span>주문번호 {orderId}</span>
          </div>
        </div>

        {/* 시간 정보 */}
        <div className="space-y-2 text-sm text-gray-600">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-1 text-gray-500">
              <Calendar className="h-4 w-4" />
              <span className="text-xs">주문일시</span>
            </div>
            <span className="font-medium text-gray-800">{createdAt}</span>
          </div>

          {pickupCompletedAt && (
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-1 text-gray-500">
                <Clock className="h-4 w-4" />
                <span className="text-xs">픽업시간</span>
              </div>
              <span className="font-medium text-gray-800">
                {pickupCompletedAt}
              </span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PickUpStatusCard;
