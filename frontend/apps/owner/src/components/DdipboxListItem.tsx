import { useState } from 'react';
import {
  MoreHorizontal,
  Edit,
  Trash2,
  Package,
  Eye,
  EyeOff,
  RefreshCw,
} from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { useToggleDdipboxStatus } from '@/queries/ddipbox';
import EditDdipboxDialog from './EditDdipboxDialog';
import DeleteDdipboxDialog from './DeleteDdipboxDialog';
import DdipboxQuantityDialog from './DdipboxQuantityDialog';
import type { DdipBox } from '@/types/ddipbox';

interface DdipboxListItemProps {
  ddipbox: DdipBox;
  storeId: number;
}

const DdipboxListItem = ({ ddipbox, storeId }: DdipboxListItemProps) => {
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isQuantityDialogOpen, setIsQuantityDialogOpen] = useState(false);

  const toggleStatusMutation = useToggleDdipboxStatus();

  const handleToggleStatus = () => {
    toggleStatusMutation.mutate({
      storeId,
      ddipboxId: ddipbox.ddipboxId,
      data: {
        isActive: !ddipbox.isActive,
        reason: ddipbox.isActive ? '일시 중단' : '판매 재개',
      },
    });
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  const getDiscountRate = () => {
    if (ddipbox.originalPrice === 0) {
      return 0;
    }
    return Math.round(
      ((ddipbox.originalPrice - ddipbox.salePrice) / ddipbox.originalPrice) *
        100,
    );
  };

  const getStockStatus = () => {
    const percentage =
      (ddipbox.remainingQuantity / ddipbox.dailyQuantity) * 100;
    if (percentage === 0) {
      return { status: 'out', color: 'destructive' as const, text: '품절' };
    }
    if (percentage <= 20) {
      return { status: 'low', color: 'destructive' as const, text: '부족' };
    }
    if (percentage <= 50) {
      return { status: 'medium', color: 'outline' as const, text: '보통' };
    }
    return { status: 'high', color: 'secondary' as const, text: '충분' };
  };

  const stockStatus = getStockStatus();

  return (
    <div className="bg-card rounded-lg border p-6 transition-shadow hover:shadow-md">
      <div className="flex items-start justify-between">
        {/* 메인 정보 */}
        <div className="flex-1 space-y-3">
          <div className="flex items-center gap-3">
            <h3 className="text-lg font-semibold">{ddipbox.ddipboxName}</h3>
            <Badge variant={ddipbox.isActive ? 'default' : 'secondary'}>
              {ddipbox.isActive ? (
                <>
                  <Eye className="mr-1 h-3 w-3" />
                  판매중
                </>
              ) : (
                <>
                  <EyeOff className="mr-1 h-3 w-3" />
                  판매중지
                </>
              )}
            </Badge>
            <Badge variant="outline">{ddipbox.category}</Badge>
          </div>

          {ddipbox.description && (
            <p className="text-muted-foreground text-sm">
              {ddipbox.description}
            </p>
          )}

          {/* 가격 정보 */}
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <span className="text-muted-foreground text-sm line-through">
                {formatPrice(ddipbox.originalPrice)}원
              </span>
              <span className="text-primary text-lg font-bold">
                {formatPrice(ddipbox.salePrice)}원
              </span>
              {getDiscountRate() > 0 && (
                <Badge variant="destructive" className="text-xs">
                  {getDiscountRate()}% 할인
                </Badge>
              )}
            </div>
          </div>

          {/* 재고 및 수량 정보 */}
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2">
              <Package className="text-muted-foreground h-4 w-4" />
              <span className="text-sm">
                재고:{' '}
                <span className="font-medium">{ddipbox.remainingQuantity}</span>
                /{ddipbox.dailyQuantity}개
              </span>
              <Badge variant={stockStatus.color} className="text-xs">
                {stockStatus.text}
              </Badge>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-muted-foreground text-sm">
                고객당 최대:{' '}
                <span className="font-medium">{ddipbox.maxPerCustomer}</span>개
              </span>
            </div>
          </div>
        </div>

        {/* 액션 버튼 */}
        <div className="flex items-center gap-2">
          {/* 활성화 토글 */}
          <div className="flex items-center gap-2">
            <Switch
              checked={ddipbox.isActive}
              onCheckedChange={handleToggleStatus}
              disabled={toggleStatusMutation.isPending}
            />
            <span className="text-muted-foreground text-sm">
              {ddipbox.isActive ? '판매중' : '중지'}
            </span>
          </div>

          {/* 더보기 메뉴 */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setIsEditDialogOpen(true)}>
                <Edit className="mr-2 h-4 w-4" />
                수정
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setIsQuantityDialogOpen(true)}>
                <RefreshCw className="mr-2 h-4 w-4" />
                재고 관리
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem
                onClick={() => setIsDeleteDialogOpen(true)}
                className="text-destructive"
              >
                <Trash2 className="mr-2 h-4 w-4" />
                삭제
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* 다이얼로그들 */}
      <EditDdipboxDialog
        open={isEditDialogOpen}
        onOpenChange={setIsEditDialogOpen}
        ddipbox={ddipbox}
        storeId={storeId}
      />

      <DeleteDdipboxDialog
        open={isDeleteDialogOpen}
        onOpenChange={setIsDeleteDialogOpen}
        ddipbox={ddipbox}
        storeId={storeId}
      />

      <DdipboxQuantityDialog
        open={isQuantityDialogOpen}
        onOpenChange={setIsQuantityDialogOpen}
        ddipbox={ddipbox}
        storeId={storeId}
      />
    </div>
  );
};

export default DdipboxListItem;
