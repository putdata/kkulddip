import { AlertTriangle, Package } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useDeleteDdipbox } from '@/queries/ddipbox';
import type { DdipBox } from '@/types/ddipbox';

interface DeleteDdipboxDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  ddipbox: DdipBox;
  storeId: number;
}

const DeleteDdipboxDialog = ({
  open,
  onOpenChange,
  ddipbox,
  storeId,
}: DeleteDdipboxDialogProps) => {
  const deleteDdipboxMutation = useDeleteDdipbox();

  const handleDelete = () => {
    deleteDdipboxMutation.mutate(
      {
        storeId,
        ddipboxId: ddipbox.ddipboxId,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
        },
      },
    );
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <AlertTriangle className="text-destructive h-5 w-5" />
            띱박스 삭제
          </DialogTitle>
          <DialogDescription>
            이 작업은 되돌릴 수 없습니다. 정말로 삭제하시겠습니까?
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 삭제할 띱박스 정보 */}
          <div className="bg-muted rounded-lg p-4">
            <div className="flex items-start gap-3">
              <div className="bg-background rounded-lg p-2">
                <Package className="h-4 w-4" />
              </div>
              <div className="flex-1 space-y-2">
                <div className="flex items-center gap-2">
                  <h4 className="font-medium">{ddipbox.ddipboxName}</h4>
                  <Badge variant={ddipbox.isActive ? 'default' : 'secondary'}>
                    {ddipbox.isActive ? '판매중' : '판매중지'}
                  </Badge>
                </div>
                <div className="text-muted-foreground text-sm">
                  <div>카테고리: {ddipbox.category}</div>
                  <div>
                    가격: {formatPrice(ddipbox.salePrice)}원
                    {ddipbox.originalPrice !== ddipbox.salePrice && (
                      <span className="text-muted-foreground ml-1">
                        (정가: {formatPrice(ddipbox.originalPrice)}원)
                      </span>
                    )}
                  </div>
                  <div>
                    재고: {ddipbox.remainingQuantity}/{ddipbox.dailyQuantity}개
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* 경고 메시지 */}
          <div className="bg-destructive/10 border-destructive/20 rounded-lg border p-4">
            <div className="flex gap-3">
              <AlertTriangle className="text-destructive mt-0.5 h-4 w-4 flex-shrink-0" />
              <div className="space-y-1 text-sm">
                <div className="text-destructive font-medium">
                  삭제 시 다음과 같은 영향이 있습니다:
                </div>
                <ul className="text-muted-foreground space-y-1">
                  <li>• 고객이 더 이상 이 띱박스를 구매할 수 없습니다</li>
                  <li>
                    • 관련된 주문 내역은 유지되지만 띱박스 정보는 삭제됩니다
                  </li>
                  <li>• 이 작업은 되돌릴 수 없습니다</li>
                </ul>
              </div>
            </div>
          </div>

          {/* 대안 제안 */}
          <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
            <div className="text-sm">
              <div className="mb-1 font-medium text-blue-800">
                💡 대안: 임시로 판매를 중지해보세요
              </div>
              <div className="text-blue-700">
                완전히 삭제하는 대신 판매를 중지하면 나중에 언제든 다시 활성화할
                수 있습니다.
              </div>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={deleteDdipboxMutation.isPending}
          >
            취소
          </Button>
          <Button
            variant="destructive"
            onClick={handleDelete}
            disabled={deleteDdipboxMutation.isPending}
          >
            {deleteDdipboxMutation.isPending ? '삭제 중...' : '삭제'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default DeleteDdipboxDialog;
