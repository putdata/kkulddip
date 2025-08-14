import { useState, useEffect } from 'react';
import { RefreshCw, Package, Hash, RotateCcw } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { useUpdateDdipboxQuantity } from '@/queries/ddipbox';
import { useDdipboxPricing } from '@/hooks/useDdipboxPricing';
import type { DdipBox, UpdateDdipBoxQuantityRequest } from '@/types/ddipbox';

interface DdipboxQuantityDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  ddipbox: DdipBox;
  storeId: number;
}

const DdipboxQuantityDialog = ({
  open,
  onOpenChange,
  ddipbox,
  storeId,
}: DdipboxQuantityDialogProps) => {
  const [operationType, setOperationType] = useState<'direct' | 'reset'>(
    'direct',
  );
  const [remainingQuantity, setRemainingQuantity] = useState(
    ddipbox.remainingQuantity,
  );
  const [dailyQuantity, setDailyQuantity] = useState(ddipbox.dailyQuantity);

  const updateQuantityMutation = useUpdateDdipboxQuantity();
  const { getStockStatus } = useDdipboxPricing();

  // 다이얼로그가 열릴 때마다 초기화
  useEffect(() => {
    if (open) {
      setOperationType('direct');
      setRemainingQuantity(ddipbox.remainingQuantity);
      setDailyQuantity(ddipbox.dailyQuantity);
    }
  }, [open, ddipbox]);

  const validateForm = () => {
    if (operationType === 'direct') {
      if (remainingQuantity < 0) {
        return false;
      }
    } else {
      if (dailyQuantity < 1) {
        return false;
      }
    }

    return true;
  };

  const handleSubmit = () => {
    if (!validateForm()) {
      return;
    }

    const requestData: UpdateDdipBoxQuantityRequest = {};

    if (operationType === 'direct') {
      requestData.remainingQuantity = remainingQuantity;
      requestData.resetRemaining = false;
    } else {
      requestData.dailyQuantity = dailyQuantity;
      requestData.resetRemaining = true;
    }

    updateQuantityMutation.mutate(
      {
        storeId,
        ddipboxId: ddipbox.ddipboxId,
        data: requestData,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
        },
      },
    );
  };

  const currentStatus = getStockStatus(
    ddipbox.remainingQuantity,
    ddipbox.dailyQuantity,
  );
  const previewStatus =
    operationType === 'direct'
      ? getStockStatus(remainingQuantity, ddipbox.dailyQuantity)
      : getStockStatus(dailyQuantity, dailyQuantity);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <RefreshCw className="h-5 w-5" />
            재고 관리
          </DialogTitle>
          <DialogDescription>
            {ddipbox.ddipboxName}의 재고를 관리합니다.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* 현재 재고 상태 */}
          <div className="bg-muted rounded-lg p-4">
            <div className="mb-2 flex items-center justify-between">
              <h3 className="font-medium">현재 재고 상태</h3>
              <Badge variant={currentStatus.color}>{currentStatus.text}</Badge>
            </div>
            <div className="text-muted-foreground flex items-center gap-4 text-sm">
              <div className="flex items-center gap-1">
                <Package className="h-4 w-4" />
                <span>
                  남은 재고:{' '}
                  <span className="text-foreground font-medium">
                    {ddipbox.remainingQuantity}
                  </span>
                  개
                </span>
              </div>
              <div className="flex items-center gap-1">
                <Hash className="h-4 w-4" />
                <span>
                  일일 수량:{' '}
                  <span className="text-foreground font-medium">
                    {ddipbox.dailyQuantity}
                  </span>
                  개
                </span>
              </div>
            </div>
          </div>

          {/* 업데이트 방식 선택 */}
          <div className="space-y-4">
            <Label className="text-base font-medium">업데이트 방식</Label>
            <RadioGroup
              value={operationType}
              onValueChange={(value: 'direct' | 'reset') =>
                setOperationType(value)
              }
              className="space-y-3"
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="direct" id="direct" />
                <Label htmlFor="direct" className="flex-1">
                  <div className="font-medium">직접 재고 수정</div>
                  <div className="text-muted-foreground text-sm">
                    현재 남은 재고 수량을 직접 변경합니다
                  </div>
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="reset" id="reset" />
                <Label htmlFor="reset" className="flex-1">
                  <div className="font-medium">일일 수량으로 재고 리셋</div>
                  <div className="text-muted-foreground text-sm">
                    일일 수량을 변경하고 재고를 해당 수량으로 리셋합니다
                  </div>
                </Label>
              </div>
            </RadioGroup>
          </div>

          <Separator />

          {/* 입력 폼 */}
          <div className="space-y-4">
            {operationType === 'direct' ? (
              <div className="space-y-2">
                <Label htmlFor="remainingQuantity">
                  남은 재고 수량 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <Package className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="remainingQuantity"
                    type="number"
                    placeholder="0"
                    value={remainingQuantity || ''}
                    onChange={e =>
                      setRemainingQuantity(Number(e.target.value) || 0)
                    }
                    className="pl-10"
                    min="0"
                  />
                </div>
                <div className="text-muted-foreground text-xs">
                  현재: {ddipbox.remainingQuantity}개 → 변경 후:{' '}
                  {remainingQuantity}개
                </div>
              </div>
            ) : (
              <div className="space-y-2">
                <Label htmlFor="dailyQuantity">
                  일일 수량 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <Hash className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="dailyQuantity"
                    type="number"
                    placeholder="1"
                    value={dailyQuantity || ''}
                    onChange={e =>
                      setDailyQuantity(Number(e.target.value) || 1)
                    }
                    className="pl-10"
                    min="1"
                  />
                </div>
                <div className="text-muted-foreground text-xs">
                  현재: {ddipbox.dailyQuantity}개 → 변경 후: {dailyQuantity}개
                  (재고도 {dailyQuantity}개로 리셋됨)
                </div>
              </div>
            )}
          </div>

          {/* 변경 후 예상 상태 */}
          <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
            <div className="mb-2 flex items-center justify-between">
              <h4 className="font-medium text-blue-800">변경 후 예상 상태</h4>
              <Badge variant={previewStatus.color}>{previewStatus.text}</Badge>
            </div>
            <div className="flex items-center gap-4 text-sm text-blue-700">
              {operationType === 'direct' ? (
                <>
                  <div>
                    남은 재고:{' '}
                    <span className="font-medium">{remainingQuantity}</span>개
                  </div>
                  <div>
                    일일 수량:{' '}
                    <span className="font-medium">{ddipbox.dailyQuantity}</span>
                    개
                  </div>
                </>
              ) : (
                <>
                  <div>
                    남은 재고:{' '}
                    <span className="font-medium">{dailyQuantity}</span>개
                  </div>
                  <div>
                    일일 수량:{' '}
                    <span className="font-medium">{dailyQuantity}</span>개
                  </div>
                </>
              )}
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={updateQuantityMutation.isPending}
          >
            취소
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={updateQuantityMutation.isPending}
          >
            {updateQuantityMutation.isPending ? (
              '업데이트 중...'
            ) : operationType === 'direct' ? (
              <>
                <Package className="mr-2 h-4 w-4" />
                재고 수정
              </>
            ) : (
              <>
                <RotateCcw className="mr-2 h-4 w-4" />
                재고 리셋
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default DdipboxQuantityDialog;
