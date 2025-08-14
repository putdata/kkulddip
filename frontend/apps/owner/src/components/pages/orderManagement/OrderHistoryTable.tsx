import { CheckCircle } from 'lucide-react';
import type { Order } from '@/types/order';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { formatDateTime } from '@/utils/dateUtils';
import { useOrderHistoryTable } from '@/hooks/useOrderHistoryTable';
import EmptyOrderHistory from './EmptyOrderHistory';
import PickupStatusButton from './PickupStatusButton';

interface OrderHistoryTableProps {
  orders: Order[];
  onPickupClick?: (order: Order) => void;
}

const OrderHistoryTable = ({
  orders,
  onPickupClick,
}: OrderHistoryTableProps) => {
  const {
    processingIds,
    completedIds,
    handlePickupClick,
    getOrderItemsText,
    getTotalQuantity,
  } = useOrderHistoryTable(onPickupClick);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            확정됨
          </Badge>
        );
      case 'PICKED_UP':
        return (
          <Badge variant="secondary" className="bg-green-100 text-green-800">
            픽업완료
          </Badge>
        );
      case 'CANCELLED':
        return (
          <Badge variant="secondary" className="bg-red-100 text-red-800">
            취소됨
          </Badge>
        );
      case 'FAILED':
        return (
          <Badge variant="secondary" className="bg-red-100 text-red-800">
            실패
          </Badge>
        );
      default:
        return (
          <Badge variant="secondary" className="bg-gray-100 text-gray-800">
            {status}
          </Badge>
        );
    }
  };

  if (orders.length === 0) {
    return <EmptyOrderHistory />;
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[140px]">주문번호</TableHead>
            <TableHead>고객정보</TableHead>
            <TableHead>주문상품</TableHead>
            <TableHead className="text-center">수량</TableHead>
            <TableHead className="text-right">금액</TableHead>
            <TableHead className="text-center">상태</TableHead>
            <TableHead>주문일시</TableHead>
            <TableHead>픽업시간</TableHead>
            <TableHead className="text-center">액션</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {orders.map(order => (
            <TableRow key={order.orderId}>
              <TableCell className="font-mono text-sm">
                {order.orderId.slice(-8)}
              </TableCell>
              <TableCell>
                <div className="flex flex-col">
                  <span className="font-medium">
                    {order.customerName || `고객 #${order.customerId}`}
                  </span>
                  {order.customerName && (
                    <span className="text-muted-foreground text-xs">
                      ID: {order.customerId}
                    </span>
                  )}
                </div>
              </TableCell>
              <TableCell>
                <div className="max-w-[200px]">
                  <span className="text-sm">
                    {getOrderItemsText(order.orderItems)}
                  </span>
                </div>
              </TableCell>
              <TableCell className="text-center">
                {getTotalQuantity(order.orderItems)}개
              </TableCell>
              <TableCell className="text-right font-medium">
                {order.originalPrice.toLocaleString()}원
              </TableCell>
              <TableCell className="text-center">
                {getStatusBadge(order.orderStatus)}
              </TableCell>
              <TableCell className="text-muted-foreground text-sm">
                {formatDateTime(order.orderDate)}
              </TableCell>
              <TableCell className="text-sm">
                {order.pickupTime ? (
                  <div className="flex items-center gap-1 text-green-600">
                    <CheckCircle className="h-3 w-3" />
                    {formatDateTime(order.pickupTime)}
                  </div>
                ) : (
                  <span className="text-muted-foreground">-</span>
                )}
              </TableCell>
              <TableCell className="text-center">
                <PickupStatusButton
                  order={order}
                  isProcessing={processingIds.has(order.orderId)}
                  isCompleted={completedIds.has(order.orderId)}
                  onPickupClick={handlePickupClick}
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default OrderHistoryTable;
