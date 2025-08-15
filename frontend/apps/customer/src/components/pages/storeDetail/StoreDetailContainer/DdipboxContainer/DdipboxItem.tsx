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
import { ROUTE_PATH } from '@/router';
import type { DdipBox } from '@/types/store';
import { formatPrice } from '@/utils/priceFormat';
import { useNavigate } from 'react-router-dom';

interface RandomItemProps {
  ddipbox: DdipBox;
}

export const DdipboxItem = ({ ddipbox }: RandomItemProps) => {
  const navigate = useNavigate();

  const message = STORE_DETAIL_MESSAGES;

  return (
    <Card className="flex w-full flex-col gap-1">
      <CardHeader className="text-lg font-bold">
        {ddipbox.ddipboxName}
        <CardDescription className="text-gray-400">
          {ddipbox.description}
        </CardDescription>
      </CardHeader>
      {/* 만약 랜덤일 경우(isRandom)? 메뉴 3개와 더보기 버튼 : 메뉴 5개까지 출력  */}
      <CardContent>
        {ddipbox.items.slice(0, 3).map(item => (
          <li key={item.itemId}>{item.ddipboxItemName}</li>
        ))}
        {ddipbox.items.length >= 4 && (
          <Dialog>
            <DialogTrigger asChild>
              <span className="h-6 text-sm text-gray-500 underline">
                모두 보기
              </span>
            </DialogTrigger>
            <DialogContent className="w-80 sm:max-w-md">
              <DialogDescription>
                {ddipbox.items.map(item => (
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
            {formatPrice(ddipbox.originalPrice)}
          </p>
          <p className="text-2xl font-bold text-amber-500">
            {formatPrice(ddipbox.salePrice)}
          </p>
        </div>
        <Button
          className="bg-amber-500 hover:bg-amber-500 data-[state=on]:bg-amber-600"
          onClick={() => navigate(ROUTE_PATH.PAY)}
        >
          {message.RESERVE_BUTTON_TEXT}
        </Button>
      </CardFooter>
    </Card>
  );
};
