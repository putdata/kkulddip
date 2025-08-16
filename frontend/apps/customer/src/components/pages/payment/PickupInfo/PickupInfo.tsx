import { MapPin } from 'lucide-react';

interface PickupInfoProps {
  storeName: string;
  address: string;
  pickupTime: string;
}

export default function PickupInfo({ storeName, address }: PickupInfoProps) {
  return (
    <div className="space-y-3">
      <p className="text-sm font-medium text-gray-900">픽업 정보</p>
      <div className="rounded-lg bg-gray-50 p-4">
        <div className="flex items-start space-x-3">
          <MapPin className="mt-0.5 h-5 w-5 text-gray-600" />
          <div className="flex-1">
            <p className="text-xs font-medium text-gray-900">{storeName}</p>
            <p className="mt-1 text-xs text-gray-500">{address}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
