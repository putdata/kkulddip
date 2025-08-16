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
import { CheckCircle, Clock, Package, Loader2 } from 'lucide-react';
import { useStoreOrderHistory } from '@/queries/order';
import { usePickupOrder } from '@/queries/order/usePickupOrder';
import { useOrderHistoryTable } from '@/hooks/useOrderHistoryTable';
import type { Order } from '@/types/order';

interface PickupReadySectionProps {
  storeId: number;
}

const PickupReadySection = ({ storeId }: PickupReadySectionProps) => {
  const { data: ordersData, isLoading, error } = useStoreOrderHistory(storeId);
  const pickupOrderMutation = usePickupOrder(storeId);

  // Filter only CONFIRMED orders ready for pickup
  const confirmedOrders =
    ordersData?.filter(order => order.orderStatus === 'CONFIRMED') || [];

  const {
    processingIds,
    completedIds,
    handlePickupClick,
    handlePickupSuccess,
    handlePickupError,
  } = useOrderHistoryTable({
    onPickupClick: (order: Order) => {
      pickupOrderMutation.mutate(order.orderId, {
        onSuccess: () => handlePickupSuccess(order.orderId),
        onError: () => handlePickupError(order.orderId),
      });
    },
  });

  if (isLoading) {
    return (
      <Card className="h-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Package className="h-5 w-5 text-blue-500" />
            픽업 대기
            <div className="ml-auto h-5 w-8 animate-pulse rounded bg-gray-200"></div>
          </CardTitle>
        </CardHeader>
        <CardContent className="h-full p-0">
          <div className="overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="text-center">주문번호</TableHead>
                  <TableHead className="text-center">상품수</TableHead>
                  <TableHead className="text-center">금액</TableHead>
                  <TableHead className="text-center">픽업시간</TableHead>
                  <TableHead className="text-center">액션</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {[1, 2, 3, 4, 5].map(i => (
                  <TableRow key={i}>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-16 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-8 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-16 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="mx-auto h-4 w-12 animate-pulse rounded bg-gray-200"></div>
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="flex justify-center">
                        <div className="h-7 w-16 animate-pulse rounded bg-gray-200"></div>
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
            <Package className="h-5 w-5 text-blue-500" />
            픽업 대기
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <Package className="mx-auto mb-2 h-8 w-8 text-red-500" />
              <p>주문 데이터를 불러올 수 없습니다</p>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Package className="h-5 w-5 text-blue-500" />
          픽업 대기
          <Badge variant="secondary" className="ml-auto">
            {confirmedOrders.length}건
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent className="h-full p-0">
        {confirmedOrders.length > 0 ? (
          <div className="overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="text-center">주문번호</TableHead>
                  <TableHead className="text-center">상품수</TableHead>
                  <TableHead className="text-center">금액</TableHead>
                  <TableHead className="text-center">픽업시간</TableHead>
                  <TableHead className="text-center">액션</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {confirmedOrders.slice(0, 5).map((order: Order) => (
                  <TableRow key={order.orderId}>
                    <TableCell className="text-center font-medium">
                      {order.orderId.slice(-6)}
                    </TableCell>
                    <TableCell className="text-center">
                      {order.orderItems?.length}개
                    </TableCell>
                    <TableCell className="text-center font-semibold">
                      ₩{order.originalPrice?.toLocaleString()}
                    </TableCell>
                    <TableCell className="text-muted-foreground text-center text-sm">
                      {order.pickupTime ? (
                        <div className="flex items-center justify-center gap-1">
                          <Clock className="h-3 w-3" />
                          {new Date(order.pickupTime).toLocaleTimeString(
                            'ko-KR',
                            {
                              hour: '2-digit',
                              minute: '2-digit',
                            },
                          )}
                        </div>
                      ) : (
                        '-'
                      )}
                    </TableCell>
                    <TableCell className="text-center">
                      <div className="flex justify-center">
                        <Button
                          size="sm"
                          onClick={() => handlePickupClick(order)}
                          disabled={
                            processingIds.has(order.orderId) ||
                            pickupOrderMutation.isPending
                          }
                          className="h-7 px-3 text-xs"
                        >
                          {processingIds.has(order.orderId) ? (
                            <Loader2 className="h-3 w-3 animate-spin" />
                          ) : completedIds.has(order.orderId) ? (
                            <>
                              <CheckCircle className="mr-1 h-3 w-3 text-green-600" />
                              완료
                            </>
                          ) : (
                            <>
                              <CheckCircle className="mr-1 h-3 w-3" />
                              완료
                            </>
                          )}
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
            {confirmedOrders.length > 5 && (
              <div className="border-t p-2 text-center">
                <Badge variant="outline">
                  +{confirmedOrders.length - 5}개 더 있음
                </Badge>
              </div>
            )}
          </div>
        ) : (
          <div className="flex h-full items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <Package className="mx-auto mb-2 h-8 w-8" />
              <p>픽업 대기 중인 주문이 없습니다</p>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default PickupReadySection;
