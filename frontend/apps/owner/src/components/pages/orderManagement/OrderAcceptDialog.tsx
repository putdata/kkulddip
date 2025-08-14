import { useState } from 'react';
import { Clock, Check, Keyboard } from 'lucide-react';
import type { Order } from '@/types/order';
import { useConfirmOrder } from '@/queries/order';
import { getTimeOptions, formatTimeKorean, addMinutes } from '@/utils/dateUtils';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

interface OrderAcceptDialogProps {
  order: Order;
  storeId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const OrderAcceptDialog = ({
  order,
  storeId,
  open,
  onOpenChange,
}: OrderAcceptDialogProps) => {
  // 기본값: 현재 시간 + 30분
  const defaultTime = addMinutes(30);
  const [pickupTime, setPickupTime] = useState(defaultTime.toISOString());
  const [manualHour, setManualHour] = useState(
    defaultTime.getHours().toString().padStart(2, '0'),
  );
  const [manualMinute, setManualMinute] = useState(
    defaultTime.getMinutes().toString().padStart(2, '0'),
  );

  const confirmOrderMutation = useConfirmOrder({
    orderId: order.orderId,
    storeId,
  });

  const handleConfirm = () => {
    confirmOrderMutation.mutate(
      {
        action: 'CONFIRM',
        pickupTime,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          const newDefaultTime = addMinutes(30);
          setPickupTime(newDefaultTime.toISOString());
          setManualHour(newDefaultTime.getHours().toString().padStart(2, '0'));
          setManualMinute(newDefaultTime.getMinutes().toString().padStart(2, '0'));
        },
      },
    );
  };

  const handleManualTimeChange = (hour: string, minute: string) => {
    const h = parseInt(hour, 10);
    const m = parseInt(minute, 10);
    
    if (!isNaN(h) && !isNaN(m) && h >= 0 && h < 24 && m >= 0 && m < 60) {
      const newDate = new Date();
      newDate.setHours(h);
      newDate.setMinutes(m);
      newDate.setSeconds(0);
      newDate.setMilliseconds(0);
      
      // 현재 시간보다 이후인지 확인
      if (newDate > new Date()) {
        setPickupTime(newDate.toISOString());
      }
    }
  };

  const timeOptions = getTimeOptions();

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>주문 수락</DialogTitle>
          <DialogDescription>
            픽업 예상 시간을 설정하고 주문을 확정해주세요
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 픽업 시간 선택 - 탭으로 선택/직접입력 전환 */}
          <Tabs defaultValue="quick" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="quick" className="gap-2">
                <Clock className="h-4 w-4" />
                빠른 선택
              </TabsTrigger>
              <TabsTrigger value="manual" className="gap-2">
                <Keyboard className="h-4 w-4" />
                직접 입력
              </TabsTrigger>
            </TabsList>
            
            <TabsContent value="quick" className="space-y-2">
              <Label htmlFor="pickup-time">픽업 예상 시간</Label>
              <Select value={pickupTime} onValueChange={setPickupTime}>
                <SelectTrigger id="pickup-time" className="w-full">
                  <SelectValue>
                    <div className="flex items-center gap-2">
                      <Clock className="h-4 w-4" />
                      {formatTimeKorean(pickupTime)}
                    </div>
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {timeOptions.map(option => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <p className="text-muted-foreground text-xs">
                30분 단위로 빠르게 선택할 수 있습니다
              </p>
            </TabsContent>
            
            <TabsContent value="manual" className="space-y-2">
              <Label>픽업 예상 시간 직접 입력</Label>
              <div className="flex items-center gap-2">
                <div className="flex-1">
                  <Input
                    type="number"
                    placeholder="시"
                    value={manualHour}
                    onChange={e => {
                      const value = e.target.value.slice(0, 2);
                      setManualHour(value);
                      handleManualTimeChange(value, manualMinute);
                    }}
                    min="0"
                    max="23"
                    className="text-center"
                  />
                </div>
                <span className="text-lg font-semibold">:</span>
                <div className="flex-1">
                  <Input
                    type="number"
                    placeholder="분"
                    value={manualMinute}
                    onChange={e => {
                      const value = e.target.value.slice(0, 2);
                      setManualMinute(value);
                      handleManualTimeChange(manualHour, value);
                    }}
                    min="0"
                    max="59"
                    className="text-center"
                  />
                </div>
              </div>
              <p className="text-muted-foreground text-xs">
                24시간 형식으로 입력해주세요 (예: 14:30)
              </p>
            </TabsContent>
          </Tabs>

          {/* 선택된 시간 표시 */}
          <div className="bg-muted flex items-center justify-center rounded-lg p-4">
            <Badge variant="secondary" className="text-base">
              <Clock className="mr-2 h-4 w-4" />
              예상 픽업 시간: {formatTimeKorean(pickupTime)}
            </Badge>
          </div>

          <Separator />

          {/* 주문 요약 */}
          <div className="space-y-2">
            <div className="text-sm font-medium">주문 내역</div>
            <div className="bg-muted/50 space-y-2 rounded-lg p-3">
              <div className="text-muted-foreground break-all text-xs">
                주문번호: {order.orderId}
              </div>
              {order.orderItems.map((item, index) => (
                <div key={index} className="flex justify-between text-sm">
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <span className="font-medium">
                    {(item.unitPrice * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
              <Separator className="my-2" />
              <div className="flex justify-between font-medium">
                <span>총 금액</span>
                <span className="text-primary">
                  {order.originalPrice.toLocaleString()}원
                </span>
              </div>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={confirmOrderMutation.isPending}
          >
            취소
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={confirmOrderMutation.isPending}
            className="gap-2"
          >
            {confirmOrderMutation.isPending ? (
              '처리 중...'
            ) : (
              <>
                <Check className="h-4 w-4" />
                주문 확정
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default OrderAcceptDialog;