import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import { STORE_DETAIL_MESSAGES } from '@/constants/storeDetail';
import type { DdipBoxSummaryDto } from '@/dummies/storeDetailDummy';

interface ItemProps {
  ddipbox: DdipBoxSummaryDto;
}

export const DdipboxItem = ({ ddipbox }: ItemProps) => {
  const message = STORE_DETAIL_MESSAGES;
  return (
    <Card className="flex w-full flex-col gap-1">
      <CardHeader className="text-lg font-bold">
        {ddipbox.ddipboxName}
      </CardHeader>
      <CardContent className="flex justify-between">
        <div>
          {ddipbox.ddipBoxItem.map(item => (
            <li>{item.ddipboxItemName}</li>
          ))}
        </div>
      </CardContent>
      <CardFooter className="justify-between">
        <div className="flex items-end gap-1">
          <p className="text-gray-500 line-through">
            {ddipbox.originalPrice}원
          </p>
          <p className="text-2xl font-bold text-amber-500">
            {ddipbox.salePrice}원
          </p>
        </div>
        <Button className="bg-amber-500">{message.RESERVE_BUTTON_TEXT}</Button>
      </CardFooter>
    </Card>
  );
};
