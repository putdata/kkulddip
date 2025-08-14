import { Package, Eye, EyeOff, Edit, RefreshCw, Trash2 } from 'lucide-react';
import type { DdipBox } from '@/types/ddipbox';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { useToggleDdipboxStatus } from '@/queries/ddipbox';
import { useDdipboxPricing } from '@/hooks/useDdipboxPricing';

interface DdipboxCardProps {
  ddipbox: DdipBox;
  storeId: number;
  onEditClick: () => void;
  onDeleteClick: () => void;
  onQuantityClick: () => void;
}

const DdipboxCard = ({
  ddipbox,
  storeId,
  onEditClick,
  onDeleteClick,
  onQuantityClick,
}: DdipboxCardProps) => {
  const toggleStatusMutation = useToggleDdipboxStatus();
  const { getDdipboxCalculatedInfo } = useDdipboxPricing();

  const {
    discountRate,
    stockStatus,
    formattedOriginalPrice,
    formattedSalePrice,
    hasDiscount,
  } = getDdipboxCalculatedInfo(ddipbox);

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

  return (
    <Card className="transition-shadow hover:shadow-lg">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="min-w-0 flex-1">
            <CardTitle className="break-all text-lg">
              {ddipbox.ddipboxName}
            </CardTitle>
            <CardDescription className="mt-1 flex items-center gap-2">
              <Badge variant="outline">{ddipbox.category}</Badge>
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
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <Switch
              checked={ddipbox.isActive}
              onCheckedChange={handleToggleStatus}
              disabled={toggleStatusMutation.isPending}
            />
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {ddipbox.description && (
          <p className="text-muted-foreground text-sm">{ddipbox.description}</p>
        )}

        {/* 가격 정보 */}
        <div className="space-y-2">
          <div className="text-muted-foreground text-sm font-medium">가격</div>
          <div className="flex items-center gap-2">
            <span className="text-muted-foreground text-sm line-through">
              {formattedOriginalPrice}원
            </span>
            <span className="text-primary text-lg font-bold">
              {formattedSalePrice}원
            </span>
            {hasDiscount && (
              <Badge variant="destructive" className="text-xs">
                {discountRate}% 할인
              </Badge>
            )}
          </div>
        </div>

        {/* 재고 및 수량 정보 */}
        <div className="space-y-2 border-t pt-3">
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground flex items-center gap-1">
              <Package className="h-3 w-3" />
              재고
            </span>
            <div className="flex items-center gap-2">
              <span className="font-medium">
                {ddipbox.remainingQuantity}/{ddipbox.dailyQuantity}개
              </span>
              <Badge variant={stockStatus.color} className="text-xs">
                {stockStatus.text}
              </Badge>
            </div>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">고객당 최대</span>
            <span className="font-medium">{ddipbox.maxPerCustomer}개</span>
          </div>
        </div>
      </CardContent>

      <CardFooter className="flex gap-2">
        <Button
          className="flex-1"
          variant="outline"
          size="sm"
          onClick={onEditClick}
        >
          <Edit className="mr-1 h-3 w-3" />
          수정
        </Button>
        <Button
          className="flex-1"
          variant="outline"
          size="sm"
          onClick={onQuantityClick}
        >
          <RefreshCw className="mr-1 h-3 w-3" />
          재고
        </Button>
        <Button
          className="flex-1"
          variant="outline"
          size="sm"
          onClick={onDeleteClick}
        >
          <Trash2 className="mr-1 h-3 w-3" />
          삭제
        </Button>
      </CardFooter>
    </Card>
  );
};

export default DdipboxCard;
