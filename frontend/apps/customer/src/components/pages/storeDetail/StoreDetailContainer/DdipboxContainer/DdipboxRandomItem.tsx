import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTrigger,
} from '@/components/ui/dialog';
import { STORE_DETAIL_MESSAGES } from '@/constants/storeDetail';
import type { DdipBoxSummaryDto } from '@/dummies/storeDetailDummy';

interface RandomItemProps {
  ddipbox: DdipBoxSummaryDto;
}

export const DdipboxRandomItem = ({ ddipbox }: RandomItemProps) => {
  const message = STORE_DETAIL_MESSAGES;

  return (
    <Card className="flex w-full flex-col gap-1">
      <CardHeader className="text-lg font-bold">
        {ddipbox.ddipboxName}
        <CardDescription className="text-gray-400">
          {message.RANDOM_DDIPBOX_DESCRIPTION}
        </CardDescription>
      </CardHeader>
      <CardContent>
        {ddipbox.ddipBoxItem.slice(0, 3).map(item => (
          <li key={item.itemId}>{item.ddipboxItemName}</li>
        ))}
        {ddipbox.ddipBoxItem.length >= 4 && (
          <Dialog>
            <DialogTrigger asChild>
              <span className="h-6 text-sm text-gray-500 underline">
                모두 보기
              </span>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogDescription>
                {ddipbox.ddipBoxItem.map(item => (
                  <li key={item.itemId}>{item.ddipboxItemName}</li>
                ))}
              </DialogDescription>
            </DialogContent>
          </Dialog>
        )}
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
