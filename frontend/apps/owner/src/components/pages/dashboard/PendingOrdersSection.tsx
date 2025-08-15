import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Clock, AlertCircle } from 'lucide-react';
import { usePendingOrders } from '@/queries/order';
import type { Order } from '@/types/order';

interface PendingOrdersSectionProps {
  storeId: number;
}

const PendingOrdersSection = ({ storeId }: PendingOrdersSectionProps) => {
  const { data: ordersData, isLoading, error } = usePendingOrders(storeId);

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-orange-500" />
            대기 중인 주문
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[1, 2, 3].map(i => (
              <div key={i} className="animate-pulse">
                <div className="h-16 rounded-lg bg-gray-200"></div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-orange-500" />
            대기 중인 주문
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <AlertCircle className="mx-auto mb-2 h-8 w-8 text-red-500" />
              <p>주문 데이터를 불러올 수 없습니다</p>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  const pendingOrders = ordersData || [];

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Clock className="h-5 w-5 text-orange-500" />
          대기 중인 주문
          <Badge variant="secondary" className="ml-auto">
            {pendingOrders.length}건
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent className="h-full">
        {pendingOrders.length > 0 ? (
          <div className="space-y-3">
            {pendingOrders.slice(0, 4).map((order: Order, index: number) => (
              <div
                key={order.orderId}
                className="flex items-center justify-between rounded-lg border bg-orange-50 p-3 transition-colors hover:bg-orange-100"
              >
                <div className="flex items-center space-x-3">
                  <div className="flex h-6 w-6 items-center justify-center rounded-full bg-orange-500 text-xs font-bold text-white">
                    {index + 1}
                  </div>
                  <div>
                    <div className="font-medium">주문 #{order.orderId}</div>
                    <div className="text-muted-foreground text-sm">
                      {order.orderItems?.length}개 상품
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="font-semibold text-orange-600">
                    ₩{order.originalPrice?.toLocaleString()}
                  </div>
                  <div className="text-muted-foreground text-xs">
                    {new Date(order.orderDate).toLocaleTimeString('ko-KR', {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </div>
                </div>
              </div>
            ))}
            {pendingOrders.length > 4 && (
              <div className="py-2 text-center">
                <Badge
                  variant="outline"
                  className="bg-orange-100 text-orange-600"
                >
                  +{pendingOrders.length - 4}개 더 있음
                </Badge>
              </div>
            )}
          </div>
        ) : (
          <div className="flex h-full items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <Clock className="mx-auto mb-2 h-8 w-8" />
              <p>대기 중인 주문이 없습니다</p>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default PendingOrdersSection;
