import { MapPin, Clock } from 'lucide-react';

interface PickupInfoProps {
  storeName: string;
  address: string;
  pickupTime: string;
}

export default function PickupInfo({
  storeName,
  address,
  pickupTime,
}: PickupInfoProps) {
  return (
    <div className="space-y-3">
      <h3 className="font-medium text-gray-900">픽업 정보</h3>
      <div className="rounded-lg bg-gray-50 p-4">
        <div className="flex items-start space-x-3">
          <MapPin className="mt-0.5 h-5 w-5 text-gray-600" />
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-900">{storeName}</p>
            <p className="mt-1 text-sm text-gray-500">{address}</p>
            <div className="mt-2 flex items-center space-x-1">
              <Clock className="h-4 w-4 text-gray-500" />
              <span className="text-sm text-gray-500">
                픽업 예상시간: {pickupTime}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
