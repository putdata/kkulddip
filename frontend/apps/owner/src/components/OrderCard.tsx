import { useState } from 'react';
import { Clock, Package, User } from 'lucide-react';
import type { Order } from '@/types/order';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import OrderConfirmDialog from './OrderConfirmDialog';

interface OrderCardProps {
  order: Order;
  storeId: number;
}

const OrderCard = ({ order, storeId }: OrderCardProps) => {
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      month: 'numeric',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      hour12: true,
    });
  };

  const totalQuantity = order.orderItems.reduce(
    (sum, item) => sum + item.quantity,
    0,
  );

  return (
    <>
      <Card className="hover:shadow-lg transition-shadow">
        <CardHeader>
          <div className="flex items-start justify-between">
            <div>
              <CardTitle className="text-lg">
                주문번호: {order.orderId.slice(0, 8)}
              </CardTitle>
              <CardDescription className="mt-1">
                <div className="flex items-center gap-1">
                  <Clock className="h-3 w-3" />
                  {formatDate(order.orderDate)}
                </div>
              </CardDescription>
            </div>
            <Badge variant="secondary" className="bg-yellow-100 text-yellow-800">
              대기중
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 주문 상품 목록 */}
          <div className="space-y-2">
            <div className="text-sm font-medium text-muted-foreground">
              주문 상품
            </div>
            <div className="space-y-1">
              {order.orderItems.map((item, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between text-sm"
                >
                  <span>
                    {item.productName} x {item.quantity}
                  </span>
                  <span className="font-medium">
                    {(item.unitPrice * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* 주문 정보 */}
          <div className="space-y-2 border-t pt-3">
            <div className="flex items-center justify-between text-sm">
              <span className="flex items-center gap-1 text-muted-foreground">
                <Package className="h-3 w-3" />
                총 수량
              </span>
              <span className="font-medium">{totalQuantity}개</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-1 text-sm text-muted-foreground">
                <User className="h-3 w-3" />
                고객 ID
              </span>
              <span className="text-sm font-medium">#{order.customerId}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium">총 금액</span>
              <span className="text-lg font-bold text-primary">
                {order.originalPrice.toLocaleString()}원
              </span>
            </div>
          </div>

          {/* 액션 버튼 */}
          <div className="flex gap-2 pt-2">
            <Button
              className="flex-1"
              variant="outline"
              onClick={() => setShowConfirmDialog(true)}
            >
              주문 거절
            </Button>
            <Button
              className="flex-1"
              onClick={() => setShowConfirmDialog(true)}
            >
              주문 확정
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* 주문 확정/거절 다이얼로그 */}
      {showConfirmDialog && (
        <OrderConfirmDialog
          order={order}
          storeId={storeId}
          open={showConfirmDialog}
          onOpenChange={setShowConfirmDialog}
        />
      )}
    </>
  );
};

export default OrderCard;