import { MapPin, Clock } from 'lucide-react';
import type { RestaurantInfo as RestaurantInfoType } from '@/types/cart';

export interface RestaurantInfoProps {
  restaurant: RestaurantInfoType;
  pickupTimePrefix: string;
}

const RestaurantInfo = ({
  restaurant,
  pickupTimePrefix,
}: RestaurantInfoProps) => {
  return (
    <div className="rounded-lg bg-gray-50 p-4">
      <h2 className="mb-2 font-medium text-gray-900">{restaurant.name}</h2>
      <div className="flex items-center space-x-4 text-sm text-gray-500">
        <div className="flex items-center space-x-1">
          <Clock className="h-4 w-4" />
          <span>
            {pickupTimePrefix} {restaurant.pickupTime}
          </span>
        </div>
        <div className="flex items-center space-x-1">
          <MapPin className="h-4 w-4" />
          <span>{restaurant.pickupType}</span>
        </div>
      </div>
    </div>
  );
};

export default RestaurantInfo;
