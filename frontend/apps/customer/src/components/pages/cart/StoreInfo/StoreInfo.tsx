import { Clock } from 'lucide-react';
import type { StoreInfo as StoreInfoType } from '@/types/cart';
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from '@/components/ui/card';

export interface StoreInfoProps {
  Store: StoreInfoType;
  pickupTimePrefix: string;
}

const StoreInfo = ({ Store, pickupTimePrefix }: StoreInfoProps) => {
  return (
    <Card className="gap-3 rounded-lg border-0 bg-white py-0 shadow-none">
      <div className="flex justify-between pr-3">
        <CardContent className="flex flex-col justify-around">
          <CardTitle>{Store.name}</CardTitle>
          <CardDescription>
            <div className="flex items-center gap-1 text-xs">
              <Clock className="h-3 w-3" />
              <span>{pickupTimePrefix}</span>
              <span>{Store.pickupTime}</span>
            </div>
          </CardDescription>
        </CardContent>
        <img
          className="h-15 w-15"
          src={Store.storeImageUrl}
          alt={`${Store.name}의 이미지`}
        />
      </div>
    </Card>
  );
};

export default StoreInfo;
