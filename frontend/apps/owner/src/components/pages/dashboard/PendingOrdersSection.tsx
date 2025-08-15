import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Clock, AlertCircle, Check, X } from 'lucide-react';
import { usePendingOrders } from '@/queries/order';
import OrderAcceptDialog from '@/components/pages/orderManagement/OrderAcceptDialog';
import OrderRejectDialog from '@/components/pages/orderManagement/OrderRejectDialog';
import type { Order } from '@/types/order';

interface PendingOrdersSectionProps {
  storeId: number;
}

const PendingOrdersSection = ({ storeId }: PendingOrdersSectionProps) => {
  const { data: ordersData, isLoading, error } = usePendingOrders(storeId);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showAcceptDialog, setShowAcceptDialog] = useState(false);
  const [showRejectDialog, setShowRejectDialog] = useState(false);

  const handleAcceptOrder = (order: Order) => {
    setSelectedOrder(order);
    setShowAcceptDialog(true);
  };

  const handleRejectOrder = (order: Order) => {
    setSelectedOrder(order);
    setShowRejectDialog(true);
  };

  if (isLoading) {
    return (
      <Card className="h-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-orange-500" />
            대기 중인 주문
            <div className="ml-auto h-5 w-8 animate-pulse rounded bg-gray-200"></div>
          </CardTitle>
        </CardHeader>
        <CardContent className="h-full p-0">
          <div className="overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>주문번호</TableHead>
                  <TableHead className="text-center">상품수</TableHead>
                  <TableHead className="text-right">금액</TableHead>
                  <TableHead className="text-center">시간</TableHead>
                  <TableHead className="text-center">액션</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {[1, 2, 3, 4, 5].map(i => (
                  <TableRow key={i}>
                    <TableCell>
                      <div className="h-4 w-16 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-8 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="ml-auto h-4 w-16 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-10 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="flex gap-1">
                        <div className="h-7 w-7 animate-pulse rounded bg-gray-200"></div>
                        <div className="h-7 w-7 animate-pulse rounded bg-gray-200"></div>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
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
      <CardContent className="h-full p-0">
        {pendingOrders.length > 0 ? (
          <div className="overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>주문번호</TableHead>
                  <TableHead className="text-center">상품수</TableHead>
                  <TableHead className="text-right">금액</TableHead>
                  <TableHead className="text-center">시간</TableHead>
                  <TableHead className="text-center">액션</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {pendingOrders.slice(0, 5).map((order: Order) => (
                  <TableRow key={order.orderId}>
                    <TableCell className="font-medium">
                      {order.orderId.slice(-6)}
                    </TableCell>
                    <TableCell className="text-center">
                      {order.orderItems?.length}개
                    </TableCell>
                    <TableCell className="text-right font-semibold">
                      ₩{order.originalPrice?.toLocaleString()}
                    </TableCell>
                    <TableCell className="text-muted-foreground text-center text-sm">
                      {new Date(order.orderDate).toLocaleTimeString('ko-KR', {
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="flex gap-1">
                        <Button
                          size="sm"
                          onClick={() => handleAcceptOrder(order)}
                          className="h-7 px-2 text-xs"
                        >
                          <Check className="h-3 w-3" />
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          onClick={() => handleRejectOrder(order)}
                          className="h-7 px-2 text-xs"
                        >
                          <X className="h-3 w-3" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
            {pendingOrders.length > 5 && (
              <div className="border-t p-2 text-center">
                <Badge variant="outline">
                  +{pendingOrders.length - 5}개 더 있음
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

      {/* Order Action Dialogs */}
      {selectedOrder && (
        <>
          <OrderAcceptDialog
            order={selectedOrder}
            storeId={storeId}
            open={showAcceptDialog}
            onOpenChange={setShowAcceptDialog}
          />
          <OrderRejectDialog
            order={selectedOrder}
            storeId={storeId}
            open={showRejectDialog}
            onOpenChange={setShowRejectDialog}
          />
        </>
      )}
    </Card>
  );
};

export default PendingOrdersSection;
