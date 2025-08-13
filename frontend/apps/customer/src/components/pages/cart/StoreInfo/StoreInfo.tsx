import { MapPin, Clock } from 'lucide-react';
import type { StoreInfo as StoreInfoType } from '@/types/cart';

export interface StoreInfoProps {
  Store: StoreInfoType;
  pickupTimePrefix: string;
}

const StoreInfo = ({ Store, pickupTimePrefix }: StoreInfoProps) => {
  return (
    <div className="rounded-lg bg-gray-50 p-4">
      <div className="mb-2 text-sm font-medium text-gray-900">{Store.name}</div>
      <div className="flex items-center space-x-4 text-xs text-gray-500">
        <div className="flex items-center space-x-1">
          <Clock className="h-4 w-4" />
          <div className="flex flex-col text-xs">
            <span className="text-xs">{pickupTimePrefix}</span>
            <span className="text-xs">{Store.pickupTime}</span>
          </div>
        </div>
        <div className="flex items-center space-x-1">
          <MapPin className="h-4 w-4" />
          <span>{Store.pickupType}</span>
        </div>
      </div>
    </div>
  );
};

export default StoreInfo;
