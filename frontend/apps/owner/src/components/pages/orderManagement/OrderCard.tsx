import { Clock, Package, User } from 'lucide-react';
import type { Order } from '@/types/order';
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
import { formatDateTime } from '@/utils/dateUtils';

interface OrderCardProps {
  order: Order;
  onAcceptClick: () => void;
  onRejectClick: () => void;
}

const OrderCard = ({ order, onAcceptClick, onRejectClick }: OrderCardProps) => {

  const totalQuantity = order.orderItems.reduce(
    (sum, item) => sum + item.quantity,
    0,
  );

  return (
    <>
      <Card className="transition-shadow hover:shadow-lg">
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="min-w-0 flex-1">
              <CardTitle className="break-all text-base">
                주문번호: {order.orderId}
              </CardTitle>
              <CardDescription className="mt-1 flex items-center gap-1">
                <Clock className="h-3 w-3" />
                {formatDateTime(order.orderDate)}
              </CardDescription>
            </div>
            <Badge
              variant="secondary"
              className="bg-yellow-100 text-yellow-800"
            >
              대기중
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <div className="text-muted-foreground text-sm font-medium">
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

          <div className="space-y-2 border-t pt-3">
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground flex items-center gap-1">
                <Package className="h-3 w-3" />총 수량
              </span>
              <span className="font-medium">{totalQuantity}개</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-muted-foreground flex items-center gap-1 text-sm">
                <User className="h-3 w-3" />
                고객 ID
              </span>
              <span className="text-sm font-medium">#{order.customerId}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium">총 금액</span>
              <span className="text-primary text-lg font-bold">
                {order.originalPrice.toLocaleString()}원
              </span>
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex gap-2">
          <Button
            className="flex-1"
            variant="outline"
            onClick={onRejectClick}
          >
            주문 거절
          </Button>
          <Button
            className="flex-1"
            onClick={onAcceptClick}
          >
            주문 확정
          </Button>
        </CardFooter>
      </Card>
    </>
  );
};

export default OrderCard;
