import { useState } from 'react';
import { Clock } from 'lucide-react';
import type { Order } from '@/types/order';
import { useConfirmOrder } from '@/queries/order';
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
import { Textarea } from '@/components/ui/textarea';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';

interface OrderConfirmDialogProps {
  order: Order;
  storeId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const OrderConfirmDialog = ({
  order,
  storeId,
  open,
  onOpenChange,
}: OrderConfirmDialogProps) => {
  const [activeTab, setActiveTab] = useState<'confirm' | 'reject'>('confirm');
  const [pickupTime, setPickupTime] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');

  const confirmOrderMutation = useConfirmOrder({
    orderId: order.orderId,
    storeId,
  });

  const handleConfirm = async () => {
    if (!pickupTime) {
      toast.error('픽업 시간을 입력해주세요.');
      return;
    }

    try {
      await confirmOrderMutation.mutateAsync({
        action: 'CONFIRM',
        pickupTime: new Date(pickupTime).toISOString(),
      });
      onOpenChange(false);
    } catch (error) {
      console.error('주문 확정 실패:', error);
    }
  };

  const handleReject = async () => {
    if (!rejectionReason.trim()) {
      toast.error('거절 사유를 입력해주세요.');
      return;
    }

    try {
      await confirmOrderMutation.mutateAsync({
        action: 'REJECT',
        rejectionReason: rejectionReason.trim(),
      });
      onOpenChange(false);
    } catch (error) {
      console.error('주문 거절 실패:', error);
    }
  };

  // 현재 시간 + 30분을 기본값으로 설정
  const getDefaultPickupTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes() + 30);
    return now.toISOString().slice(0, 16);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>주문 처리</DialogTitle>
          <DialogDescription>
            주문번호: {order.orderId.slice(0, 8)}
          </DialogDescription>
        </DialogHeader>

        <Tabs
          value={activeTab}
          onValueChange={v => setActiveTab(v as 'confirm' | 'reject')}
        >
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="confirm">주문 확정</TabsTrigger>
            <TabsTrigger value="reject">주문 거절</TabsTrigger>
          </TabsList>

          <TabsContent value="confirm" className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="pickup-time">픽업 예상 시간</Label>
              <div className="relative">
                <Clock className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2" />
                <Input
                  id="pickup-time"
                  type="datetime-local"
                  value={pickupTime}
                  onChange={e => setPickupTime(e.target.value)}
                  min={new Date().toISOString().slice(0, 16)}
                  defaultValue={getDefaultPickupTime()}
                  className="pl-10"
                />
              </div>
              <p className="text-muted-foreground text-xs">
                고객에게 안내될 픽업 예상 시간을 설정해주세요.
              </p>
            </div>

            {/* 주문 요약 */}
            <div className="bg-muted space-y-2 rounded-lg p-3">
              <div className="text-sm font-medium">주문 내역</div>
              {order.orderItems.map((item, index) => (
                <div key={index} className="flex justify-between text-sm">
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <span>
                    {(item.unitPrice * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
              <div className="flex justify-between border-t pt-2 font-medium">
                <span>총 금액</span>
                <span>{order.originalPrice.toLocaleString()}원</span>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="reject" className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="rejection-reason">거절 사유</Label>
              <Textarea
                id="rejection-reason"
                placeholder="예: 재료 소진, 영업 종료 등..."
                value={rejectionReason}
                onChange={e => setRejectionReason(e.target.value)}
                className="min-h-[100px]"
              />
              <p className="text-muted-foreground text-xs">
                고객에게 전달될 거절 사유를 입력해주세요.
              </p>
            </div>

            {/* 주문 요약 */}
            <div className="bg-muted space-y-2 rounded-lg p-3">
              <div className="text-sm font-medium">거절할 주문</div>
              {order.orderItems.map((item, index) => (
                <div key={index} className="flex justify-between text-sm">
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <span>
                    {(item.unitPrice * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
            </div>
          </TabsContent>
        </Tabs>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            취소
          </Button>
          {activeTab === 'confirm' ? (
            <Button
              onClick={handleConfirm}
              disabled={confirmOrderMutation.isPending}
            >
              {confirmOrderMutation.isPending ? '처리 중...' : '주문 확정'}
            </Button>
          ) : (
            <Button
              variant="destructive"
              onClick={handleReject}
              disabled={confirmOrderMutation.isPending}
            >
              {confirmOrderMutation.isPending ? '처리 중...' : '주문 거절'}
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default OrderConfirmDialog;
