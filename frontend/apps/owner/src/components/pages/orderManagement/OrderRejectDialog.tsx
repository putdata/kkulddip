import { useState } from 'react';
import {
  X,
  AlertCircle,
  Package,
  Clock,
  Users,
  Timer,
  MessageSquare,
} from 'lucide-react';
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
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

interface OrderRejectDialogProps {
  order: Order;
  storeId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const REJECTION_REASONS = [
  { value: 'out_of_stock', label: '재료 소진', icon: Package },
  { value: 'closing_soon', label: '영업 종료', icon: Clock },
  { value: 'too_many_orders', label: '주문 과다', icon: Users },
  { value: 'preparation_time', label: '준비 시간 부족', icon: Timer },
  { value: 'other', label: '기타', icon: MessageSquare },
] as const;

const OrderRejectDialog = ({
  order,
  storeId,
  open,
  onOpenChange,
}: OrderRejectDialogProps) => {
  const [selectedReason, setSelectedReason] = useState('');
  const [customReason, setCustomReason] = useState('');

  const confirmOrderMutation = useConfirmOrder({
    orderId: order.orderId,
    storeId,
  });

  const handleReject = () => {
    const rejectionReason =
      selectedReason === 'other'
        ? customReason
        : REJECTION_REASONS.find(r => r.value === selectedReason)?.label || '';

    if (!rejectionReason.trim()) {
      return;
    }

    confirmOrderMutation.mutate(
      {
        action: 'REJECT',
        rejectionReason: rejectionReason.trim(),
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          setSelectedReason('');
          setCustomReason('');
        },
      },
    );
  };

  const isValid =
    selectedReason &&
    (selectedReason !== 'other' || customReason.trim().length > 0);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>주문 거절</DialogTitle>
          <DialogDescription>
            고객에게 전달될 거절 사유를 선택해주세요
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 거절 사유 선택 - 카드형 UI */}
          <div className="space-y-3">
            <Label>거절 사유를 선택해주세요</Label>
            <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
              {REJECTION_REASONS.map(reason => {
                const Icon = reason.icon;
                const isSelected = selectedReason === reason.value;
                return (
                  <button
                    key={reason.value}
                    type="button"
                    onClick={() => setSelectedReason(reason.value)}
                    className={cn(
                      'flex flex-col items-center justify-center gap-1.5 rounded-lg border-2 p-3 transition-all hover:bg-accent',
                      isSelected
                        ? 'border-primary bg-primary/5 text-primary'
                        : 'border-border bg-background',
                    )}
                  >
                    <Icon className="h-5 w-5" />
                    <span className="text-xs font-medium">{reason.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* 기타 사유 입력 */}
          {selectedReason === 'other' && (
            <div className="space-y-2 animate-in slide-in-from-top-2">
              <Label htmlFor="custom-reason">구체적인 사유를 입력해주세요</Label>
              <Textarea
                id="custom-reason"
                placeholder="예: 특정 메뉴 준비 불가, 오늘 조기 마감 등..."
                value={customReason}
                onChange={e => setCustomReason(e.target.value)}
                className="min-h-[100px] resize-none"
                autoFocus
              />
              <p className="text-muted-foreground text-xs">
                고객에게 전달될 메시지입니다. 정중하게 작성해주세요.
              </p>
            </div>
          )}

          {/* 경고 메시지 */}
          <Alert variant="destructive" className="border-orange-200 bg-orange-50">
            <AlertCircle className="h-4 w-4 text-orange-600" />
            <AlertDescription className="text-orange-800">
              주문을 거절하면 고객에게 알림이 전송되며, 이 작업은 취소할 수
              없습니다.
            </AlertDescription>
          </Alert>

          <Separator />

          {/* 거절할 주문 정보 */}
          <div className="space-y-2">
            <div className="text-sm font-medium">거절할 주문</div>
            <div className="bg-muted/50 space-y-2 rounded-lg p-3">
              <div className="text-muted-foreground break-all text-xs">
                주문번호: {order.orderId}
              </div>
              {order.orderItems.map((item, index) => (
                <div key={index} className="flex justify-between text-sm">
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <span className="text-muted-foreground">
                    {(item.unitPrice * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
              <Separator className="my-2" />
              <div className="text-muted-foreground flex justify-between text-sm">
                <span>총 금액</span>
                <span>{order.originalPrice.toLocaleString()}원</span>
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
            variant="destructive"
            onClick={handleReject}
            disabled={!isValid || confirmOrderMutation.isPending}
            className="gap-2"
          >
            {confirmOrderMutation.isPending ? (
              '처리 중...'
            ) : (
              <>
                <X className="h-4 w-4" />
                주문 거절
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default OrderRejectDialog;