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
import type { DdipBoxSummaryDto } from '@/types/store';

interface RandomItemProps {
  ddipbox: DdipBoxSummaryDto;
}

export const DdipboxItem = ({ ddipbox }: RandomItemProps) => {
  const message = STORE_DETAIL_MESSAGES;

  return (
    <Card className="flex w-full flex-col gap-1">
      {/* isRandom 일 때만 렌더링 */}

      <CardHeader className="text-lg font-bold">
        {ddipbox.ddipboxName}
        {ddipbox.isRandom && (
          <CardDescription className="text-gray-400">
            {message.RANDOM_DDIPBOX_DESCRIPTION}
          </CardDescription>
        )}
      </CardHeader>
      {/* 만약 랜덤일 경우(isRandom)? 메뉴 3개와 더보기 버튼 : 메뉴 5개까지 출력  */}
      {ddipbox.isRandom ? (
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
              <DialogContent className="w-80 sm:max-w-md">
                <DialogDescription>
                  {ddipbox.ddipBoxItem.map(item => (
                    <li key={item.itemId}>{item.ddipboxItemName}</li>
                  ))}
                </DialogDescription>
              </DialogContent>
            </Dialog>
          )}
        </CardContent>
      ) : (
        <CardContent className="flex justify-between">
          <div>
            {ddipbox.ddipBoxItem.map(item => (
              <li>{item.ddipboxItemName}</li>
            ))}
          </div>
        </CardContent>
      )}
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
